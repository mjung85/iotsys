/*
 * This code licensed to public domain
 */
package obix.test;

import java.io.*;
import obix.*;
import obix.Enum;
import obix.io.*;

/**
 * Test is base class for all Test classes as well as 
 * main entry point for running obix unit tests.
 *
 * @author    Brian Frank
 * @creation  27 Apr 05
 * @version   $Revision$ $Date$
 */
public abstract class Test
{ 

////////////////////////////////////////////////////////////////
// Test List
////////////////////////////////////////////////////////////////

  public static final Test[] tests =
  {
    new TreeTest(),
    new IOTest(),
    new AbstimeTest(),
    new ReltimeTest(),
    new TimeTest(),
    new DateTest(),
    new UriTest(),
    new ContractTest(),
    new FragmentsTest(),
  };

////////////////////////////////////////////////////////////////
// Test
////////////////////////////////////////////////////////////////

  public String getName()
  {
    return getClass().getName();
  }
  
  public abstract void run()
    throws Exception;

////////////////////////////////////////////////////////////////
// Utils
////////////////////////////////////////////////////////////////

  public void verify(boolean condition)
  {
    if (!condition) throw new TestException("Failed verify");
    count++;
  }
  
  public void verify(boolean condition, String msg)
  {
    if (!condition) throw new TestException("Failed verify: " + msg);
    count++;
  }

  public void verify(Object a, Object b)
  {                        
    if (a == null) verify(b == null);
    else if (b == null) verify(a == null);
    else verify(a.equals(b));
  }
  
  public void verify(long a, long b)
  {                      
    verify(a == b);  
  }

  public void verifyEquivalent(Obj a, Obj b)
  {                         
    if (verbose)
      System.out.println("  " + a + " ?= " + b);  
    
    // identity
    verify(a.getClass() == b.getClass());
    verify(a.getName(), b.getName());
    verify(a.getHref(), b.getHref());
    verify(a.getIs(),   b.getIs());
    
    // value
    if (a instanceof Val)
      verify( ((Val)a).encodeVal(), ((Val)b).encodeVal() );

    // list
    if (a instanceof List)     
    {                        
      verify( ((List)a).getOf(),  ((List)b).getOf() );
    }

    // op
    if (a instanceof Op)     
    {                        
      verify( ((Op)a).getIn(),  ((Op)b).getIn() );
      verify( ((Op)a).getOut(), ((Op)b).getOut() );
    }

    // event
    if (a instanceof Feed)     
    {                        
      verify( ((Feed)a).getIn(),  ((Feed)b).getIn() );
      verify( ((Feed)a).getOf(),  ((Feed)b).getOf() );
    }

    // facets
    verify(a.getDisplay(), b.getDisplay());
    verify(a.getDisplayName(), b.getDisplayName());
    verify(a.getIcon(), b.getIcon());
    verify(a.getStatus() == b.getStatus());
    verify(a.isNull() == b.isNull());
    verify(a.isWritable() == b.isWritable());
    if (a instanceof Bool)
    {
      verify(((Bool)a).getRange(), ((Bool)b).getRange());
    }
    else if (a instanceof Enum)
    {
      verify(((Enum)a).getRange(), ((Enum)b).getRange());
    }
    else if (a instanceof Int)
    {
      verify(((Int)a).getMin() == ((Int)b).getMin());
      verify(((Int)a).getMax() == ((Int)b).getMax());
      verify(((Int)a).getUnit(),  ((Int)b).getUnit());
    }
    else if (a instanceof Real)
    {
      verify(((Real)a).getMin() == ((Real)b).getMin());
      verify(((Real)a).getMax() == ((Real)b).getMax());
      verify(((Real)a).getUnit(),  ((Real)b).getUnit());
      verify(((Real)a).getPrecision() == ((Real)b).getPrecision());
    }
    else if (a instanceof Str)
    {
      verify(((Str)a).getMin() == ((Str)b).getMin());
      verify(((Str)a).getMax() == ((Str)b).getMax());
    }
    else if (a instanceof Reltime)
    {
      verify(((Reltime)a).getMin(), ((Reltime)b).getMin());
      verify(((Reltime)a).getMax(), ((Reltime)b).getMax());
    }
    else if (a instanceof Abstime)
    {
      verify(((Abstime)a).getMin(), ((Abstime)b).getMin());
      verify(((Abstime)a).getMax(), ((Abstime)b).getMax());
      verify(((Abstime)a).getTz(),  ((Abstime)b).getTz());
    }
    else if (a instanceof Time)
    {
      verify(((Time)a).getMin(), ((Time)b).getMin());
      verify(((Time)a).getMax(), ((Time)b).getMax());
      verify(((Time)a).getTz(),  ((Time)b).getTz());
    }
    else if (a instanceof Date)
    {
      verify(((Date)a).getMin(), ((Date)b).getMin());
      verify(((Date)a).getMax(), ((Date)b).getMax());
      verify(((Date)a).getTz(),  ((Date)b).getTz());
    }
    else if (a instanceof List)
    {
      verify(((List)a).getMin() == ((List)b).getMin());
      verify(((List)a).getMax() == ((List)b).getMax());
    }
    
    // recurse children
    Obj[] akids = a.list();
    Obj[] bkids = b.list();
    verify(akids.length == bkids.length);
    for(int i=0; i<akids.length; ++i)
      verifyEquivalent(akids[i], bkids[i]);
  }

  public Obj roundtrip(Obj orig)
    throws Exception            
  {
    return roundtrip(orig, true);
  }

  public Obj roundtrip(Obj orig, boolean testBinary)
    throws Exception         
  {             
    // encode to byte array
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    ObixEncoder out = new ObixEncoder(bout);
    out.encodeDocument(orig);
    out.flush();
    byte[] buf = bout.toByteArray();

    // decode from byte array
    ByteArrayInputStream bin = new ByteArrayInputStream(buf);
    ObixDecoder in = new ObixDecoder(bin);
    Obj piped = in.decodeDocument(); 
    
    // verify original is same as that piped thru IO
    if (verbose)
    {
      System.out.println("");  
      System.out.println("============");  
      System.out.write(buf);
      System.out.println("--- xml ---");
      ObixEncoder.dump(piped);
    }
    verifyEquivalent(orig, piped);    
    
    // binary round trip  
    binaryRoundtrip = null;   
    if (testBinary)
    {
      byte[] binary = BinObixEncoder.toBytes(orig);         
      binaryRoundtrip = binary;
      Obj binaryPiped = BinObixDecoder.fromBytes(binary);
      if (verbose)
      {                            
        System.out.print("Bin:");
        for (int i=0; i<binary.length; ++i)
        {
          int c = binary[i] & 0xff;
          System.out.print(" " + (c < 0xa ? "0" : "") + Integer.toHexString(c));
        }                                                                       
        System.out.println();
        ObixEncoder.dump(binaryPiped);
      }
      verifyEquivalent(orig, binaryPiped);    
    }
    
    return piped;
  }

////////////////////////////////////////////////////////////////
// Main
////////////////////////////////////////////////////////////////

  public static void main(String args[])
  {                            
    // check args for filter             
    String filter = "";
    if (args.length > 0 && !args[0].startsWith("/")) 
      filter = "obix.test." + args[0];         
    
    // check args for options  
    for(int i=0; i<args.length; ++i)
      if (args[i].equals("/v")) verbose = true;   
     
    // run tests
    for(int i=0; i<tests.length; ++i)
    {        
      Test test = tests[i]; 
      if (!test.getName().startsWith(filter)) continue;
      try
      {         
        test.run();
        System.out.println("Success: " + test.getName() + " [" + test.count + "]");
      }
      catch(Throwable e)
      {        
        System.out.println("FAILED:  " + test.getName());
        e.printStackTrace();
      }
    }  
  }
    
////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  static boolean verbose;

  int count;  
  byte[] binaryRoundtrip;
  
}
