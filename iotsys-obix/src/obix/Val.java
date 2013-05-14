/*
 * This code licensed to public domain
 */
package obix;

/**
 * Val is base class for value types: Bool, Int, 
 * Real, Enum, Abstime, Reltime, and Uri
 *
 * @author    Brian Frank
 * @creation  27 Apr 05
 * @version   $Revision$ $Date$
 */
public abstract class Val
  extends Obj    
  implements Comparable
{ 

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////
  
  /**
   * Construct a named Val.
   */
  public Val(String name)
  {               
    super(name);
  }

  /**
   * Construct an unnamed Val.
   */
  public Val()
  {
  }

////////////////////////////////////////////////////////////////
// Val
////////////////////////////////////////////////////////////////
  
  /**
   * For now return valEquals() 
   * TODO maybe should implement name, facet equality in Obj.equals()?
   */
  public final boolean equals(Object that)
  {                                                   
    if (that instanceof Val)
      return valEquals((Val)that);
    return false;
  }

  /**
   * Return if the value is equal to the specified Val's value
   * regardless of other aspects like name and facets.
   */
  public abstract boolean valEquals(Val that);

  /**
   * Encode the value as a string
   */
  public abstract String encodeVal();

  /**
   * Decode the value from a string.
   */
  public abstract void decodeVal(String val)
    throws Exception;    

  /**
   * Encode the value as a Java code literal to pass to the constructor.
   */
  public String encodeJava()
  {
    return encodeVal();
  }
  
  /**
   * Debug to string is encodeVal
   */
  public final String toString()   
  {
    return encodeVal();
  }
  
}
