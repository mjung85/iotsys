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

/**
 * BinObixDecoder is used to deserialize a binary encoded object
 * into memory as a tree of Obj instances.   Unlike ObixDecoder, 
 * this class doesn't support mapping contracts to predefined 
 * classes (see ObixDecoder.getUseContracts).
 *
 * @author    Brian Frank
 * @creation  1 Sep 09
 * @version   $Revision$ $Date$
 */
public class BinObixDecoder 
  extends DataInputStream
{

////////////////////////////////////////////////////////////////
// Factory
////////////////////////////////////////////////////////////////
  
  /**
   * Decode an Obj from a in-memory byte array.
   */
  public static Obj fromBytes(byte[] bytes)                 
  {                 
    try
    {
      ByteArrayInputStream in = new ByteArrayInputStream(bytes);
      BinObixDecoder decoder = new BinObixDecoder(in);
      return decoder.decodeDocument();      
    }
    catch(Exception e)
    {
      throw new RuntimeException(e.toString());
    }
  }
                                             
////////////////////////////////////////////////////////////////
// Constructors
////////////////////////////////////////////////////////////////

  /**
   * Construct for specified input stream.
   */
  public BinObixDecoder(InputStream in)
    throws Exception
  {                                
    super(in);
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
      strTable.clear();
      return decode(readUnsignedByte());      
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
  private Obj decode(int code)
    throws Exception
  {                              
    // create obj type
    Obj obj;
    switch (code & 0x7c) // mask out more and val encoding bits
    {                                         
      case BinObix.OBJ:     obj = new Obj();                         break;
      case BinObix.BOOL:    obj = new Bool(readBoolVal(code));       break;
      case BinObix.INT:     obj = new Int(readIntVal(code));         break;
      case BinObix.REAL:    obj = new Real(readRealVal(code));       break;
      case BinObix.STR:     obj = new Str(readStrVal(code));         break;        
      case BinObix.ENUM:    obj = new Enum(readStrVal(code));        break;
      case BinObix.ABSTIME: obj = readAbstimeVal(code);              break;
      case BinObix.RELTIME: obj = readReltimeVal(code);              break;
      case BinObix.TIME:    obj = readTimeVal(code);                 break;
      case BinObix.DATE:    obj = readDateVal(code);                 break;
      case BinObix.URI:     obj = new Uri(readStrVal(code));         break;        
      case BinObix.LIST:    obj = new List();                        break;
      case BinObix.OP:      obj = new Op();                          break;
      case BinObix.FEED:    obj = new Feed();                        break;
      case BinObix.REF:     obj = new Ref();                         break;
      case BinObix.ERR:     obj = new Err();                         break;
      default:
        throw new IOException("Invalid object code 0x" + Integer.toHexString(code));
    }         
    
    // facets             
    boolean hasChildren = false;
    while ((code & 0x80) != 0)
    {                       
      code = readUnsignedByte();
      switch (code & 0x7c)  // mask out more and val encoding bits
      {
        case BinObix.HAS_CHILDREN: hasChildren = true;                     break;
        case BinObix.NAME:         obj.setName(readStrVal(code));          break;
        case BinObix.HREF:         obj.setHref(new Uri(readStrVal(code))); break;  // TODO: need to handle like ObixDecoder
        case BinObix.IS:           obj.setIs(readContractVal(code));       break;
        case BinObix.STATUS_0:
        case BinObix.STATUS_1:     obj.setStatus(readStatusVal(code));     break;        
        case BinObix.DISPLAY:      obj.setDisplay(readStrVal(code));       break;
        case BinObix.DISPLAY_NAME: obj.setDisplayName(readStrVal(code));   break;
        case BinObix.ICON:         obj.setIcon(new Uri(readStrVal(code))); break;
        case BinObix.NULL:         obj.setNull(readBoolVal(code));         break;
        case BinObix.WRITABLE:     obj.setWritable(readBoolVal(code));     break;
        case BinObix.OF:           readOf(obj, code);                      break;
        case BinObix.IN:           readIn(obj,  code);                     break;
        case BinObix.OUT:          readOut(obj, code);                     break;        
        case BinObix.MIN:          readMin(obj, code);                     break;        
        case BinObix.MAX:          readMax(obj, code);                     break;        
        case BinObix.RANGE:        readRange(obj, code);                   break;        
        case BinObix.UNIT:         readUnit(obj, code);                    break;
        case BinObix.PRECISION:    readPrecision(obj, code);               break;
        case BinObix.TZ:           readTz(obj, code);                      break;
        default:
          throw new IOException("Invalid facet code 0x" + Integer.toHexString(code));
      }
    }     

    // if we have children
    if (hasChildren)
    {                                         
      while (true)
      {
        code = readUnsignedByte();
        if (code == BinObix.CHILDREN_END) break;
        Obj child = decode(code); 
        obj.add(child);
      }
    }
    
    return obj;
  }                    

////////////////////////////////////////////////////////////////
// Facet Setters
////////////////////////////////////////////////////////////////
  
  private void readOf(Obj obj, int code) throws IOException
  {                                    
    Contract c = readContractVal(code);
    switch (obj.getBinCode())
    {
      case BinObix.LIST: ((List)obj).setOf(c); break;
      case BinObix.FEED: ((Feed)obj).setOf(c); break;
      default: throw new IOException("Invalid 'of' facet on " + obj.getElement());
    }
  }

  private void readIn(Obj obj, int code) throws IOException
  {
    Contract c = readContractVal(code);
    switch (obj.getBinCode())
    {
      case BinObix.OP:   ((Op)obj).setIn(c);   break;
      case BinObix.FEED: ((Feed)obj).setIn(c); break;
      default: throw new IOException("Invalid 'in' facet on " + obj.getElement());
    }
  }

  private void readOut(Obj obj, int code) throws IOException
  {
    Contract c = readContractVal(code);
    switch (obj.getBinCode())
    {
      case BinObix.OP:   ((Op)obj).setOut(c);   break;
      default: throw new IOException("Invalid 'out' facet on " + obj.getElement());
    }
  }

  private void readMin(Obj obj, int code) throws IOException
  {
    switch (obj.getBinCode())
    {
      case BinObix.INT:     ((Int)obj).setMin(readIntVal(code));         break;
      case BinObix.REAL:    ((Real)obj).setMin(readRealVal(code));       break;
      case BinObix.STR:     ((Str)obj).setMin((int)readIntVal(code));    break;
      case BinObix.LIST:    ((List)obj).setMin((int)readIntVal(code));   break;
      case BinObix.ABSTIME: ((Abstime)obj).setMin(readAbstimeVal(code)); break;
      case BinObix.RELTIME: ((Reltime)obj).setMin(readReltimeVal(code)); break;
      case BinObix.TIME:    ((Time)obj).setMin(readTimeVal(code));       break;
      case BinObix.DATE:    ((Date)obj).setMin(readDateVal(code));       break;
      default: throw new IOException("Invalid 'min' facet on " + obj.getElement());
    }
  }

  private void readMax(Obj obj, int code) throws IOException
  {
    switch (obj.getBinCode())
    {
      case BinObix.INT:     ((Int)obj).setMax(readIntVal(code));         break;
      case BinObix.REAL:    ((Real)obj).setMax(readRealVal(code));       break;
      case BinObix.STR:     ((Str)obj).setMax((int)readIntVal(code));    break;
      case BinObix.LIST:    ((List)obj).setMax((int)readIntVal(code));   break;
      case BinObix.ABSTIME: ((Abstime)obj).setMax(readAbstimeVal(code)); break;
      case BinObix.RELTIME: ((Reltime)obj).setMax(readReltimeVal(code)); break;
      case BinObix.TIME:    ((Time)obj).setMax(readTimeVal(code));       break;
      case BinObix.DATE:    ((Date)obj).setMax(readDateVal(code));       break;
      default: throw new IOException("Invalid 'max' facet on " + obj.getElement());
    }
  }

  private void readRange(Obj obj, int code) throws IOException
  {                                       
    Uri uri = new Uri(readStrVal(code));
    switch (obj.getBinCode())
    {
      case BinObix.BOOL: ((Bool)obj).setRange(uri);   break;
      case BinObix.ENUM: ((Enum)obj).setRange(uri);   break;
      default: throw new IOException("Invalid 'range' facet on " + obj.getElement());
    }
  }

  private void readUnit(Obj obj, int code) throws IOException
  {                                       
    Uri uri = new Uri(readStrVal(code));
    switch (obj.getBinCode())
    {
      case BinObix.INT:  ((Int)obj).setUnit(uri);   break;
      case BinObix.REAL: ((Real)obj).setUnit(uri);   break;
      default: throw new IOException("Invalid 'unit' facet on " + obj.getElement());
    }
  }

  private void readPrecision(Obj obj, int code) throws IOException
  {                                       
    int prec = (int)readIntVal(code);
    switch (obj.getBinCode())
    {
      case BinObix.REAL: ((Real)obj).setPrecision(prec); break;
      default: throw new IOException("Invalid 'precision' facet on " + obj.getElement());
    }
  }

  private void readTz(Obj obj, int code) throws IOException
  {                                       
    String tz = readStrVal(code);
    switch (obj.getBinCode())
    {
      case BinObix.ABSTIME: ((Abstime)obj).setTz(tz); break;
      case BinObix.TIME:    ((Time)obj).setTz(tz); break;
      case BinObix.DATE:    ((Date)obj).setTz(tz); break;
      default: throw new IOException("Invalid 'tz' facet on " + obj.getElement());
    }
  }

////////////////////////////////////////////////////////////////
// Value Decoding 
////////////////////////////////////////////////////////////////
  
  private boolean readBoolVal(int code)  throws IOException
  {
    switch (code & 0x3)
    {
      case BinObix.BOOL_FALSE: return false;
      case BinObix.BOOL_TRUE:  return true;
      default: throw new IOException("Invalid bool value code 0x" + Integer.toHexString(code));
    }
  }

  private long readIntVal(int code) throws IOException
  {                      
    switch (code & 0x3)
    {
      case BinObix.INT_U1: return readUnsignedByte();
      case BinObix.INT_U2: return readUnsignedShort();
      case BinObix.INT_S4: return readInt();
      case BinObix.INT_S8: return readLong();
      default: throw new IOException("Invalid int value code 0x" + Integer.toHexString(code));
    }
  }

  private double readRealVal(int code) throws IOException
  {                      
    switch (code & 0x3)
    {
      case BinObix.REAL_F4: return readFloat();
      case BinObix.REAL_F8: return readDouble();
      default: throw new IOException("Invalid float value code 0x" + Integer.toHexString(code));
    }
  }

  private Abstime readAbstimeVal(int code) throws IOException
  {                      
    switch (code & 0x3)
    {
      case BinObix.ABSTIME_SEC: return new Abstime(readInt() * 1000L + Abstime.JAVA_2000);
      case BinObix.ABSTIME_NS:  return new Abstime(readLong()/1000000L + Abstime.JAVA_2000);
      default: throw new IOException("Invalid abstime value code 0x" + Integer.toHexString(code));
    }
  }

  private Reltime readReltimeVal(int code) throws IOException
  {                      
    switch (code & 0x3)
    {
      case BinObix.RELTIME_SEC: return new Reltime(readInt() * 1000L);
      case BinObix.RELTIME_NS:  return new Reltime(readLong() / 1000000L);
      default: throw new IOException("Invalid reltime value code 0x" + Integer.toHexString(code));
    }
  }

  private Time readTimeVal(int code) throws IOException
  {                      
    switch (code & 0x3)
    {
      case BinObix.TIME_SEC: return new Time(readInt() * 1000L);
      case BinObix.TIME_NS:  return new Time(readLong() / 1000000L);
      default: throw new IOException("Invalid time value code 0x" + Integer.toHexString(code));
    }
  }                  
  
  private Date readDateVal(int code) throws IOException
  {                      
    switch (code & 0x3)
    {
      case BinObix.DATE_YYMD: return new Date(readUnsignedShort(), readUnsignedByte(), readUnsignedByte());
      default: throw new IOException("Invalid date value code 0x" + Integer.toHexString(code));
    }
  }

  private Contract readContractVal(int code) throws IOException
  {                         
    return new Contract(readStrVal(code));
  }                                   

  private Status readStatusVal(int code) throws IOException
  {                            
    switch (code & 0x7f)
    {
      case BinObix.STATUS_0 | BinObix.STATUS_0_DISABLED:      return Status.disabled;
      case BinObix.STATUS_0 | BinObix.STATUS_0_FAULT:         return Status.fault;
      case BinObix.STATUS_0 | BinObix.STATUS_0_DOWN:          return Status.down;
      case BinObix.STATUS_0 | BinObix.STATUS_0_UNACKED_ALARM: return Status.unackedAlarm;
      case BinObix.STATUS_1 | BinObix.STATUS_1_ALARM:         return Status.alarm;
      case BinObix.STATUS_1 | BinObix.STATUS_1_UNACKED:       return Status.unacked;
      case BinObix.STATUS_1 | BinObix.STATUS_1_OVERRIDDEN:    return Status.overridden;
      default: throw new IOException("Invalid status value code 0x" + Integer.toHexString(code));
    }
  }

  private String readStrVal(int code) throws IOException
  {                          
    switch (code & 0x3)
    {
      case BinObix.STR_UTF8: 
        String str = readStr();
        strTable.add(str);
        return str;
      case BinObix.STR_PREV: 
        int index = readUnsignedShort();
        if (index >= strTable.size())
          throw new IOException("Str table index out of bounds " + index + " >= " + strTable.size());
        return (String)strTable.get(index);
      default: throw new IOException("Invalid str value code 0x" + Integer.toHexString(code));
    }
  }
  
  String readStr() throws IOException
  {
    StringBuffer s = new StringBuffer();
    while (true)
    {
      int c = readUnsignedByte();
      if (c == 0) break;
      s.append((char)c);
    }
    return s.toString();          
  } 

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////
  
  ArrayList strTable = new ArrayList();
} 
