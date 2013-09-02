/*
 * This code licensed to public domain
 */
package obix.io;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import obix.*;
import obix.Enum;
import obix.xml.*;

/**
 * ObixDecoder is used to deserialize an XML stream
 * into memory as a tree of Obj instances. 
 *
 * @author    Brian Frank
 * @creation  27 Apr 05
 * @version   $Revision$ $Date$
 */
public class ObixDecoder 
  extends XParser
{

////////////////////////////////////////////////////////////////
// Factory
////////////////////////////////////////////////////////////////
  
  /**
   * Decode an Obj from a String.
   */
  public static Obj fromString(String xml)                 
  {                 
    try
    {
      ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
      ObixDecoder decoder = new ObixDecoder(in);
      return decoder.decodeDocument();      
    }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new RuntimeException(e.toString());
    }
  }
                                             
////////////////////////////////////////////////////////////////
// Constructors
////////////////////////////////////////////////////////////////

  /**
   * Construct for specified input stream.
   */
  public ObixDecoder(InputStream in)
    throws Exception
  {                                
    super(in);
  }

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////
  
  /**
   * Get the useContracts flags.  If this flag is true, then
   * we attempt to map contract attributes to predefined classes
   * using the ContractRegistry.  If the false, then we just
   * map to the built-in classes (Obj, Bool, Int, etc).  The
   * default for this flag is true.
   */
  public boolean getUseContracts()
  {                                                    
    return useContracts;
  }
  
  /**
   * Set the useContracts flag - see getUseContracts() for details.
   */
  public void setUseContracts(boolean useContracts)
  {
    this.useContracts = useContracts;
  }

////////////////////////////////////////////////////////////////
// Document
////////////////////////////////////////////////////////////////

  /**
   * Convenience for <code>decodeDocument(true)</code>.
   */
  public Obj decodeDocument()
    throws Exception
  {                            
    return decodeDocument(true);
  }

  /**
   * Decode the XML document into a Obj, and
   * optionally close the input stream.
   */
  public Obj decodeDocument(boolean close)
    throws Exception
  {
    try
    {
      // parse into memory
      XElem root = parse();
  
      // decode root recursively
      Obj result = decode(null, root, null);
      
      // spit out warning for unresolved frag identifiers
      warningsForUnresolvedFragRefs();
      
      return result;
    }
    finally
    {
      if (close) close();
    }
  }          
  
////////////////////////////////////////////////////////////////
// Object Decoding
////////////////////////////////////////////////////////////////
  
  /**
   * Recursively decode element into Obj instances.
   */
  private Obj decode(Obj parent, XElem x, Contract defaultContract)
    throws Exception
  {                              
    // attribute variables
    String name        = null;
    String val         = null;   
    String href        =  null;   
    String is          = null;    
    String of          = null;    
    String in          = null;
    String out         = null;
    String display     = null;
    String displayName = null;
    String icon        = null;
    String isNull      = null;
    String writable    = null;
    String status      = null;
    String range       = null;    
    String min         = null;
    String max         = null;
    String unit        = null;    
    String precision   = null;
    String tz          = null;
    
    // fill in attributes found
    int attrSize = x.attrSize();
    for(int i=0; i<attrSize; ++i)
    {
      String attrName = x.attrName(i);
      String attrVal  = x.attrValue(i);
      if (attrName.equals("name"))             name  = attrVal;
      else if (attrName.equals("val"))         val   = attrVal;
      else if (attrName.equals("href"))        href  = attrVal;
      else if (attrName.equals("is"))          is    = attrVal;
      else if (attrName.equals("of"))          of    = attrVal;
      else if (attrName.equals("in"))          in    = attrVal;
      else if (attrName.equals("out"))         out   = attrVal;
      else if (attrName.equals("display"))     display   = attrVal;
      else if (attrName.equals("displayName")) displayName = attrVal;
      else if (attrName.equals("null"))        isNull    = attrVal;
      else if (attrName.equals("icon"))        icon      = attrVal;
      else if (attrName.equals("writable"))    writable  = attrVal;
      else if (attrName.equals("status"))      status    = attrVal;
      else if (attrName.equals("range"))       range     = attrVal;
      else if (attrName.equals("min"))         min       = attrVal;
      else if (attrName.equals("max"))         max       = attrVal;
      else if (attrName.equals("unit"))        unit      = attrVal;
      else if (attrName.equals("precision"))   precision = attrVal;
      else if (attrName.equals("tz"))          tz        = attrVal;
    }             
    
    // map element name to an Obj Class (Obj, Bool, Int, etc)
    String elemName = x.name();
    Class cls = Obj.toClass(elemName);
    if (cls == null)
    {
      System.out.println("WARNING: Unknown element: " + x + " [Line " + x.line() + "]");
      return null;
    }
    
    // if we have a contract specified, then parse it
    Contract contract = null;
    if (is != null)
      contract = decodeContract(is, x);   
    
    // if a name was specified, check the parent for
    // an existing default object to use (this happens if
    // the parent was created from a contract list)
    Obj obj = null;
    if (parent != null && name != null)
      obj = parent.get(name);        
    
    // if obj wasn't found in parent then we need do 
    // go thru a process to figure out how to create it
    if (obj == null)
    {                                  
      // if the decoder is configured to use contracts 
      // and we have a contract available, then map
      // the contract to a class (otherwise we fallback
      // to the class we looked up for the elem name)
      if (useContracts)
      {
        if (contract != null) 
          cls = ContractRegistry.toClass(cls, contract);
        else if (defaultContract != null)
          cls = ContractRegistry.toClass(cls, defaultContract);
      }
      
      // instaniate an object from the class
      obj = (Obj)cls.newInstance();
    }   
        

    // If we are using an object from the parent, then
    // let's make sure the element name specified in the document
    // doesn't conflict with the contract's definition.  There
    // are two cases where this is actually ok:
    //   1) using ref to indicate reference to target object
    //   2) covariantly overridden obj to be something else
    // If we detect a mismatch that fits one of those two cases
    // then reallocate obj correctly.        
    //
    // TODO: this code isn't quite perfect, because technically by 
    //   allocating a new instance we might be throwing awaya type 
    //   we mapped from the ContractRegistery or facets the contract 
    //   had declared - but in practice I'm not sure it matters   
    else if (!elemName.equals(obj.getElement()))
    {
      if (elemName.equals("ref") || obj.getElement().equals("obj"))
      { 
        Obj newObj = Obj.toObj(elemName);
        if (newObj != null)
        {                               
          newObj.setName(name);
          if (obj.getParent() != null) obj.getParent().replace(obj, newObj);
          obj = newObj;
        }
      }
      else
      {
        throw err("Element name '" + elemName + "' conflicts with contract element '" + obj.getElement() + "'", x);
      }
    }
    
    // name
    if (name != null && obj.getName() == null)
      obj.setName(name);
            
    // href
    if (href != null)  
    {          
      if (obj instanceof Ref)
      {
        obj.setHref(decodeRefUri(href, x));
      }
      else
      {    
        decodeHref(obj, href);
      }
    }

    // is
    if (contract != null)
      obj.setIs(contract);
    
    // parse value
    if (val != null)       
    {       
      if (isNull == null) obj.setNull(false);
      try
      {
        ((Val)obj).decodeVal(val);
      }
      catch(Exception e)
      {
        throw err("Invalid val attribte '" + val + "' for " + obj.getElement(), x, e);
      }
    }           

    // facets
    if (display != null)     obj.setDisplay(display);
    if (displayName != null) obj.setDisplayName(displayName);
    if (icon != null)        obj.setIcon(new Uri(icon));
    if (status != null)      obj.setStatus(Status.parse(status));
    if (isNull != null)      obj.setNull(isNull.equals("true"));
    if (writable != null)    obj.setWritable(writable.equals("true"));
    
    // meta-data & Type specific facets                                           
    Contract childrenDefaultContract = null;
    if (obj instanceof List)
    {
      List list = (List)obj;
      if (of != null) list.setOf(childrenDefaultContract = decodeContract(of, x));     
      if (min != null) list.setMin(Integer.parseInt(min));     
      if (max != null) list.setMax(Integer.parseInt(max));     
    }
    else if (obj instanceof Op)
    {                    
      Op op = (Op)obj;
      if (in != null)  op.setIn(decodeContract(in, x));     
      if (out != null) op.setOut(decodeContract(out, x));     
    }
    else if (obj instanceof Bool)
    {                         
      Bool b = (Bool)obj; 
      if (range != null) b.setRange(decodeRefUri(range, x));     
    }
    else if (obj instanceof Int)
    {                         
      Int i = (Int)obj; 
      if (min != null) i.setMin(Long.parseLong(min));     
      if (max != null) i.setMax(Long.parseLong(max));     
      if (unit != null) i.setUnit(decodeRefUri(unit, x));     
    }
    else if (obj instanceof Str)
    {                           
      Str s = (Str)obj;
      if (min != null) s.setMin(Integer.parseInt(min));     
      if (max != null) s.setMax(Integer.parseInt(max));     
    }
    else if (obj instanceof Real)
    {                         
      Real r = (Real)obj; 
      if (min != null) r.setMin(Double.parseDouble(min));     
      if (max != null) r.setMax(Double.parseDouble(max));     
      if (unit != null) r.setUnit(decodeRefUri(unit, x));     
      if (precision != null) r.setPrecision(Integer.parseInt(precision));     
    }
    else if (obj instanceof Enum)
    {                         
      Enum e = (Enum)obj; 
      if (range != null) e.setRange(decodeRefUri(range, x));     
    }
    else if (obj instanceof Reltime)
    {                         
      Reltime r = (Reltime)obj; 
      if (min != null) r.setMin(Reltime.parse(min));     
      if (max != null) r.setMax(Reltime.parse(max));     
    }
    else if (obj instanceof Abstime)
    {                         
      Abstime a = (Abstime)obj; 
      if (min != null) a.setMin(Abstime.parse(min));     
      if (max != null) a.setMax(Abstime.parse(max));     
      if (tz != null) a.setTz(tz);
    }
    else if (obj instanceof Time)
    {                         
      Time t = (Time)obj; 
      if (min != null) t.setMin(Time.parse(min));     
      if (max != null) t.setMax(Time.parse(max));     
      if (tz != null)  t.setTz(tz);
    }
    else if (obj instanceof Date)
    {                         
      Date d = (Date)obj; 
      if (min != null) d.setMin(Date.parse(min));     
      if (max != null) d.setMax(Date.parse(max));     
      if (tz != null)  d.setTz(tz);
    }
    else if (obj instanceof Feed)
    {                    
      Feed feed = (Feed)obj;
      if (in != null) feed.setIn(decodeContract(in, x));     
      if (of != null) feed.setOf(decodeContract(of, x));     
    }    
    
    // recurse
    XElem[] kids = x.elems();
    for(int i=0; i<kids.length; ++i)
    {
      Obj kid = decode(obj, kids[i], childrenDefaultContract);
      if (kid != null && kid.getParent() ==  null) 
      {
        try
        {
          obj.add(kid);
        }
        catch(Exception e)
        {
          throw err("Cannot add child '" + name + "'", kids[i], e);
        }
      }
    }
    
    // all done
    return obj;
  }

////////////////////////////////////////////////////////////////
// Hrefs and Frag identifiers
////////////////////////////////////////////////////////////////
  
  /**
   * Parse a uri string which identifies an object's href.
   * If the id indicates a fragment identifier, then save
   * the object away for resolution via decodeRefUri().
   */
  void decodeHref(Obj obj, String href)
  { 
    // set href field
    Uri uri = new Uri(null, href);
    obj.setHref(uri);
    
    // if not fragment then we are done
    if (!uri.isFragment()) return;
    
    // save away in fragments table for 
    // resolution in the decodeRefUri() method
    fragIds.put(href, obj);
    
    // it is possible that we've already parsed objects
    // which reference this fragment href, so check that
    FragRefs refs = (FragRefs)fragRefs.get(href);
    if (refs == null) return;
    
    // set the target object for all the uris which 
    // forwardly referenced this object
    fragRefs.remove(href);
    for (int i=0; i<refs.uris.size(); ++i)
      ((Uri)refs.uris.get(i)).setResolved(obj);
  }  

  /**
   * Parse a string into a Uri instance.  If the Uri references
   * a fragment identifier, then resolve it too.  This method
   * should only be used for Uris which reference something (as
   * opposed to Uris which identify something.
   */
  Uri decodeRefUri(String s, XElem elem)
  {           
    Uri uri = new Uri(s);
    if (!uri.isFragment()) return uri;
    
    // check if we've already parsed the obj to which
    // this fragment id referneces
    Obj resolved = (Obj)fragIds.get(s); 
    if (resolved != null)
    { 
      uri.setResolved(resolved);
      return uri;
    }                           
    
    // if not then this uri is forwardly used (or 
    // maybe missing), so just store it away      
    FragRefs refs = (FragRefs)fragRefs.get(s);
    if (refs == null) fragRefs.put(s, refs = new FragRefs());
    refs.uris.add(uri);
    refs.elems.add(elem);
    
    return uri;
  }

  /**
   * Decode a contract list of Uris which might include
   * fragment identifier references we should immediately
   * resolve.
   */
  Contract decodeContract(String s, XElem elem)
  {                 
    StringTokenizer st = new StringTokenizer(s, " ");
    ArrayList acc = new ArrayList();     
    while(st.hasMoreTokens()) acc.add(decodeRefUri(st.nextToken(), elem));
    return new Contract((Uri[])acc.toArray(new Uri[acc.size()]));
  } 
  
  /**
   * Print a warning for all the unresolved fragment identifiers.
   */                                                            
  void warningsForUnresolvedFragRefs()
  {  
    FragRefs[] unresolved = (FragRefs[])fragRefs.values().toArray(new FragRefs[fragRefs.size()]);
    for (int i=0; i<unresolved.length; ++i)
    {                   
      FragRefs r = unresolved[i];
      for (int j=0; j < r.uris.size(); ++j)
      {
        Uri uri = (Uri)r.uris.get(j);
        XElem elem = (XElem)r.elems.get(j);
        warning("Unresolved fragment reference '" + uri + "'", elem);
      }
    }   
  }
  
  static class FragRefs
  {
    ArrayList uris  = new ArrayList(4);
    ArrayList elems = new ArrayList(4);
  }

////////////////////////////////////////////////////////////////
// Error
////////////////////////////////////////////////////////////////

  XException err(String msg, XElem elem, Throwable cause)
  {
    return new XException(msg, elem, cause);
  }
  
  XException err(String msg, XElem elem)
  {
    return new XException(msg, elem);
  }

  void warning(String msg, XElem elem)
  {                                               
    String line = "";
    if (elem != null) line = " [line " + elem.line() + "]";
    System.out.println("WARNING: " + msg + line);
  }

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  private boolean useContracts = true;
  private HashMap fragIds = new HashMap();  // fragment id -> Obj
  private HashMap fragRefs = new HashMap(); // fragment id -> FragRefs
  
} 
