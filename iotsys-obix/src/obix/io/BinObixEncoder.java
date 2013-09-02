/*
 * This code licensed to public domain
 */
package obix.io;

import java.io.*;
import java.util.HashMap;
import obix.*;
import obix.Enum;
import obix.xml.*;

/**
 * BinObixEncoder is used to serialize an Obj tree to 
 * an binary object stream.
 *
 * @author    Brian Frank
 * @creation  1 Sep 09
 * @version   $Revision$ $Date$
 */
public class BinObixEncoder
  extends DataOutputStream
{ 

////////////////////////////////////////////////////////////////
// Factory
////////////////////////////////////////////////////////////////
    
  /**
   * Encode the specified obj to an in memory byte array.
   */
  public static byte[] toBytes(Obj obj)
  {                 
    try
    {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      BinObixEncoder encoder = new BinObixEncoder(out);
      encoder.encode(obj);      
      encoder.flush();
      return out.toByteArray();
    }
    catch(IOException e)
    {
      throw new RuntimeException(e.toString());
    }
  }

////////////////////////////////////////////////////////////////
// Constructors
////////////////////////////////////////////////////////////////

  /**
   * Construct encoder for specified file.
   */
  public BinObixEncoder(File file)
    throws IOException
  {
    this(new BufferedOutputStream(new FileOutputStream(file)));
  }

  /**
   * Construct encoder for specified output stream.
   */
  public BinObixEncoder(OutputStream out)
    throws IOException     
  {             
    super(out);
  }
  
////////////////////////////////////////////////////////////////
// Document
////////////////////////////////////////////////////////////////
  
  /**
   * Encode a full obix binary document including version header.
   */
  public void encodeDocument(Obj obj)
    throws IOException
  {           
    strTable.clear();            
    encode(obj);   
    flush();
  }             
  
  /**
   * Encode an object and it's children.
   */
  public void encode(Obj obj)
    throws IOException
  {                
    // build linked list of object/facet structure for 
    // this object; then serialize to output stream
    boolean hasChildren = obj.size() > 0;
    Node node = toNode(obj, hasChildren);   
    while (node != null)
    {
      node.write(this);
      node = node.next;
    }              
    
    // if we have children, then we set the more bit on 
    // the last node bytecode, so write the children
    if (hasChildren)
    {   
      write(BinObix.HAS_CHILDREN);  
      Obj[] kids = obj.list();
      for(int i=0; i<kids.length; ++i)
        encode(kids[i]);
      write(BinObix.CHILDREN_END);  
    }
  }
  
  private Node toNode(Obj obj, boolean markTailMore)
  {
    Node head = null, tail = null;     
    int objCode = obj.getBinCode();
    switch (objCode)
    {      
      case BinObix.OBJ:
        head = tail = new Node(null, objCode);
        break;

      case BinObix.BOOL:                  
        Bool b = (Bool)obj;
        head = tail = new BoolNode(null, objCode, b.get());
        if (b.getRange() != null) tail = new StrNode(tail, BinObix.RANGE, b.getRange().toString());
        break;

      case BinObix.INT:                  
        Int i = (Int)obj;
        head = tail = new IntNode(null, objCode, i.get());
        if (i.getMin() != Int.MIN_DEFAULT) tail = new IntNode(tail, BinObix.MIN, i.getMin());
        if (i.getMax() != Int.MAX_DEFAULT) tail = new IntNode(tail, BinObix.MAX, i.getMax());
        if (i.getUnit() != null)           tail = new StrNode(tail, BinObix.UNIT, i.getUnit().toString());
        break;

      case BinObix.REAL:                  
        Real f = (Real)obj;
        head = tail = new RealNode(null, objCode, f.get());
        if (f.getMin() != Real.MIN_DEFAULT) tail = new RealNode(tail, BinObix.MIN, f.getMin());
        if (f.getMax() != Real.MAX_DEFAULT) tail = new RealNode(tail, BinObix.MAX, f.getMax());
        if (f.getUnit() != null)            tail = new StrNode(tail, BinObix.UNIT, f.getUnit().toString());
        if (f.getPrecision() != Real.PRECISION_DEFAULT) tail = new IntNode(tail, BinObix.PRECISION, f.getPrecision());
        break;

      case BinObix.STR:                 
        Str s = (Str)obj;
        head = tail = new StrNode(null, objCode, s.get());
        if (s.getMin() != Str.MIN_DEFAULT) tail = new IntNode(tail, BinObix.MIN, s.getMin());
        if (s.getMax() != Str.MAX_DEFAULT) tail = new IntNode(tail, BinObix.MAX, s.getMax());
        break;

      case BinObix.ENUM:                 
        Enum e = (Enum)obj;
        head = tail = new StrNode(null, objCode, e.get());
        if (e.getRange() != null) tail = new StrNode(tail, BinObix.RANGE, e.getRange().toString());
        break;

      case BinObix.ABSTIME:                 
        Abstime a = (Abstime)obj;
        head = tail = new AbstimeNode(null, objCode, a);
        if (a.getMin() != null) tail = new AbstimeNode(tail, BinObix.MIN, a.getMin());
        if (a.getMax() != null) tail = new AbstimeNode(tail, BinObix.MAX, a.getMax());
        if (a.getTz() != null)  tail = new StrNode(tail, BinObix.TZ, a.getTz());
        break;

      case BinObix.RELTIME:                 
        Reltime r = (Reltime)obj;
        head = tail = new ReltimeNode(null, objCode, r);
        if (r.getMin() != null) tail = new ReltimeNode(tail, BinObix.MIN, r.getMin());
        if (r.getMax() != null) tail = new ReltimeNode(tail, BinObix.MAX, r.getMax());
        break;

      case BinObix.TIME:                 
        Time t = (Time)obj;
        head = tail = new TimeNode(null, objCode, t);
        if (t.getMin() != null) tail = new TimeNode(tail, BinObix.MIN, t.getMin());
        if (t.getMax() != null) tail = new TimeNode(tail, BinObix.MAX, t.getMax());
        if (t.getTz() != null)  tail = new StrNode(tail, BinObix.TZ, t.getTz());
        break;
        
      case BinObix.DATE:                 
        Date d = (Date)obj;
        head = tail = new DateNode(null, objCode, d);
        if (d.getMin() != null) tail = new DateNode(tail, BinObix.MIN, d.getMin());
        if (d.getMax() != null) tail = new DateNode(tail, BinObix.MAX, d.getMax());
        if (d.getTz() != null)  tail = new StrNode(tail, BinObix.TZ, d.getTz());
        break;

      case BinObix.URI:                 
        Uri u = (Uri)obj;
        head = tail = new StrNode(null, objCode, u.get());
        break;

      case BinObix.LIST:                 
        List list = (List)obj;
        head = tail = new Node(null, objCode);
        if (!list.getOf().containsOnlyObj())   tail = new ContractNode(tail, BinObix.OF, list.getOf());
        if (list.getMin() != List.MIN_DEFAULT) tail = new IntNode(tail, BinObix.MIN, list.getMin());
        if (list.getMax() != List.MAX_DEFAULT) tail = new IntNode(tail, BinObix.MAX, list.getMax());
        break;
        
      case BinObix.OP:        
        Op op = (Op)obj;
        head = tail = new Node(null, objCode);
        if (!op.getIn().containsOnlyObj())  tail = new ContractNode(tail, BinObix.IN,  op.getIn());
        if (!op.getOut().containsOnlyObj()) tail = new ContractNode(tail, BinObix.OUT, op.getOut());
        break;
               
      case BinObix.FEED:                 
        Feed feed = (Feed)obj;
        head = tail = new Node(null, objCode);
        if (!feed.getIn().containsOnlyObj())  tail = new ContractNode(tail, BinObix.IN, feed.getIn());
        if (!feed.getOf().containsOnlyObj())  tail = new ContractNode(tail, BinObix.OF, feed.getOf());
        break;
        
      case BinObix.REF:                 
      case BinObix.ERR:                 
        head = tail = new Node(null, objCode);
        break;
      
      default: 
        throw new IllegalStateException(obj.getElement());  
    }                 

    // object facets
    if (obj.getName() != null)        tail = new StrNode(tail, BinObix.NAME, obj.getName());
    if (obj.getHref() != null)        tail = new StrNode(tail, BinObix.HREF, obj.getHref().encodeVal());
    if (obj.getIs()   != null)        tail = new ContractNode(tail, BinObix.IS, obj.getIs());
    if (obj.getStatus() != Status.ok) tail = new StatusNode(tail, BinObix.STATUS_0, obj.getStatus());    
    if (obj.getDisplay() != null)     tail = new StrNode(tail, BinObix.DISPLAY, obj.getDisplay());
    if (obj.getDisplayName() != null) tail = new StrNode(tail, BinObix.DISPLAY_NAME, obj.getDisplayName());        
    if (obj.getIcon() != null)        tail = new StrNode(tail, BinObix.ICON, obj.getIcon().encodeVal());        
    if (obj.isNull())                 tail = new BoolNode(tail, BinObix.NULL, true);
    if (obj.isWritable())             tail = new BoolNode(tail, BinObix.WRITABLE, true);
    
    if (markTailMore) tail.code |= BinObix.MORE;
    
    return head;          
  }

////////////////////////////////////////////////////////////////
// Node
////////////////////////////////////////////////////////////////

  static class Node
  {           
    Node(Node tail, int code) 
    { 
      if (tail != null) 
      { 
        tail.code |= BinObix.MORE;
        tail.next = this;
      }
      this.code = code; 
    }      
    
    void write(DataOutputStream out) throws IOException
    {
      out.write(code);
    }
        
    int code;
    Node next;
  }

  static class BoolNode extends Node
  {            
    BoolNode(Node n, int c, boolean v) { super(n,c); val = v; }
    void write(DataOutputStream out) throws IOException
    {           
      if (val)
        out.write(code | BinObix.BOOL_TRUE); 
      else
        out.write(code | BinObix.BOOL_FALSE);
    }
    boolean val;
  }

  static class IntNode extends Node
  {            
    IntNode(Node n, int c, long v) { super(n,c); val = v; }
    void write(DataOutputStream out) throws IOException
    {           
      if (0 <= val && val <= 0xff)
      {
        out.write(code | BinObix.INT_U1);
        out.write((int)val);
      }
      else if (0 <= val && val <= 0xffff)
      {
        out.write(code | BinObix.INT_U2);
        out.writeShort((short)val);
      }
      else if (Integer.MIN_VALUE <= val && val <= Integer.MAX_VALUE)
      {
        out.write(code | BinObix.INT_S4);
        out.writeInt((int)val);
      }
      else
      {
        out.write(code | BinObix.INT_S8);
        out.writeLong(val);
      }
    }
    long val;
  }

  static class RealNode extends Node
  {            
    RealNode(Node n, int c, double v) { super(n,c); val = v; }
    void write(DataOutputStream out) throws IOException
    {           
      out.write(code | BinObix.REAL_F8);
      out.writeDouble(val);
    }   
    double val;
  }

  class StrNode extends Node
  {            
    StrNode(Node n, int c, String v) { super(n,c); val = v; }
    void write(DataOutputStream out) throws IOException
    {           
      Integer index = (Integer)strTable.get(val);
      if (index != null && index.intValue() < 0xffff)
      {
        out.write(code | BinObix.STR_PREV);
        out.writeShort(index.intValue());
      }                    
      else
      {                         
        strTable.put(val, new Integer(strTable.size()));
        out.write(code | BinObix.STR_UTF8);
        writeStr(out, val);
      }
    }
    String val;
  }

  class ContractNode extends StrNode
  {            
    ContractNode(Node n, int c, Contract v) { super(n,c,v.toString()); }
  }

  static class AbstimeNode extends Node
  {            
    AbstimeNode(Node n, int c, Abstime v) { super(n,c); val = v; }
    void write(DataOutputStream out) throws IOException
    {                        
      if (val.getMillisecond() == 0)
      {
        out.write(code | BinObix.ABSTIME_SEC);
        out.writeInt((int)(val.getMillis2000()/1000L));
      }
      else
      {
        out.write(code | BinObix.ABSTIME_NS);
        out.writeLong(val.getMillis2000()*1000000L);
      }
    }
    Abstime val;
  }

  static class ReltimeNode extends Node
  {            
    ReltimeNode(Node n, int c, Reltime v) { super(n,c); val = v; }
    void write(DataOutputStream out) throws IOException
    {           
      long ms = val.get();                 
      if (ms % 1000L == 0)
      {
        out.write(code | BinObix.RELTIME_SEC);
        out.writeInt((int)(ms/1000L));
      }
      else
      {
        out.write(code | BinObix.RELTIME_NS);
        out.writeLong(ms*1000000L);
      }
    }
    Reltime val;
  }

  static class TimeNode extends Node
  {            
    TimeNode(Node n, int c, Time v) { super(n,c); val = v; }
    void write(DataOutputStream out) throws IOException
    {           
      long ms = val.getMillis();                 
      if (ms % 1000L == 0)
      {
        out.write(code | BinObix.TIME_SEC);
        out.writeInt((int)(ms/1000L));
      }
      else
      {
        out.write(code | BinObix.TIME_NS);
        out.writeLong(ms*1000000L);
      }
    }
    Time val;
  }

  static class DateNode extends Node
  {            
    DateNode(Node n, int c, Date v) { super(n,c); val = v; }
    void write(DataOutputStream out) throws IOException
    {           
      out.write(code | BinObix.DATE_YYMD);
      out.writeShort(val.getYear());
      out.write(val.getMonth());
      out.write(val.getDay());
    }
    Date val;
  }

  static class StatusNode extends Node
  {            
    StatusNode(Node n, int c, Status v) { super(n,c); val = v; }
    void write(DataOutputStream out) throws IOException
    {                  
      int x;
           if (val == Status.disabled)     x = BinObix.STATUS_0 | BinObix.STATUS_0_DISABLED;
      else if (val == Status.fault)        x = BinObix.STATUS_0 | BinObix.STATUS_0_FAULT;
      else if (val == Status.down)         x = BinObix.STATUS_0 | BinObix.STATUS_0_DOWN;
      else if (val == Status.unackedAlarm) x = BinObix.STATUS_0 | BinObix.STATUS_0_UNACKED_ALARM;
      else if (val == Status.alarm)        x = BinObix.STATUS_1 | BinObix.STATUS_1_ALARM;
      else if (val == Status.unacked)      x = BinObix.STATUS_1 | BinObix.STATUS_1_UNACKED;
      else if (val == Status.overridden)   x = BinObix.STATUS_1 | BinObix.STATUS_1_OVERRIDDEN;
      else throw new IllegalStateException(val.toString());
      out.write(x);
    }
    Status val;
  }               
  
  static void writeStr(DataOutputStream out, String s) throws IOException
  {
    for (int i=0; i<s.length(); ++i)
    {
      int c = s.charAt(i);
      if (c == 0 || c > 0x7f) throw new IOException("Invalic ASCII string chars: " + s);
      out.write(c);
    }
    out.write(0);
  }

////////////////////////////////////////////////////////////////
// Main
////////////////////////////////////////////////////////////////

  /**
   * Take a XML string as an argument, and output the binary encoding.
   */
  public static void main(String[] args)
    throws Exception
  {       
    if (args.length == 0)
    {
      System.out.println("usage: BinObixEncoder <xml>");
      return;
    }
                   
    System.out.println("=== XML ===");
    String xml = args[0];
    Obj obj = ObixDecoder.fromString(xml);
    obj.dump();
    
    System.out.println();
    System.out.println("=== Binary ===");
    byte[] buf = BinObixEncoder.toBytes(obj);
    for (int i=0; i<buf.length; ++i)
    {
      String hex = Integer.toHexString(buf[i] & 0xff);
      hex = hex.toUpperCase();
      if (hex.length() == 1) hex = "0" + hex;
      System.out.print("  " + hex);
    } 
    System.out.println();
    
    int percent = buf.length * 100 / xml.length();
    
    System.out.println();
    System.out.println("XML:    " + xml.length() + " bytes");
    System.out.println("Binary: " + buf.length + " bytes (" + percent + "% of XML)");
  }           
    
////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////
  
  private int indent;    
  private HashMap strTable = new HashMap(); // String -> Integer
}
