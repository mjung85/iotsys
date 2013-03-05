/*
 * This code licensed to public domain
 */
package obix;

/**
 * Str models a unicode character string.
 *
 * @author    Brian Frank
 * @creation  27 Apr 05
 * @version   $Revision$ $Date$
 */
public class Str
  extends Val
{ 

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////
  
  /**
   * Construct named Str with specified value.
   */
  public Str(String name, String val) 
  { 
    super(name);  
    set(val);
  }                 
  
  /**
   * Construct unnamed Str with specified value.
   */
  public Str(String val) 
  { 
    set(val);
  } 
  
  /**
   * Construct unnamed Str with value of "".
   */
  public Str() 
  { 
    set("");
  }

////////////////////////////////////////////////////////////////
// Str
////////////////////////////////////////////////////////////////

  /**
   * Get value as a string. 
   */
  public String get()
  {
    return val;
  }

  /**
   * Set value. 
   */
  public void set(String val)
  {
    if (val == null) throw new IllegalArgumentException("val cannot be null");
    this.val = val;
  }

////////////////////////////////////////////////////////////////
// Val
////////////////////////////////////////////////////////////////

  /**
   * Return "str".
   */
  public String getElement()
  {
    return "str";
  }

  /**
   * Return BinObix.STR.
   */
  public int getBinCode()
  {
    return obix.io.BinObix.STR;
  }

  /**
   * Return if specified Val has equivalent string value.
   */
  public boolean valEquals(Val that)
  {
    if (that  instanceof Str)
      return ((Str)that).val.equals(val);
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
    return val.compareTo(((Str)that).val);
  }

  /**
   * Encode the value as a string
   */
  public String encodeVal()
  {
    return String.valueOf(val);
  }

  /**
   * Decode the value from a string.
   */
  public void decodeVal(String val)
    throws Exception
  {
    set(val);
  }

  /**
   * Encode the value as a Java code literal to pass to the constructor.
   */
  public String encodeJava()
  {
    return '"' + val + '"';
  }    

////////////////////////////////////////////////////////////////
// Facets
////////////////////////////////////////////////////////////////
  
  /**
   * Get the min facet or MIN_DEFAULT if unspecified.
   */
  public int getMin()
  {
    return min;
  }

  /**
   * Set the min facet.
   */
  public void setMin(int min)
  {                  
    if (min < 0) throw new IllegalArgumentException("min < 0");
    this.min = min;
  }

  /**
   * Get the max facet or MAX_DEFAULT if unspecified.
   */
  public int getMax()
  {
    return max;
  }

  /**
   * Set the max facet.
   */
  public void setMax(int max)
  {
    if (max < 0) throw new IllegalArgumentException("max < 0");
    this.max = max;
  }
    
////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////
  
  /** Min facet default is zero */
  public static final int MIN_DEFAULT = 0;  
  
  /** Max facet default is Integer.MAX_VALUE */
  public static final int MAX_DEFAULT = Integer.MAX_VALUE;
  
  private String val;
  private int min = MIN_DEFAULT;
  private int max = MAX_DEFAULT;
  
}
