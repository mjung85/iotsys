/*
 * This code licensed to public domain
 */
package obix;      


/**
 * Ref models a reference object.
 *
 * @author    Brian Frank
 * @creation  30 Mar 06
 * @version   $Revision$ $Date$
 */
public class Ref
  extends Obj
{ 

////////////////////////////////////////////////////////////////
// Construction
////////////////////////////////////////////////////////////////
  
  /**
   * Construct named Ref with specified and and href.
   */
  public Ref(String name, Uri href) 
  { 
    super(name);  
    setHref(href);
  }                 

  /**
   * Construct named Ref.
   */
  public Ref(String name) 
  {                
    super(name);
  }                 
    
  /**
   * Construct unnamed Ref.
   */
  public Ref() 
  { 
  }

////////////////////////////////////////////////////////////////
// Ref
////////////////////////////////////////////////////////////////
  
  /**
   * Convenience for <code>getHref().isResolved()</code>.
   */
  public boolean isResolved()
  {
    Uri href = getHref();
    if (href == null) return false;
    return href.isResolved();
  }

  /**
   * Convenience for <code>getHref().getResolved()</code>.
   */
  public Obj getResolved()
  {
    Uri href = getHref();
    if (href == null) return null;
    return href.getResolved();
  }

////////////////////////////////////////////////////////////////
// Obj
////////////////////////////////////////////////////////////////

  /**
   * Return "ref".
   */
  public String getElement()
  {
    return "ref";
  }

  /**
   * Return BinObix.REF.
   */
  public int getBinCode()
  {
    return obix.io.BinObix.REF;
  }

  /**
   * Debug to string is href
   */
  public final String toString()   
  {       
    Uri href = getHref();
    if (href == null) return "null";
    return href.toString();
  }
    
////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////
    
}
