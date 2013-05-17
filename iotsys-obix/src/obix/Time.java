/*
 * This code licensed to public domain
 */
package obix;

/**
 * Time models a time of day with millisecond granuality. 
 *
 * @author    Brian Frank
 * @creation  21 Sep 09
 * @version   $Revision$ $Date$
 */
public class Time
  extends Val
{ 

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////
  
  /**
   * Construct named Time with specified values.
   */
  public Time(String name, int hour, int min, int sec, int ms) 
  { 
    super(name); 
    set(hour, min, sec, ms);
  }                 

  /**
   * Construct named Time with value of midnight.
   */
  public Time(String name) 
  { 
    super(name); 
  }                 
  
  /**
   * Construct unnamed Time with specified values.
   */
  public Time(int hour, int min, int sec, int ms) 
  {
    set(hour, min, sec, ms);
  }

  /**
   * Construct unnamed Time with specified values.
   */
  public Time(int hour, int min, int sec) 
  {
    set(hour, min, sec, 0);
  }

  /**
   * Construct unnamed Time with millis since midnight.
   */
  public Time(long millis) 
  {
    set(millis);
  }
  
  /**
   * Construct unnamed Time with value of midnight.
   */
  public Time() 
  { 
  }                 
  
////////////////////////////////////////////////////////////////
// Time
////////////////////////////////////////////////////////////////

  /**
   * Get milliseconds since midnight.
   */
  public long getMillis()
  {
    return (hour*3600000L) + (min*60000L) + (sec*1000L) + ms;
  }
  
  /**
   * The hour: 0-23.
   */
  public final int getHour()
  {
    return hour;
  }

  /**
   * The minute: 0-59.
   */
  public final int getMinute()
  {
    return min;
  }

  /**
   * The seconds: 0-59.
   */
  public final int getSecond()
  {
    return sec;
  }

  /**
   * The milliseconds: 0-999.
   */
  public final int getMillisecond()
  {
    return ms;
  }

  /**
   * Set values. 
   */
  public void set(int hour, int min, int sec, int ms)
  {                    
    if (hour < 0 || hour > 23) throw new IllegalArgumentException("hour not 0-23: " + hour);
    if (min < 0  || min > 59)  throw new IllegalArgumentException("min not 0-59: " + min);
    if (sec < 0  || sec > 59)  throw new IllegalArgumentException("sec not 0-59: " + sec);
    if (ms < 0   || ms > 999)  throw new IllegalArgumentException("ms not 0-999: " + ms);
        
    this.hour = hour;
    this.min  = min;
    this.sec  = sec;
    this.ms   = ms;
  }

  /**
   * Set with millis since midnight
   */
  public void set(long millis)
  {
    hour = (int)(millis / 3600000L); millis -= hour*3600000L;                         
    min  = (int)(millis / 60000L);   millis -= min*60000L;                         
    sec  = (int)(millis / 1000L);    millis -= sec*1000L;                         
    ms   = (int)(millis);
  }

////////////////////////////////////////////////////////////////
// Val
////////////////////////////////////////////////////////////////

  /**
   * Return "time".
   */
  public String getElement()
  {
    return "time";
  }

  /**
   * Return BinObix.TIME.
   */
  public int getBinCode()
  {
    return obix.io.BinObix.TIME;
  }

  /**
   * Return if specified Val has equivalent time value.
   */
  public boolean valEquals(Val that)
  {
    if (that  instanceof Time)
      return getMillis() == ((Time)that).getMillis();
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
    long a = getMillis();
    long b = ((Time)that).getMillis();     
    if (a == b) return 0;
    if (a < b) return -1;
    else return 1;
  }

  /**
   * Encode the value as a string
   */
  public String encodeVal()
  {                    
    StringBuffer s = new StringBuffer();
    
    if (hour < 10) s.append('0');
    s.append(hour);
    
    s.append(':');
    if (min < 10) s.append('0');
    s.append(min);

    s.append(':');
    if (sec < 10) s.append('0');
    s.append(sec);
    
    if (ms != 0)
    {
      s.append('.');
      if (ms < 10) s.append("00");
      else if (ms < 100) s.append('0');
      s.append(ms);
    }    
    return s.toString();
  }                        
  
  /**
   * Parse from value string.
   */                        
  public static Time parse(String s)
    throws Exception
  {
    Time r = new Time();
    r.decodeVal(s);
    return r;
  }  

  /**
   * Decode the value from a string.
   */
  public void decodeVal(String s)
    throws Exception
  {                    
    try
    {             
      if (s.charAt(2) != ':') throw new Exception();          
      int hour = Integer.parseInt(s.substring(0, 2));
      int min  = Integer.parseInt(s.substring(3, 5));
      int sec = 0, ms = 0;
      if (s.length() > 5)
      {
        if (s.charAt(5) != ':') throw new Exception();          
        sec = Integer.parseInt(s.substring(6, 8));        
        if (s.length() > 8)                    
        {
          if (s.charAt(8) != '.') throw new Exception();          
          if (s.length() >= 9)  ms += (s.charAt(9)  - '0') * 100;
          if (s.length() >= 10) ms += (s.charAt(10) - '0') * 10;
          if (s.length() >= 11) ms += (s.charAt(11) - '0') * 1;
        }
      }                   
      set(hour, min, sec, ms);
    }
    catch(Exception e)
    {                          
      throw new Exception("Invalid time: " + s);
    }
  }            
  
////////////////////////////////////////////////////////////////
// Facets
////////////////////////////////////////////////////////////////
  
  /**
   * Get the min facet or null if unspecified.
   */
  public Time getMin()
  {
    return minTime;
  }

  /**
   * Set the min facet.
   */
  public void setMin(Time min)
  {
    this.minTime = min;
  }

  /**
   * Get the max facet or null if unspecified.
   */
  public Time getMax()
  {
    return maxTime;
  }

  /**
   * Set the max facet.
   */
  public void setMax(Time max)
  {
    this.maxTime = max;
  }

  /**
   * Get the tz facet or null if not unspecified.
   */
  public String getTz()
  {
    return tz;
  }
  
  /**
   * Set the tz facet.
   */
  public void setTz(String tz)
  {
    this.tz = tz;
  }
    
////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////
  
  private int hour, min, sec, ms;
  private Time minTime;
  private Time maxTime;
  private String tz;
  
}
