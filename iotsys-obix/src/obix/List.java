/*
 * This code licensed to public domain
 */
package obix;

/**
 * List is the core type used to managed unnamed collections of objects.
 *
 * @author    Brian Frank
 * @creation  27 Apr 05
 * @version   $Revision$ $Date$
 */
public class List
  extends Obj
{ 

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////

  /**
   * Construct a named List with of contract.
   */
  public List(String name, Contract of)
  {               
    super(name);        
    setOf(of);              
  }
  
  /**
   * Construct a named List.
   */
  public List(String name)
  {               
    this(name, null);
  }

  /**
   * Construct an unnamed List.
   */
  public List()
  {                
    this(null, null);
  }

////////////////////////////////////////////////////////////////
// List
////////////////////////////////////////////////////////////////

  /**
   * Return "list".
   */
  public String getElement()
  {
    return "list";
  }

  /**
   * Return BinObix.LIST.
   */
  public int getBinCode()
  {
    return obix.io.BinObix.LIST;
  }

  /**
   * Get the contract of the objects this list contains.
   */
  public Contract getOf()
  {        
    return of;
  }

  /**
   * Set the contract of the objects this list contains.
   */
  public void setOf(Contract of)
  {              
    this.of = (of != null) ? of : Contract.Obj;
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
  
  Contract of;    
  private int min = MIN_DEFAULT;
  private int max = MAX_DEFAULT;
 
  
}
