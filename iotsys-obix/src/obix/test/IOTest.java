/*
 * This code licensed to public domain
 */
package obix.test;

import java.io.*;
import java.util.TimeZone;

import obix.*;
import obix.Enum;
import obix.io.*;

/**
 * IOTest verifies ObixEncoder and ObixDecoder
 *
 * @author    Brian Frank
 * @creation  27 Apr 05
 * @version   $Revision$ $Date$
 */
public class IOTest
  extends Test
{ 

////////////////////////////////////////////////////////////////
// Test
////////////////////////////////////////////////////////////////

  public void run()
    throws Exception
  {      
    // single obj                
    verify(new Obj());   
    verify(new Obj("foo")); 
    
    // single bool                
    verify(new Bool());   
    verify(new Bool("foo"));
    verify(new Bool(true));   
    verify(new Bool("foo", false));

    // single enum                
    verify(new Enum());   
    verify(new Enum("foo", ""));
    verify(new Enum("foo", "bar"));

    // single int                
    verify(new Int());   
    verify(new Int("foo"));
    verify(new Int(77));   
    verify(new Int("foo", 77));    
    verify(new Int("foo", 0x12345678abcdefL));
    
    // single real                
    verify(new Real());   
    verify(new Real("foo"));
    verify(new Real(1.7));   
    verify(new Real("foo", 1.7));

    // single str                
    verify(new Str());   
    verify(new Str("foo", ""));
    verify(new Str("foo", "hello world"));

    // single abstime                
    verify(new Abstime());   
    verify(new Abstime("foo"));
    verify(new Abstime("foo", System.currentTimeMillis()));

    // single reltime                
    verify(new Reltime());   
    verify(new Reltime("foo"));
    verify(new Reltime("foo", 60000L));

    // single time                
    verify(new Time());   
    verify(new Time("foo"));
    verify(new Time(23, 59, 0));
    verify(new Time("foo", 1, 2, 3, 4));

    // single date                
    verify(new Date());   
    verify(new Date("foo"));
    verify(new Date(2011, 3, 30));
    verify(new Date("foo", 1929, 12, 31));

    // single uri                
    verify(new Uri());   
    verify(new Uri("foo", ""));
    verify(new Uri("foo", "obix:foo"));

    // single op                
    verify(new Op());   
    verify(new Op("foo"));   
    verify(new Op("foo", new Contract("obix:Foo"), new Contract("obix:Bar")));   
    verify(new Op("foo", new Contract("obix:Foo obix:Ray"), new Contract("obix:Bar obix:Boo")));   

    // single feed                   
    verify(new Feed());   
    verify(new Feed("foo"));   
    verify(new Feed("foo", new Contract("obix:Foo"), null));   
    verify(new Feed("foo", null, new Contract("obix:Bar")));   
    verify(new Feed("foo", new Contract("obix:Foo"), new Contract("obix:Bar")));   

    // single ref                
    verify(new Ref());   
    verify(new Ref("foo"));
    verify(new Ref("foo", new Uri("/obix/foo")));

    // single err                
    verify(new Err());   
    verify(new Err("foo"));
    
    // href
    Obj x = new Obj();
    x.setHref(new Uri("/obix/boo"));
    verify(x);

    // is
    x = new Obj();
    x.setIs(new Contract("/obix/boo obix:Hanover"));
    verify(x);    

    // single list
    verify(new List());    
    verify(new List("coolList"));    
    verify(new List("coolList", new Contract("obix:Uri")));    

    // complex     
    x = new Obj();
    x.add(new Obj("foo"));
    x.add(new Int("bar", 1972));
    x.add(new Real("moo", 0.5));
    x.add(new Str("description", "Pretty cool"));
    x.add(new List("items", new Contract("obix:Str")));
    x.add(new Op("doIt", new Contract("obix:Str"), new Contract("obix:Int")));
    x.add(new Feed("yourFired", new Contract("obix:Nil"), new Contract("obix:Int")));
    Obj nest = new Obj("nest"); x.add(nest);
      nest.add(new Int("baz", -33));
    verify(x);     

    // verify facets: display
    x = new Obj();
    x.setDisplay("da display");
    verify(x);

    // verify facets: displayName
    x = new Obj();
    x.setDisplayName("da displayName");
    verify(x);

    // verify facets: null
    x = new Obj();
    x.setNull(true);
    verify(x);
    
    // verify facets: icon
    x = new Obj();
    x.setIcon(new Uri("/icon.png"));
    verify(x);


    // verify facets: writable
    x = new Obj();
    x.setWritable(true);
    verify(x);

    // verify facets: status
    x = new Obj();
    for (int i=0; i<Status.list().length; ++i)
    {
      x.setStatus(Status.list()[i]);
      verify(x);    
    }

    // verify facets: Bool.range
    Bool b = new Bool();
    b.setRange(new Uri("/myrange"));
    verify(b);    
    
    // verify facets: Int.min
    Int i = new Int();
    i.setMin(0);
    verify(i);    
    
    // verify facets: Int.max
    i = new Int();
    i.setMax(88);
    verify(i);    

    // verify facets: Int.unit
    i = new Int();
    i.setUnit(new Uri("obix:Celsius"));
    verify(i);    
    
    // verify facets: Real.min
    Real r = new Real();
    r.setMin(0);
    verify(r);    
    
    // verify facets: Real.max
    r = new Real();
    r.setMax(88);
    verify(r);    

    // verify facets: Real.unit
    r = new Real();
    r.setUnit(new Uri("obix:Celsius"));
    verify(r);    
    
    // verify facets: Real.precision
    r = new Real();
    r.setPrecision(3);
    verify(r);        
    
    // verify facets: Enum.range
    Enum e = new Enum();
    e.setRange(new Uri("/myrange"));
    verify(e);    

    // verify facets: Str.min
    Str s = new Str();
    s.setMin(1);
    verify(s);    

    // verify facets: Str.max
    s = new Str();
    s.setMax(20);
    verify(s);    

    // verify facets: Reltime.min
    Reltime rt = new Reltime();
    rt.setMin(new Reltime(99));
    verify(rt);    

    // verify facets: Reltime.max
    rt = new Reltime();
    rt.setMax(new Reltime(-8));
    verify(rt);    
    
    // verify facets: Abstime.min
    Abstime a = new Abstime();
    a.setMin(new Abstime(System.currentTimeMillis()));
    verify(a);    
    
    // verify facets: Abstime.max
    a = new Abstime();
    a.setMax(new Abstime(System.currentTimeMillis()));
    verify(a);    
    
    // verify facets: Abstime.tz
    TimeZone tz = TimeZone.getTimeZone("Europe/London");
    a = new Abstime(System.currentTimeMillis(), tz);
    verify(a);

    // verify facets: Time.min
    Time time = new Time();
    time.setMin(new Time(0, 0, 0));
    verify(time);    

    // verify facets: Time.max
    time = new Time();
    time.setMax(new Time(23, 59, 59, 999));
    verify(time); 

    // verify facets: Time.tz
    time = new Time(3, 45, 9);
    time.setTz("America/Chicago");
    verify(time); 

    // verify facets: Date.min
    Date date = new Date();
    date.setMin(new Date(1980, 1, 1));
    verify(date);    

    // verify facets: Date.max
    date = new Date();
    date.setMax(new Date(2020, 12, 31));
    verify(date); 

    // verify facets: Date.tz
    date = new Date(2003, 6, 3);
    date.setTz("America/Denver");
    verify(date); 

    // verify facets: List.min
    List l = new List();
    l.setMin(1);
    verify(l);    

    // verify facets: List.max
    l = new List();
    l.setMax(20);
    verify(l);    
    
    // verify with unknown elements
    Obj obj = make("<obj><what/><int name='i' val='3'/></obj>");
    verify (obj.size() == 1);
    verify (obj.get("i").getInt() == 3);    
    
    // string table     
    x = new Obj();    
    x.setIs(new Contract("acme:FooBar"));
    x.add(new Str("a", "a"));
    x.add(new Str("b", "beta"));
    x.add(new Str("c0", "charlie really long string"));
    x.add(new Str("c1", "charlie really long string"));
    x.add(new Str("c2", "charlie really long string"));
    nest = new Obj("nest"); x.add(nest);
      nest.setIs(new Contract("acme:FooBar"));
      nest.add(new Str("a", "a"));
      nest.add(new Str("b", "beta"));
      nest.add(new Str("xyz", "xyz"));
    verify(x);        
    verifySingleStr("acme:FooBar", binaryRoundtrip);
    verifySingleStr("beta", binaryRoundtrip);
    verifySingleStr("xyz", binaryRoundtrip);
    verifySingleStr("charlie really long string", binaryRoundtrip);
  }              
  
////////////////////////////////////////////////////////////////
// Utils
////////////////////////////////////////////////////////////////
  
  public void verify(Obj orig)
    throws Exception         
  {            
    roundtrip(orig); 
  }

  public Obj make(String xml)
    throws Exception
  {
    return make(xml, true);
  }

  public Obj make(String xml, boolean useContracts)
    throws Exception
  {            
    ObixDecoder decoder = new ObixDecoder(new ByteArrayInputStream(xml.getBytes()));
    decoder.setUseContracts(useContracts);
    return decoder.decodeDocument();
  }

  void verifySingleStr(String s, byte[] binary)
  {                 
    int match = -1;
    for (int i=0; i<binary.length; ++i)
    {
      if (!match(s, binary, i)) continue;
      if (match < 0) { match = i; continue; }
      throw new RuntimeException("Multiple matches for string '" + s + "'");
    }               
    verify(match >= 0);
  }       
  
  static boolean match(String s, byte[] buf, int off)
  {
    for (int i=0; i<s.length(); ++i)              
    {
      if (i+off >= buf.length) return false;
      if (buf[off+i] != s.charAt(i)) return false;
    }
    return true;
  }
  
}
