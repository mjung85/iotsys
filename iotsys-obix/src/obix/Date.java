/*
 * This code licensed to public domain
 */
package obix;

/**
 * Date models a day in time. 
 *
 * @author    Brian Frank
 * @creation  21 Sep 09
 * @version   $Revision$ $Date$
 */
public class Date
  extends Val
{ 

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////
  
  /**
   * Construct named Date with specified values.
   */
  public Date(String name, int year, int mon, int day) 
  { 
    super(name); 
    set(year, mon, day);
  }                 

  /**
   * Construct named Date with value of 1 Jan 2000.
   */
  public Date(String name) 
  { 
    super(name); 
  }                 
  
  /**
   * Construct unnamed Date with specified values.
   */
  public Date(int year, int mon, int day) 
  {
    set(year, mon, day);
  }

  /**
   * Construct unnamed Date with value of 1 Jan 2000.
   */
  public Date() 
  {
  }                 
  
////////////////////////////////////////////////////////////////
// Date
////////////////////////////////////////////////////////////////
  
  /**
   * The year such as 2009.
   */
  public final int getYear()
  {
    return year;
  }

  /**
   * The month: 1-12.
   */
  public final int getMonth()
  {
    return mon;
  }

  /**
   * The day of month: 1-31.
   */
  public final int getDay()
  {
    return day;
  }

  /**
   * Set values. 
   */
  public void set(int year, int mon, int day)
  {                    
    if (year < 1900 || year > 2100) throw new IllegalArgumentException("year not 1900-2100: " + year);
    if (mon < 1 || mon > 12)  throw new IllegalArgumentException("mon not 1-12: " + mon);
    if (day < 0 || day > 31)  throw new IllegalArgumentException("day not 1-31: " + day);
        
    this.year = year;
    this.mon  = mon;
    this.day  = day;
  }

////////////////////////////////////////////////////////////////
// Val
////////////////////////////////////////////////////////////////

  /**
   * Return "date".
   */
  public String getElement()
  {
    return "date";
  }

  /**
   * Return BinObix.DATE.
   */
  public int getBinCode()
  {
    return obix.io.BinObix.DATE;
  }

  /**
   * Return if specified Val has equivalent time value.
   */
  public boolean valEquals(Val that)
  {
    if (that  instanceof Date) 
    {
      Date x = (Date)that;
      return year == x.year && mon == x.mon && day == x.day;
    }
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
    Date a = this;
    Date b = (Date)that;     
    
    if (a.year < b.year) return -1;
    else if (a.year > b.year) return 1;

    if (a.mon < b.mon) return -1;
    else if (a.mon > b.mon) return 1;

    if (a.day < b.day) return -1;
    else if (a.day > b.day) return 1;
    
    return 0;
  }

  /**
   * Encode the value as a string
   */
  public String encodeVal()
  {                    
    StringBuffer s = new StringBuffer();
    
    s.append(year);
    s.append('-');    
    if (mon < 10) s.append('0');
    s.append(mon);
    s.append('-');    
    if (day < 10) s.append('0');
    s.append(day);
    return s.toString();
  }                        
  
  /**
   * Parse from value string.
   */                        
  public static Date parse(String s)
    throws Exception
  {
    Date r = new Date();
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
      if (s.length() != 10 || s.charAt(4) != '-' || s.charAt(7) != '-')
        throw new Exception();
        
      int year = Integer.parseInt(s.substring(0, 4));
      int mon  = Integer.parseInt(s.substring(5, 7));
      int day  = Integer.parseInt(s.substring(8));
      set(year, mon, day);
    }
    catch(Exception e)
    {                                
      throw new Exception("Invalid date: " + s);
    }
  }            
  
////////////////////////////////////////////////////////////////
// Facets
////////////////////////////////////////////////////////////////
  
  /**
   * Get the min facet or null if unspecified.
   */
  public Date getMin()
  {
    return min;
  }

  /**
   * Set the min facet.
   */
  public void setMin(Date min)
  {
    this.min = min;
  }

  /**
   * Get the max facet or null if unspecified.
   */
  public Date getMax()
  {
    return max;
  }

  /**
   * Set the max facet.
   */
  public void setMax(Date max)
  {
    this.max = max;
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
  
  private int year = 2000;
  private int mon = 1;      // 1-12
  private int day = 1;      // 1-31        
  private Date min;
  private Date max;
  private String tz;
  
}
