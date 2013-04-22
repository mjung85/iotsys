/*
 * This code licensed to public domain
 */
package obix;

/**
 * Bool models a boolean true/false value.
 *
 * @author    Brian Frank
 * @creation  27 Apr 05
 * @version   $Revision$ $Date$
 */
public class Bool
  extends Val
{ 

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////
  
  /**
   * Construct named Bool with specified value.
   */
  public Bool(String name, boolean val) 
  { 
    super(name); 
    this.val = val; // we don't want to notify observers here
   // set(val);
  }                 

  /**
   * Construct named Bool with value of false.
   */
  public Bool(String name) 
  { 
    super(name); 
    this.val = false; // we don't want to notify observers here
  //  set(false);
  }                 
    
  /**
   * Construct unnamed Bool with specified value.
   */
  public Bool(boolean val) 
  { 
	 this.val = val; // we don't want to notify observers here
    //set(val);
  }
  
  /**
   * Construct unnamed Bool with value of false.
   */
  public Bool() 
  { 
	this.val = false; // we don't want to notify observers here
   // set(false);
  }

////////////////////////////////////////////////////////////////
// Bool
////////////////////////////////////////////////////////////////

  /**
   * Get value as a boolean. 
   */
  public boolean get()
  {
    return val;
  }

  /**
   * Set value. 
   */
  public void set(boolean val)
  {
    this.val = val;
    notifyObservers();
  }

////////////////////////////////////////////////////////////////
// Val
////////////////////////////////////////////////////////////////

  /**
   * Return "bool".
   */
  public String getElement()
  {
    return "bool";
  }

  /**
   * Return BinObix.BOOL.
   */
  public int getBinCode()
  {
    return obix.io.BinObix.BOOL;
  }

  /**
   * Return if specified Val has equivalent boolean value.
   */
  public boolean valEquals(Val that)
  {
    if (that  instanceof Bool)
      return ((Bool)that).val == val;
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
    boolean a = val;
    boolean b = ((Bool)that).val;
    if (a == b) return 0;
    return a ? 1 : -1;
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
    this.val = val.equals("true");
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
  
  private boolean val;
  private Uri range;
  
  public void writeObject(Obj input) {
		if(this.getParent() != null){
			this.getParent().writeObject(input);
		}
		else{
			if(input instanceof Bool){
				this.set(((Bool) input).get());
			}
		}
	}
}
