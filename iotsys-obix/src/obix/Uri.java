/*
 * This code licensed to public domain
 */
package obix;      

import java.net.*;
import java.util.*;

/**
 * Uri models a Uniform Resource Identifier
 *
 * @author    Brian Frank
 * @creation  27 Apr 05
 * @version   $Revision$ $Date$
 */
public class Uri
  extends Val
{ 

////////////////////////////////////////////////////////////////
// Construction
////////////////////////////////////////////////////////////////
  
  /**
   * Construct named Uri with specified value.
   */
  public Uri(String name, String val) 
  { 
    super(name);  
    set(val);
  }                 

  /**
   * Construct unnamed Uri with specified value.
   */
  public Uri(String val) 
  { 
    set(val);
  }                 
    
  /**
   * Construct unnamed Uri with value of ""
   */
  public Uri() 
  { 
    set("");
  }

////////////////////////////////////////////////////////////////
// Uri
////////////////////////////////////////////////////////////////

  /**
   * Normalize this uri against the specified absolute
   * base uri to produce an absolute uri.
   */
  public Uri normalize(Uri base)
  {      
	  if(base == null){
		  return null;
	  }

    try
    {                         
      if (val.startsWith("obix:"))
        return this;
       
      base.checkAbsolute();  
      URL url = new URL(new URL(base.val), val);
      return new Uri(url.toString());
    }
    catch(MalformedURLException e)
    {                      
      throw new RuntimeException("Cannot normalize " + base.val + " + " + val);
    }
  }          
  
  /**
   * Return if this uri is a well formed absolute uri
   * including scheme://authority/path
   */
  public boolean isAbsolute()
  {          
    parse();
    return abs;
  }  

  /**
   * Convenience for !isAbsolute()
   */
  public boolean isRelative()
  {             
    parse();
    return !abs;
  }                
  
  /**
   * Throw a IllegalStateException if this uri is not absolute.
   */
  public void checkAbsolute()
  {                     
    parse();
    if (!abs)
      throw new IllegalStateException("Uri is not absolute: " + val);
  }

  /**
   * Return if this uri starts with "#"
   */
  public boolean isFragment()
  {                        
    return val.startsWith("#");
  }              

  /**                                              
   * Get the authority http://host:port/ or null if relative.
   * This is slightly different semanatics than java.net.URL
   * where authority doesn't include the scheme (in this
   * API that is called address).
   */
  public String getAuthority()
  {              
    parse();
    return auth;
  }                   
  
  /**
   * Convenience for new Uri(getAuthority().  This
   * method can only be used on absolute uris.
   */
  public Uri getAuthorityUri()
  {                
    parse();  
    checkAbsolute();           
    return new Uri(auth);
  }                 
  
  /**
   * Get protocol scheme of uri or null if relative.
   */
  public String getScheme()
  {
    parse();
    return scheme;
  }    
  
  /**
   * Return if the specified uri is contained within this
   * this base uri.  Since paths and queries should be
   * treated as opaque, this method really only makes sense 
   * when this uri is an authority uri used for scoping.
   */
  public boolean contains(Uri uri)
  {                
    checkAbsolute();              
    if (uri.isRelative()) return true;
    return uri.val.toLowerCase().startsWith(val.toLowerCase());
  }

  /**                                              
   * Get the host name and port number as an internet
   * address in the format host:port.  If port is -1 then
   * this simply the hostname.  This method equates to
   * java.net.URL.getAuthority() - this API uses the
   * term authority to include scheme name.
   */
  public String getAddress()
  {    
    parse();
    return addr;
  }  
  
  /**                                              
   * Get the host name or null if relative.
   */
  public String getHost()
  {    
    parse();
    return host;
  }  

  /**                                              
   * Get the port number of -1 if undefined.
   */
  public int getPort()
  {              
    parse();
    return port;
  }  

  /**                                              
   * Get the path.
   */
  public String getPath()
  {                    
    parse();
    return path;
  }  

  /**                                              
   * Get the query identified via "?" or null if none.
   */
  public Query getQuery()
  {                    
    parse();
    return query;
  }  

  /**                                              
   * Get the fragment identified via "#" or null if none.
   */
  public String getFragment()
  {                    
    parse();
    return frag;
  }  
    
  /**
   * Get this uri as a java.net.URL.  This method
   * only works if the uri is absolute!
   */                                  
  public URL toURL()
  {              
    checkAbsolute();
    try
    {           
      return new URL(val);
    }
    catch(MalformedURLException e)
    {
      throw new RuntimeException("Invalid url: " + val);
    }
  }            
  
  /**
   * If the path section of this uri is a slash separated
   * hierarchy, then return the parent uri.  Return null
   * if it doesn't make sense for this uri.
   */                                      
  public Uri parent()
  {
    parse();           
    if (path.length() <= 1) return null;
    int lastSlash = path.lastIndexOf('/', path.length()-2);
    if (lastSlash < 0) return null;
    if (lastSlash == 0)
    {
      if (auth != null)
        return new Uri(auth);
      else
        return null;
    }
    else
    {
      if (auth != null)
        return new Uri(auth + path.substring(1, lastSlash));
      else
        return new Uri(path.substring(0, lastSlash));
    }
  }            

////////////////////////////////////////////////////////////////
// Query
////////////////////////////////////////////////////////////////
  
  /**
   * Uri.Query is used to model the name/value pairs
   * of the query section.
   */
  public static class Query
  {                        
    Query(String str) { this.str = str; }
    
    public String[] keys()
    {                 
      return (String[])keys.toArray(new String[keys.size()]);
    }                                                        
    
    public String get(String key, String def)
    {
      String val = (String)map.get(key);
      if (val == null) return def;
      return val;
    }
                              
    public String toString() 
    { 
      return str; 
    }          
    
    ArrayList keys = new ArrayList();
    HashMap map = new HashMap();    
    String str;
  }                    
  
  /**
   * Parse a query string formated as "name=val&name=val...".
   * If null is passed, return null.
   */
  public static Query parseQuery(String str)
  {                                 
    if (str == null) return null;   
    Query q = new Query(str);
    try
    {        
      StringTokenizer st = new StringTokenizer(str, "&");
      while(st.hasMoreTokens())
      {
        String tok = st.nextToken();
        int eq = tok.indexOf('=');   
        String key, val;
        if (eq < 0)
        {              
          key = tok;
          val = "true";
        }
        else
        {
          key = tok.substring(0, eq);
          val = tok.substring(eq+1); 
        }     
        q.keys.add(key);
        q.map.put(key, val);
      }
    }
    catch(Exception e)
    {
    }
    return q;
  }         

  /**
   * Create a new Uri which is ensured to have the specified
   * name/value pair removed from the query.
   */
  public Uri removeQueryParam(String key)
  {                              
    return addQueryParam(key, null);       
  }   
  
  /**
   * Create a new Uri which is ensured to have the specified
   * name/value pair included in the query.
   */
  public Uri addQueryParam(String key, String value)
  {                                        
    StringBuffer newq = new StringBuffer();
    boolean found = false;
    
    Query q = getQuery();                           
    if (q != null)
    {
      String[] keys = q.keys();
      for(int i=0; i<keys.length; ++i)
      {   
        if (keys[i].equals(key))
        {                   
          if (value != null)   
          {
            if (newq.length() > 0) newq.append('&');
            newq.append(key).append('=').append(value);                        
          }
          found = true;
        }
        else
        {                                              
          if (newq.length() > 0) newq.append('&');
          newq.append(keys[i]).append('=').append(q.get(keys[i], null));                        
        }
      }    
    }                      
    
    if (!found && value != null)
    {
      if (newq.length() > 0) newq.append('&');
      newq.append(key).append('=').append(value);                        
    }                    
    
    String result = auth + path.substring(1);
    if (newq.length() > 0) result += "?" + newq;
    return new Uri(result);
  }
    
////////////////////////////////////////////////////////////////
// Parse
////////////////////////////////////////////////////////////////
  
  /**
   * Ensure this uri's components are parsed into field.
   */
  private void parse()
  {                                                     
    try
    { 
      if (parsed) return;
      
      // be nice to work with URI which I believe supports
      // relative URIs, but not available until 1.5
      try
      {
        URL url = new URL(val);
        abs    = true;
        scheme = url.getProtocol();
        addr   = url.getAuthority();
        auth   = scheme + "://" + addr + "/";
        host   = url.getHost();
        port   = url.getPort();
        path   = url.getPath();
        query  = parseQuery(url.getQuery()); 
        frag   = url.getRef();  
      }
      catch(MalformedURLException e)
      {                      
        // must be relative  
        this.abs = false;
        path = val;
        int pound = path.indexOf('#');
        if (pound >= 0)
        {    
          frag = path.substring(pound+1);
          path = path.substring(0, pound);
        }
        int qmark = path.indexOf('?');
        if (qmark >= 0)
        {    
          query = parseQuery(path.substring(qmark+1));
          path  = path.substring(0, qmark);
        }
      }      
      
      parsed = true;
    }
    catch(Exception e)
    {
      throw new RuntimeException("Invalid uri: " + val);
    }   
  }           

////////////////////////////////////////////////////////////////
// Resolved
////////////////////////////////////////////////////////////////
  
  /**
   * Return if this Uri contains a reference to its target Obj.
   */
  public boolean isResolved()
  {
    return resolved != null;
  }  
  
  /**
   * If this Uri is resolved then return the Obj it references.
   * If unresolved then return null.  Resolved Uris are typically
   * used with local internal fragment Uris.
   */
  public Obj getResolved()
  {                                         
    return resolved;
  }

  /**
   * Set the resolved Obj this Uri references.
   */
  public void setResolved(Obj resolved)
  {                           
    this.resolved = resolved;
  }              
  
////////////////////////////////////////////////////////////////
// Val
////////////////////////////////////////////////////////////////

  /**
   * Get value as a string. 
   */
  public String get()
  {
    return val;
  }

  /**
   * Set value. 
   */
  public void set(String val)
  {             
    if (val == null) throw new IllegalArgumentException("val cannot be null");
    this.parsed = false;
    this.val = val;
  }

////////////////////////////////////////////////////////////////
// Val
////////////////////////////////////////////////////////////////

  /**
   * Return "uri".
   */
  public String getElement()
  {
    return "uri";
  }

  /**
   * Return BinObix.URI.
   */
  public int getBinCode()
  {
    return obix.io.BinObix.URI;
  }

  /**
   * Return if specified Val has equivalent uri string value.
   */
  public boolean valEquals(Val that)
  {
    if (that instanceof Uri)
      return ((Uri)that).val.equals(val);
    return false;
  }

  /**
   * Compares this object with the specified object for 
   * order. Returns a negative integer, zero, or a positive 
   * integer as this object is less than, equal to, or greater 
   * than the specified object.
   */
  public int compareTo(Object that)
  {       
    return val.compareTo(((Uri)that).val);
  }

  /**
   * Encode the value as a string
   */
  public String encodeVal()
  {
    return val;
  }

  /**
   * Decode the value from a string.
   */
  public void decodeVal(String val)
    throws Exception
  {
    set(val);
  }

  /**
   * Encode the value as a Java code literal to pass to the constructor.
   */
  public String encodeJava()
  {
    return '"' + val + '"';
  }    
    
////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////
  
  private String val;  
  private boolean parsed;
  private boolean abs;
  private String auth;    
  private String scheme;    
  private String addr;
  private String host;
  private int port = -1;
  private String path;
  private Query query;
  private String frag;
  private Obj resolved;
  
}
