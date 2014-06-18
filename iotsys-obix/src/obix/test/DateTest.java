/*
 * This code licensed to public domain
 */
package obix.test;

import obix.Date;

/**
 * DateTest ensures Date follows XML Schema lexical rules
 *
 * @author    Brian Frank
 * @creation  21 Sept 09
 * @version   $Revision$ $Date$
 */
public class DateTest
  extends Test
{                    

////////////////////////////////////////////////////////////////
// Main
////////////////////////////////////////////////////////////////  

  public void run()
    throws Exception
  {                   
    // constructor
    Date d = new Date(2009, 10, 21);
    verify(d.getYear(),  2009);
    verify(d.getMonth(), 10);
    verify(d.getDay(),   21);
    
    // equality
    verify(d.equals(new Date(2009, 10, 21)));
    verify(!d.equals(new Date(2009, 10, 22)));
    verify(!d.equals(new Date(2009, 11, 21)));
    verify(!d.equals(new Date(2010, 10, 21)));
    
    
    // comparison
    verify(new Date(2009, 2, 3).compareTo(new Date(2009, 2, 3)) == 0);
    verify(new Date(2008, 2, 3).compareTo(new Date(2009, 2, 3)) < 0);
    verify(new Date(2019, 2, 3).compareTo(new Date(2009, 2, 3)) > 0);
    verify(new Date(2009, 12, 3).compareTo(new Date(2009, 2, 3)) > 0);
    verify(new Date(2009, 2, 3).compareTo(new Date(2009, 2, 8)) < 0);
    
    // parsing
    verify("2009-10-21", 2009, 10, 21);     
    verify("2000-01-02", 2000,  1,  2);     
    verify("1972-06-30", 1972,  6, 30);     
    
    verifyInvalid("2009-10-2");
    verifyInvalid("2009-1-21");
    verifyInvalid("2009.10-21");
    verifyInvalid("2009-10.21");
  }                       
  
////////////////////////////////////////////////////////////////
// Decode
////////////////////////////////////////////////////////////////  

  public void verify(String s, int year, int mon, int day)
    throws Exception
  {
    Date d = new Date();
    d.decodeVal(s);                 
    // System.out.println("-- " + s + " -> " + d);    
    verify(d.getYear()   == year);  
    verify(d.getMonth() == mon);  
    verify(d.getDay()   == day);  
    verify(d, new Date(year, mon, day));
    
    Date x = new Date();
    x.decodeVal(d.encodeVal());
    // System.out.println("   " + x);    
    verify(x, d);
  }

  public void verifyInvalid(String s)
    throws Exception
  {
    Exception ex = null;
    Date t = new Date();
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
