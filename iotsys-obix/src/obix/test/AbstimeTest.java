/*
 * This code licensed to public domain
 */
package obix.test;

import java.util.*;

import obix.Abstime;

/**
 * AbstimeTest ensures Abstime follows XML Schema lexical rules
 *
 * @author    Brian Frank
 * @creation  27 Apr 05
 * @version   $Revision$ $Date$
 */
public class AbstimeTest
  extends Test
{ 

  public static TimeZone[] timeZones =
  {
    TimeZone.getDefault(),
    TimeZone.getTimeZone("GMT"),
    TimeZone.getTimeZone("America/New_York"),
    TimeZone.getTimeZone("America/Phoenix"),
    TimeZone.getTimeZone("America/Los_Angeles"),
    TimeZone.getTimeZone("US/Mountain"),
    TimeZone.getTimeZone("Europe/Brussels"),
    TimeZone.getTimeZone("Europe/Vienna"),
    TimeZone.getTimeZone("Asia/Tokyo"),
    TimeZone.getTimeZone("Asia/Hong_Kong"),
    new SimpleTimeZone(-4*60*60*1000, "Offset1"),
    new SimpleTimeZone(-5*60*60*1000, "Offset2")
  };                     
  
  public static final int january    = 1;
  public static final int february   = 2;
  public static final int march      = 3;
  public static final int april      = 4;
  public static final int may        = 5;
  public static final int june       = 6;
  public static final int july       = 7;
  public static final int august     = 8;
  public static final int september  = 9;
  public static final int october    = 10;
  public static final int november   = 11;
  public static final int december   = 12;         
  
  public static final int sunday    = 0;
  public static final int monday    = 1;
  public static final int tuesday   = 2;
  public static final int wednesday = 3;
  public static final int thursday  = 4;
  public static final int friday    = 5;
  public static final int saturday  = 6;

////////////////////////////////////////////////////////////////
// Driver
////////////////////////////////////////////////////////////////  

  public void run()
    throws Exception
  {             
    verifyBasics();
    verifyCodec();
    verifyLeapYears();
    verifyDaysInMonth();
    verifyTimeOfDayMillis();
    verifyNextPrevDay();
    verifyNextPrevMonth();
    verifyNextPrevYear();
    verifyNextPrevWeekdays();
  }

////////////////////////////////////////////////////////////////
// Basics
////////////////////////////////////////////////////////////////  

  public void verifyBasics()
  {
    TimeZone est = TimeZone.getTimeZone("America/New_York");
    Abstime t;
    
    // Eastern Daylight (-4 GMT)
    t = new Abstime(2005, 9, 21, 10, 59, 7, 654, est);
    verify(t.getTimeZone() == est);
    verify(t.getYear()        == 2005);   
    verify(t.getMonth()       == 9);
    verify(t.getDay()         == 21);
    verify(t.getHour()        == 10);
    verify(t.getMinute()      == 59);
    verify(t.getSecond()      == 7);
    verify(t.getMillisecond() == 654);  
    verify(t.inDaylightTime());
    verify(t.getTimeZoneOffset() == -4*60*60*1000);

    // Eastern Standard (-5 GMT)
    t = new Abstime(2005, 12, 25, 13, 2, 0, 0, est);
    verify(t.getTimeZone() == est);
    verify(t.getYear()        == 2005);   
    verify(t.getMonth()       == 12);
    verify(t.getDay()         == 25);
    verify(t.getHour()        == 13);
    verify(t.getMinute()      == 2);
    verify(t.getSecond()      == 0);
    verify(t.getMillisecond() == 0);  
    verify(!t.inDaylightTime());
    verify(t.getTimeZoneOffset() == -5*60*60*1000);
    
    // Eastern Daylight from millis: 2005-09-21 10:59:01.310EDT
    t = new Abstime(1127314741310L, est);
    verify(t.getYear()        == 2005);   
    verify(t.getMonth()       == 9);
    verify(t.getDay()         == 21);
    verify(t.getHour()        == 10);
    verify(t.getMinute()      == 59);
    verify(t.getSecond()      == 1);
    verify(t.getMillisecond() == 310);  
    verify(t.inDaylightTime());
    verify(t.getTimeZoneOffset() == -4*60*60*1000); 
    
    // convert to utc (-4)
    t = t.toUtcTime();            
    verify(t.getYear()        == 2005);   
    verify(t.getMonth()       == 9);
    verify(t.getDay()         == 21);
    verify(t.getHour()        == 14);
    verify(t.getMinute()      == 59);
    verify(t.getSecond()      == 1);
    verify(t.getMillisecond() == 310);  
    verify(!t.inDaylightTime());
    verify(t.getTimeZoneOffset() == 0);  
    verify(t.encodeVal().equals("2005-09-21T14:59:01.310Z"));  
    
    // back to local time
    t = t.toLocalTime();              
    verify(t.getYear()        == 2005);   
    verify(t.getMonth()       == 9);
    verify(t.getDay()         == 21);
    verify(t.getHour()        == 10);
    verify(t.getMinute()      == 59);
    verify(t.getSecond()      == 1);
    verify(t.getMillisecond() == 310);  
    verify(t.inDaylightTime());
    verify(t.getTimeZoneOffset() == -4*60*60*1000); 
    verify(t.encodeVal().equals("2005-09-21T10:59:01.310-04:00"));  
  }
    
////////////////////////////////////////////////////////////////
// Encoding
////////////////////////////////////////////////////////////////  
  
  public void verifyCodec()
    throws Exception
  {          
    /*
    TimeZone est = new SimpleTimeZone(-4*60*60*1000, "Offset1");
    System.out.println("offset = " + (est.getRawOffset() / 3600000));
    
    Abstime t1 = new Abstime(2001, june, 7, 0, 0, 0, 0, est);    
    Abstime t2 = new Abstime(); t2.decodeVal(t1.encodeVal());
        
    System.out.println("1 millis = " + t1.getMillis());
    System.out.println("2 millis = " + t2.getMillis());
    System.out.println("delta = " + (t1.getMillis() - t2.getMillis()));
    
    System.out.println("1: " + t1.encodeVal());
    System.out.println("2: " + t2.encodeVal());

    System.out.println("1: " + t1.getTimeZoneOffset() + " - " + t1.getTimeZone().getID());
    System.out.println("2: " + t2.getTimeZoneOffset() + " - " + t2.getTimeZone().getID());
    verify(t1.equals(t2));
    */
        
    for(int i=0; i<timeZones.length; ++i)
    {
      for(int j=0; j<timeZones.length; ++j)
      {
        TimeZone iz = timeZones[i];
        TimeZone jz = timeZones[j];
        Abstime[] times =
        {
          new Abstime(System.currentTimeMillis()),
          new Abstime(System.currentTimeMillis(), iz),
          new Abstime(System.currentTimeMillis(), jz),
          new Abstime(2001, june, 7, 0, 0, 0, 0, iz),
          new Abstime(2001, june, 7, 0, 0, 0, 0, jz),
          new Abstime(2001, november, 30, 0, 0, 0, 0, iz),
          new Abstime(2001, november, 30, 0, 0, 0, 0, jz),
          new Abstime(1004553579152L, iz),
          new Abstime(1004553579152L, jz),
        };
                
        for(int k=0; k<times.length; ++k) 
          for(int m=0; m<times.length; ++m)
          {
            Abstime t = times[k];
            Abstime x = new Abstime();
            x.decodeVal(t.encodeVal());
//System.out.println(t + " ?= " + x);
            verify(t.equals(x));
          }
      }
    }                   
    
    // test arbitrary seconds precision
    Abstime t = new Abstime();
    t.decodeVal("2005-09-21T13:14:02.1234567Z");
    verify(t.equals(new Abstime(2005, 9, 21, 13, 14, 2, 123, TimeZone.getTimeZone("UTC"))));
    t.decodeVal("2005-09-21T13:14:02.1234567-05:00");
    verify(t.equals(new Abstime(2005, 9, 21, 13, 14, 2, 123, TimeZone.getTimeZone("EST"))));
    t.decodeVal("2005-09-21T13:14:02Z");
    verify(t.equals(new Abstime(2005, 9, 21, 13, 14, 2, 000, TimeZone.getTimeZone("UTC"))));
    t.decodeVal("2005-09-21T13:14:02.7Z");
    verify(t.equals(new Abstime(2005, 9, 21, 13, 14, 2, 700, TimeZone.getTimeZone("UTC"))));
    t.decodeVal("2005-09-21T13:14:02.74Z");
    verify(t.equals(new Abstime(2005, 9, 21, 13, 14, 2, 740, TimeZone.getTimeZone("UTC"))));
    t.decodeVal("2005-09-21T13:14:02.005Z");
    verify(t.equals(new Abstime(2005, 9, 21, 13, 14, 2, 05, TimeZone.getTimeZone("UTC"))));
    
    /*
    Abstime t1 = new Abstime(2001, june, 1, 0, 0);
    Abstime t2 = new Abstime(2001, october, 31, 0, 0);
    Abstime t3 = new Abstime(2001, june, 1, 0, 0, 0, 0, TimeZone.getTimeZone("GMT"));
    Abstime t4 = new Abstime(2001, october, 31, 0, 0, 0, 0, TimeZone.getTimeZone("GMT"));

    Abstime d1 = (Abstime)Abstime.DEFAULT.decodeFromString(t1.encodeToString());
    Abstime d2 = (Abstime)Abstime.DEFAULT.decodeFromString(t2.encodeToString());
    Abstime d3 = (Abstime)Abstime.DEFAULT.decodeFromString(t3.encodeToString());
    Abstime d4 = (Abstime)Abstime.DEFAULT.decodeFromString(t4.encodeToString());
    
    System.out.println("t1: " + t1 + " -> " + d1);    
    System.out.println("t2: " + t2 + " -> " + d2);    
    System.out.println("t3: " + t3 + " -> " + d3);    
    System.out.println("t4: " + t4 + " -> " + d4);    
    */
  }

////////////////////////////////////////////////////////////////
// Next / Prev Day
////////////////////////////////////////////////////////////////  
  
  public void verifyNextPrevDay()
  {
    //fall daylight savings
    Abstime start = new Abstime(2001,october,28,0,0,0,0);
    Abstime next = start.nextDay();
    verify(next.equals(new Abstime(2001,october,29,0,0,0,0)));
    verify(next.prevDay().equals(start));
    //non-boundry test
    start = next;
    next = start.nextDay();
    verify(next.equals(new Abstime(2001,october,30,0,0,0,0)));
    verify(next.prevDay().equals(start));
    //end of year
    start = new Abstime(2001,december,31,0,0,0,0);
    next = start.nextDay();
    verify(next.equals(new Abstime(2002,january,1,0,0,0,0)));
    verify(next.prevDay().equals(start));
  }

////////////////////////////////////////////////////////////////
// Next / Prev Month
////////////////////////////////////////////////////////////////  
  
  public void verifyNextPrevMonth()
  {
    // non-boundary
    verify(new Abstime(2003, july, 17).nextMonth().equals(
           new Abstime(2003, august, 17)));
    verify(new Abstime(2003, july, 17).prevMonth().equals(
           new Abstime(2003, june, 17)));
    // jan/dec       
    verify(new Abstime(2003, december, 17).nextMonth().equals(
           new Abstime(2004, january, 17)));
    verify(new Abstime(2003, january, 17).prevMonth().equals(
           new Abstime(2002, december, 17)));
    // month caps next
    verify(new Abstime(2003, january, 31).nextMonth().equals(
           new Abstime(2003, february, 28)));
    verify(new Abstime(2003, january, 30).nextMonth().equals(
           new Abstime(2003, february, 28)));
    verify(new Abstime(2003, january, 29).nextMonth().equals(
           new Abstime(2003, february, 28)));
    verify(new Abstime(2003, january, 28).nextMonth().equals(
           new Abstime(2003, february, 28)));
    verify(new Abstime(2003, january, 27).nextMonth().equals(
           new Abstime(2003, february, 27)));
    // month caps prev (in leap year)
    verify(new Abstime(2004, march, 31).prevMonth().equals(
           new Abstime(2004, february, 29)));
    verify(new Abstime(2004, march, 30).prevMonth().equals(
           new Abstime(2004, february, 29)));
    verify(new Abstime(2004, march, 29).prevMonth().equals(
           new Abstime(2004, february, 29)));
    verify(new Abstime(2004, march, 28).prevMonth().equals(
           new Abstime(2004, february, 28)));
    // cap carry thru next
    verify(new Abstime(2003, february, 28).nextMonth().equals(
           new Abstime(2003, march, 31)));
    verify(new Abstime(2004, february, 29).nextMonth().equals(
           new Abstime(2004, march, 31)));
    verify(new Abstime(2004, march, 31).nextMonth().equals(
           new Abstime(2004, april, 30)));
    // cap carry thru prev
    verify(new Abstime(2004, april, 30).prevMonth().equals(
           new Abstime(2004, march, 31)));
    verify(new Abstime(2004, march, 31).prevMonth().equals(
           new Abstime(2004, february, 29)));
    verify(new Abstime(2004, february, 28).prevMonth().equals(
           new Abstime(2004, january, 28)));
    verify(new Abstime(2004, february, 29).prevMonth().equals(
           new Abstime(2004, january, 31)));
    verify(new Abstime(2003, march, 31).prevMonth().equals(
           new Abstime(2003, february, 28)));
    verify(new Abstime(2003, february, 28).prevMonth().equals(
           new Abstime(2003, january, 31)));
  }

////////////////////////////////////////////////////////////////
// Next / Prev Year
////////////////////////////////////////////////////////////////  
  
  public void verifyNextPrevYear()
  {
    // next
    verify(new Abstime(2003, april, 30).nextYear().equals(
           new Abstime(2004, april, 30)));
    verify(new Abstime(2003, february, 28).nextYear().equals(
           new Abstime(2004, february, 28)));
    verify(new Abstime(2004, february, 29).nextYear().equals(
           new Abstime(2005, february, 28)));
           
    // prev
    verify(new Abstime(2003, april, 30).prevYear().equals(
           new Abstime(2002, april, 30)));
    verify(new Abstime(2003, february, 28).prevYear().equals(
           new Abstime(2002, february, 28)));
    verify(new Abstime(2004, february, 29).prevYear().equals(
           new Abstime(2003, february, 28)));
  }

////////////////////////////////////////////////////////////////
// Next / Prev Weekdays
////////////////////////////////////////////////////////////////  
  
  public void verifyNextPrevWeekdays()
  {
    verify(new Abstime(2003, july, 17).nextWeekday(thursday).equals(
           new Abstime(2003, july, 24)));
    verify(new Abstime(2003, july, 17).nextWeekday(friday).equals(
           new Abstime(2003, july, 18)));
    verify(new Abstime(2003, july, 17).nextWeekday(wednesday).equals(
           new Abstime(2003, july, 23)));
    verify(new Abstime(2003, december, 30).nextWeekday(friday).equals(
           new Abstime(2004, january, 2)));
           
    verify(new Abstime(2003, november, 3).prevWeekday(thursday).equals(
           new Abstime(2003, october, 30)));
    verify(new Abstime(2003, november, 3).prevWeekday(monday).equals(
           new Abstime(2003, october, 27)));
  }

////////////////////////////////////////////////////////////////
// Leap Years
////////////////////////////////////////////////////////////////

  public void verifyLeapYears()
  {
    verify(  Abstime.isLeapYear(1996) );
    verify( !Abstime.isLeapYear(1997) );
    verify( !Abstime.isLeapYear(1998) );
    verify( !Abstime.isLeapYear(1999) );
    verify(  Abstime.isLeapYear(2000) );
    verify( !Abstime.isLeapYear(2001) );
    verify( !Abstime.isLeapYear(2002) );
    verify( !Abstime.isLeapYear(2003) );
    verify(  Abstime.isLeapYear(2004) );
    verify( !Abstime.isLeapYear(2005) );
    verify( !Abstime.isLeapYear(2006) );
    verify( !Abstime.isLeapYear(2007) );
    verify(  Abstime.isLeapYear(2008) );
    
    verify( Abstime.getDaysInYear(2000) == 366 );
    verify( Abstime.getDaysInYear(2001) == 365 );
  }

  public void verifyDaysInMonth()
  {
    verify( Abstime.getDaysInMonth(2000, january)   == 31);
    verify( Abstime.getDaysInMonth(2000, february)  == 29);
    verify( Abstime.getDaysInMonth(2001, february)  == 28);
    verify( Abstime.getDaysInMonth(2000, march)     == 31);
    verify( Abstime.getDaysInMonth(2000, april)     == 30);
    verify( Abstime.getDaysInMonth(2000, may)       == 31);
    verify( Abstime.getDaysInMonth(2000, june)      == 30);
    verify( Abstime.getDaysInMonth(2000, july)      == 31);
    verify( Abstime.getDaysInMonth(2000, august)    == 31);
    verify( Abstime.getDaysInMonth(2000, september) == 30);
    verify( Abstime.getDaysInMonth(2000, october)   == 31);
    verify( Abstime.getDaysInMonth(2000, november)  == 30);
    verify( Abstime.getDaysInMonth(2000, december)  == 31);
  }

  public void verifyTimeOfDayMillis()
  {
    verify(0 == new Abstime(2000, january, 30, 0, 0, 0, 0).getTimeOfDayMillis());
    verify(1 == new Abstime(2000, january, 30, 0, 0, 0, 1).getTimeOfDayMillis());
    verify(1000 == new Abstime(2000, january, 30, 0, 0, 1, 0).getTimeOfDayMillis());
    verify(7000 == new Abstime(2000, january, 30, 0, 0, 7, 0).getTimeOfDayMillis());
    verify(60000 == new Abstime(2000, january, 30, 0, 1, 0, 0).getTimeOfDayMillis());
    verify(120000 == new Abstime(2000, january, 30, 0, 2, 0, 0).getTimeOfDayMillis());
    verify(3600000 == new Abstime(2000, january, 30, 1, 0, 0, 0).getTimeOfDayMillis());
    verify(86399999 == new Abstime(2000, january, 30, 23, 59, 59, 999).getTimeOfDayMillis());
    verify(0 == new Abstime(2000, january, 30, 24, 0, 0, 0).getTimeOfDayMillis());
  }

////////////////////////////////////////////////////////////////
// Debug
////////////////////////////////////////////////////////////////

//  private static String str(Abstime t)
//  {
//    Calendar cal = new GregorianCalendar();
//    cal.setTimeZone(t.getTimeZone());
//    cal.setTime(new Date(t.getMillis()));
//    return str(cal);
//  }

//  private static String str(Calendar calendar)
//  {
//    java.text.SimpleDateFormat f 
//      = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSz");
//    f.setTimeZone( calendar.getTimeZone() );
//    return f.format(calendar.getTime());
//  }

  public static void dump(TimeZone tz)
  {
    System.out.println(tz.getID() + " = " + tz.getRawOffset()/(60*60*1000));
  }
  
}
