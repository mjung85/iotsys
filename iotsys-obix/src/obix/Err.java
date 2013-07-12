/*
 * This code licensed to public domain
 */
package obix;      


/**
 * Err models an error object.
 *
 * @author    Brian Frank
 * @creation  30 Mar 06
 * @version   $Revision$ $Date$
 */
public class Err
  extends Obj
{ 

////////////////////////////////////////////////////////////////
// Construction
////////////////////////////////////////////////////////////////
  
  /**
   * Construct named Err.
   */
  public Err(String name) 
  {                
    super(name);
  }                 
    
  /**
   * Construct unnamed Err.
   */
  public Err() 
  { 
  }

////////////////////////////////////////////////////////////////
// Obj
////////////////////////////////////////////////////////////////

  /**
   * Return "err".
   */
  public String getElement()
  {
    return "err";
  }

  /**
   * Return BinObix.ERR.
   */
  public int getBinCode()
  {
    return obix.io.BinObix.ERR;
  }

////////////////////////////////////////////////////////////////
// Format
////////////////////////////////////////////////////////////////
  
  /**
   * Format the error for human display.
   */
  public String format()
  {                               
    // TODO - displayName, is, etc
dump();  
    return this.toDisplayString();
  }
    
////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////
    
}
