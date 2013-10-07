/*
 * This code licensed to public domain
 */
package obix.test;

import obix.Reltime;

/**
 * ReltimeTest ensures reltime follows XML Schema lexical rules
 *
 * @author    Brian Frank
 * @creation  21 Sept 05
 * @version   $Revision$ $Date$
 */
public class ReltimeTest
  extends Test
{                    

  public static final long S = 1000L;
  public static final long M = 60L*S;
  public static final long H = 60L*M;
  public static final long D = 24L*H;

////////////////////////////////////////////////////////////////
// Main
////////////////////////////////////////////////////////////////  

  public void run()
    throws Exception
  {       
    // D      
    verify("P0D",      0);
    verify("P2D",      2*D);
    verify("-P2D",    -2*D);
    verify("P12D",     12*D);
    verify("-P12D",   -12*D);
    verify("+P5432D",  5432*D);
    verify("-P5432D", -5432*D);
    
    // H
    verify("PT0H",      0);
    verify("PT2H",      2*H);
    verify("-PT2H",    -2*H);
    verify("PT12H",     12*H);
    verify("-PT12H",   -12*H);
    verify("+PT5432H",  5432*H);
    verify("-PT5432H", -5432*H);    
    
    // M
    verify("PT0M",      0);
    verify("PT2M",      2*M);
    verify("-PT2M",    -2*M);
    verify("PT12M",     12*M);
    verify("-PT12M",   -12*M);
    verify("+PT5432M",  5432*M);
    verify("-PT5432M", -5432*M);
    
    // S
    verify("PT0S",      0);
    verify("PT2S",      2*S);
    verify("-PT2S",    -2*S);
    verify("PT12S",     12*S);
    verify("-PT12S",   -12*S);
    verify("+PT5432S",  5432*S);
    verify("-PT5432S", -5432*S);
    verify("PT1.2S",     1200);
    verify("PT1.23S",    1230);
    verify("PT1.234S",   1234);
    verify("PT1.2345S",  1234);
    verify("PT1.23456S", 1234);  
    
    // Mix
    verify("P1DT2H3M4.5S",  D+2*H+3*M+4500);
    verify("-P1DT2H3M4.5S", -(D+2*H+3*M+4500));
    
    // verify invalids
    verifyInvalid("P");
    verifyInvalid("P2Y");
    verifyInvalid("P2M");
    verifyInvalid("P3H");
    verifyInvalid("P3M");
    verifyInvalid("P4S");
    verifyInvalid("PH");
    verifyInvalid("PxH");
    verifyInvalid("P2Dx");
  }

////////////////////////////////////////////////////////////////
// Decode
////////////////////////////////////////////////////////////////  

  public void verify(String s, long millis)
    throws Exception
  {
    Reltime t = new Reltime();
    t.decodeVal(s);                 
//System.out.println("-- " + s + " -> " + t + "  " + t.getMillis() + " ?= " + millis);    
    verify(t.getMillis() == millis);  
    
    Reltime x = new Reltime();
    x.decodeVal(t.encodeVal());
//System.out.println("   " + x);    
    verify(x.equals(t));
    verify(x.getMillis() == millis);
  }

  public void verifyInvalid(String s)
    throws Exception
  {
    Exception ex = null;
    Reltime t = new Reltime();
    try 
    { 
      t.decodeVal(s); 
    } 
    catch(Exception e) 
    { 
      ex = e; 
    }
//System.out.println("--- " + s + " --> " + ex);    
    verify(ex != null);

  }
  
}
