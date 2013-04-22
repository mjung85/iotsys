/*
 * This code licensed to public domain
 */
package obix.test;

import obix.Time;

/**
 * TimeTest ensures Time follows XML Schema lexical rules
 *
 * @author    Brian Frank
 * @creation  21 Sept 09
 * @version   $Revision$ $Date$
 */
public class TimeTest
  extends Test
{                    

  public static final long S = 1000L;
  public static final long M = 60L*S;
  public static final long H = 60L*M;

////////////////////////////////////////////////////////////////
// Main
////////////////////////////////////////////////////////////////  

  public void run()
    throws Exception
  {                   
    // constructor
    Time t = new Time(1, 2, 3, 4);
    verify(t.getHour(),        1);
    verify(t.getMinute(),      2);
    verify(t.getSecond(),      3);
    verify(t.getMillisecond(), 4);
    
    // equality
    verify(t.equals(new Time(1, 2, 3, 4)));
    verify(!t.equals(new Time(1, 2, 3, 0)));
    verify(!t.equals(new Time(1, 2, 0, 4)));          
    verify(!t.equals(new Time(1, 0, 3, 4)));
    verify(!t.equals(new Time(0, 2, 3, 4)));
    
    // comparison
    verify(new Time(1, 2, 3, 4).compareTo(new Time(1, 2, 3, 4)) == 0);
    verify(new Time(1, 2, 3, 3).compareTo(new Time(1, 2, 3, 4)) < 0);
    verify(new Time(1, 2, 3, 5).compareTo(new Time(1, 2, 3, 4)) > 0);
    verify(new Time(1, 2, 10, 4).compareTo(new Time(1, 2, 3, 4)) > 0);
    verify(new Time(1, 2, 3, 4).compareTo(new Time(13, 2, 3, 4)) < 0);
    
    // parsing
    verify("12:34", 12*H+34*M);     
    verify("00:00", 0);     
    verify("00:00:00", 0);     
    verify("02:00:00", 2*H);     
    verify("02:03:00", 2*H+3*M);     
    verify("02:03:04", 2*H+3*M+4*S);     
    verify("02:03:04.007", 2*H+3*M+4*S+7L);     
    verify("02:03:04.087", 2*H+3*M+4*S+87L);     
    verify("02:03:04.987", 2*H+3*M+4*S+987L);     
    verify("23:58:36.030", 23*H+58*M+36*S+30L);     
    verify("12:34:56.0123", 12*H+34*M+56*S+12L);  // truncates past ms
    verify("12:34:56.01234", 12*H+34*M+56*S+12L);  // truncates past ms
    
    verifyInvalid("2:30");
    verifyInvalid("12:4");
    verifyInvalid("12.34");
    verifyInvalid("12:34.3");
    verifyInvalid("12:34:3");
    verifyInvalid("12:34:35x");
  }                       
  
////////////////////////////////////////////////////////////////
// Decode
////////////////////////////////////////////////////////////////  

  public void verify(String s, long millis)
    throws Exception
  {
    Time t = new Time();
    t.decodeVal(s);                 
    // System.out.println("-- " + s + " -> " + t + "  " + t.getMillis() + " ?= " + millis);    
    verify(t.getMillis() == millis);  
    
    Time x = new Time();
    x.decodeVal(t.encodeVal());
    // System.out.println("   " + x);    
    verify(x.equals(t));
    verify(x.getMillis() == millis);   
    
    verify(new Time(millis), t);
  }

  public void verifyInvalid(String s)
    throws Exception
  {
    Exception ex = null;
    Time t = new Time();
    try 
    { 
      t.decodeVal(s); 
    } 
    catch(Exception e) 
    { 
      ex = e; 
    }
    // System.out.println("--- " + s + " --> " + ex);    
    verify(ex != null);

  }
  
}
