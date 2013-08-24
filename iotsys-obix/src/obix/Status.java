/*
 * This code licensed to public domain
 */
package obix;         

import java.util.HashMap;

/**
 * Status is a typesafe (pre-1.5) enumeration for 
 * the status attribute.
 *
 * @author    Brian Frank
 * @creation  23 May 06
 * @version   $Revision$ $Date$
 */
public class Status
{ 

////////////////////////////////////////////////////////////////
// Lookup Tables
////////////////////////////////////////////////////////////////

  /**
   * Get the list of status values.
   */
  public static Status[] list()
  {
    return (Status[])list.clone();
  }

  /**
   * Return the status value for the specified 
   * string encoding or null if no match.
   */
  public static Status parse(String s)
  {         
    return (Status)map.get(s);
  }

  private static Status[] list = new Status[8];
  private static HashMap map = new HashMap();

////////////////////////////////////////////////////////////////
// Range
////////////////////////////////////////////////////////////////

  public static final Status disabled     = new Status(0, "disabled");
  public static final Status fault        = new Status(1, "fault");
  public static final Status down         = new Status(2, "down");
  public static final Status unackedAlarm = new Status(3, "unackedAlarm");
  public static final Status alarm        = new Status(4, "alarm");
  public static final Status unacked      = new Status(5, "unacked");
  public static final Status overridden   = new Status(6, "overridden");
  public static final Status ok           = new Status(7, "ok");
  
////////////////////////////////////////////////////////////////
// Private Constructor
////////////////////////////////////////////////////////////////
  
  private Status(int ordinal, String name)
  {
    this.ordinal = ordinal;
    this.name    = name;  
    list[ordinal] = this;
    map.put(name, this);
  }          

////////////////////////////////////////////////////////////////
// Methods
////////////////////////////////////////////////////////////////
  
  /**
   * Get the ordinal value such that ordinal 0 (disabled) 
   * is higher priority than ordinal 7 (ok)
   */
  public int getOrdinal()
  {
    return ordinal;
  } 
  
  /**
   * Get the string name for this status value.
   */
  public String toString()
  {                                            
    return name;
  }  
  
////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  private int ordinal;
  private String name;
  
}
