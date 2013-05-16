/*
 * This code licensed to public domain
 */
package obix;

/**
 * Enum models a discrete value within a value set range.
 *
 * @author    Brian Frank
 * @creation  27 Apr 05
 * @version   $Revision$ $Date$
 */
public class Enum
  extends Val
{ 

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////
  
  /**
   * Construct named Enum with specified value.
   */
  public Enum(String name, String val) 
  { 
    super(name); 
    set(val);
  }                 
  
  /**
   * Construct unnamed Enum with specified value.
   */      
  public Enum(String val) 
  {          
    set(val);
  } 
    
  /**
   * Construct unnamed Enum with value of ""
   */
  public Enum() 
  { 
    set("");
  }

////////////////////////////////////////////////////////////////
// Enum
////////////////////////////////////////////////////////////////

  /**
   * Get value as string key. 
   */
  public String get()
  {
    return val;
  }

  /**
   * Set value as string key. 
   */
  public void set(String val)
  {
    if (val == null) throw new IllegalArgumentException("val cannot be null");
    String oldVal = this.val;
    this.val = val;
    if(!this.val.equals(oldVal)){
    	notifyObservers();
    }
  }

////////////////////////////////////////////////////////////////
// Val
////////////////////////////////////////////////////////////////

  /**
   * Return "enum".
   */
  public String getElement()
  {
    return "enum";
  }

  /**
   * Return BinObix.ENUM.
   */
  public int getBinCode()
  {
    return obix.io.BinObix.ENUM;
  }

  /**
   * Return if specified Val has equivalent enum value.
   */
  public boolean valEquals(Val that)
  {
    if (that  instanceof Enum)
      return ((Enum)that).val == val;
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
    return val.compareTo(((Enum)that).val);
  }

  /**
   * Encode the value as a string
   */
  public String encodeVal()
  {
    return val;
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
   * Get the range facet or null if unspecified.
   */
  public Uri getRange()
  {
    return range;
  }

  /**
   * Set the range facet.
   */
  public void setRange(Uri range)
  {
    this.range = range;
  }
    
////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////
  
  private String val;
  private Uri range;
  
  
  public void writeObject(Obj input) {
		if (this.getParent() != null) {
			this.getParent().writeObject(input);
		} else {
			if (input instanceof obix.Enum) {
				this.set(((obix.Enum) input).get());
			}
		}
	}
  
}
