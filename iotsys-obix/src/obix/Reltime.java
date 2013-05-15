/*
 * This code licensed to public domain
 */
package obix;

/**
 * Reltime models a relative duration of time as a 64-bit number of
 * milliseconds. We ignore values with greater precision when parsing.
 * The oBIX reltime is based on xs:duration, exception we disallow year 
 * or month periods which just create confusion.  
 *
 * @author    Brian Frank
 * @creation  27 Apr 05
 * @version   $Revision$ $Date$
 */
public class Reltime
  extends Val
{ 

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////
  
  /**
   * Construct named Reltime with specified value.
   */
  public Reltime(String name, long millis) 
  { 
    super(name); 
    set(millis);
  }                 

  /**
   * Construct named Reltime with value of 0.
   */
  public Reltime(String name) 
  { 
    super(name); 
    set(0);
  }                 
  
  /**
   * Construct unnamed Reltime with specified value.
   */
  public Reltime(long millis) 
  {
    set(millis);
  }
  
  /**
   * Construct unnamed Reltime with value of 0.
   */
  public Reltime() 
  { 
    set(0);
  }

////////////////////////////////////////////////////////////////
// Reltime
////////////////////////////////////////////////////////////////
  
  /**
   * Get the relative time in milliseconds.
   */
  public long getMillis()
  {                                        
    return val;
  }  

  /**
   * Get value in millis. 
   */
  public long get()
  {
    return val;
  }

  /**
   * Set value in millis. 
   */
  public void set(long millis)
  {
    this.val = millis;
  }

////////////////////////////////////////////////////////////////
// Val
////////////////////////////////////////////////////////////////

  /**
   * Return "reltime".
   */
  public String getElement()
  {
    return "reltime";
  }

  /**
   * Return BinObix.RELTIME.
   */
  public int getBinCode()
  {
    return obix.io.BinObix.RELTIME;
  }

  /**
   * Return if specified Val has equivalent duration value.
   */
  public boolean valEquals(Val that)
  {
    if (that  instanceof Reltime)
      return ((Reltime)that).val == val;
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
    long a = val;
    long b = ((Reltime)that).val;
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
    if (val < 0) s.append('-');
    s.append("PT");  
    long abs = Math.abs(val);
    long sec  = abs / 1000L;
    long frac = abs % 1000L;
    s.append(sec);
    if (frac != 0) 
    {
      s.append('.');
      if (frac < 10) s.append("00");
      else if (frac < 100) s.append("0");
      s.append(frac);
    }    
    s.append('S');
    return s.toString();
  }                        
  
  /**
   * Parse from value string.
   */                        
  public static Reltime parse(String s)
    throws Exception
  {
    Reltime r = new Reltime();
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
      long millis = 0;
      boolean neg = false;
      Parser p = new Parser(s);               
      
      // check for negative
      if (p.cur == '-') { neg = true; p.consume(); }
      else if (p.cur == '+') { p.consume(); }
      
      // next char must be P
      p.consume('P'); 
      if (p.cur == -1) throw new Exception();
      
      // D
      int num = p.num();  
      if (p.cur == 'D')
      {                                   
        p.consume();
        millis += num * 24L*60L*60L*1000L;
        num = p.num();  
      }   
      
      // H
      if (num >= 0 && p.cur == 'H')
      {           
        if (!p.time) throw new Exception();
        p.consume();
        millis += num * 60L*60L*1000L;
        num = p.num();  
      }
      
      // M
      if (num >= 0 && p.cur == 'M')
      {
        if (!p.time) throw new Exception();
        p.consume();
        millis += num * 60L*1000L;
        num = p.num();  
      }

      // S                                
      if (num >= 0 && p.cur == 'S' || p.cur == '.')
      {                                                             
        if (!p.time) throw new Exception();
        millis += num * 1000L;
        if (p.cur == '.')
        {
          p.consume();
          millis += p.frac();
        }
        p.consume('S');
      }            
      
      // verify we parsed everything
      if (p.cur != -1) throw new Exception();
    
      if (neg) millis = millis * -1;
      set(millis);
    }
    catch(Exception e)
    {                             
      throw new Exception("Invalid reltime: " + s);
    }
  }            
  
  static class Parser
  {         
    Parser(String s) 
    {
      this.s = s;
      this.cur = s.charAt(0);
    }     
    
    int frac()
    {     
      // get up to three decimal places as milliseconds within a fraction
      int millis = 0;
      if (curIsDigit)
      {
        millis += digit() * 100;
        consume();
        if (curIsDigit)
        {
          millis += digit() * 10;
          consume();
          if (curIsDigit)
          {
            millis += digit();
            consume();
            while(curIsDigit) consume();
          }
        }
      }    
      return millis;
    }  
    
    int num()
    {                           
      // skip T
      if (cur == 'T') { time = true; consume(); }
      
      int num = 0;
      while(curIsDigit) 
      {
        num = num*10 + digit();
        consume();
      }           
      return num;
    }      
    
    int digit()
    {
      return cur - '0';
    }        
    
    void consume(int ch)
    {
      if (cur != ch) throw new IllegalStateException();
      consume();
    } 
    
    void consume()
    {         
      off++;
      if (off < s.length())  
      {
        cur = s.charAt(off);
        curIsDigit = '0' <= cur && cur <= '9';
      }
      else
      {
        cur = -1;
        curIsDigit = false;
      }
    }   
    
    String s;
    int off;
    int cur;   
    boolean curIsDigit;  
    boolean time;
  }          

  /**
   * Encode the value as a Java code literal to pass to the constructor.
   */
  public String encodeJava()
  {
    return String.valueOf(val) + "L";
  }    

////////////////////////////////////////////////////////////////
// Facets
////////////////////////////////////////////////////////////////
  
  /**
   * Get the min facet or null if unspecified.
   */
  public Reltime getMin()
  {
    return min;
  }

  /**
   * Set the min facet.
   */
  public void setMin(Reltime min)
  {
    this.min = min;
  }

  /**
   * Get the max facet or null if unspecified.
   */
  public Reltime getMax()
  {
    return max;
  }

  /**
   * Set the max facet.
   */
  public void setMax(Reltime max)
  {
    this.max = max;
  }

    
////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////
  
  private long val;   // milliseconds
  private Reltime min;
  private Reltime max;
  
}
