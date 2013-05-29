/*
 * This code licensed to public domain
 */
package obix;    

import java.util.*;

/**
 * Contract is a list of URIs specifying a contract for an object.
 *
 * @author    Brian Frank
 * @creation  27 Apr 05
 * @version   $Revision$ $Date$
 */
public class Contract
{ 

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////
  
  /**
   * Construct from a space separated list.
   */   
  public Contract(String list)
  {               
    this(parse(list));    
    this.string = list;
  }

  /**
   * Construct from string list.
   */
  public Contract(String[] list)
  {                          
    this.list = new Uri[list.length];
    for (int i=0; i<list.length; ++i)
      this.list[i] = new Uri(list[i]);
  }        
  
  /**
   * Construct from uri list.
   */
  public Contract(Uri[] list)
  {                          
    this.list = list;
  }        
  
  /**
   * Parse a space separated list of uris.
   */
  public static Uri[] parse(String list)
  {               
    StringTokenizer st = new StringTokenizer(list, " ");
    ArrayList acc = new ArrayList();     
    while(st.hasMoreTokens()) acc.add(new Uri(st.nextToken()));
    return (Uri[])acc.toArray(new Uri[acc.size()]);
  }

////////////////////////////////////////////////////////////////
// Methods
////////////////////////////////////////////////////////////////
  
  /**
   * The primary Uri is always the first, and is supposed
   * to represent a contract that merges the entire rest 
   * of the list into one fetchable uri.
   */
  public Uri primary()
  {                                     
    return list[0];
  }            
  
  /**
   * Return the length of the uri list.
   */                                 
  public int size()
  {
    return list.length;
  }           

  /**
   * Get the uri at the specified index.
   */                                 
  public Uri get(int index)
  {
    return list[index];                       
  }

  /**
   * Get unsafe reference to list uris.
   */
  public Uri[] list()
  {        
    return list;
  }           

  /**
   * Return true if this contract list contains the specified uri.
   */
  public boolean contains(Uri uri)                                     
  {
    for (int i=0; i<list.length; ++i)
      if (list[i].equals(uri))
        return true;
    return false;                      
  }                                 
  
  /**
   * If this a contract with a size of one for "obix:Obj".
   */
  public boolean containsOnlyObj()                                     
  {                                                       
    return list.length == 1 && list[0].get().equals("obix:obj");
  }  
  
  /**
   * Equality based on the list of uris.
   */
  public boolean equals(Object that)
  {                                     
    if (that instanceof Contract)
    {            
      return toString().equals(that.toString());
    }
    return false; 
  }                   
  
  /**
   * Encode to a Java expression.
   */
  public String encodeJava()
  {             
    // might want to escape funny chars
    return "new Contract(\"" + toString() + "\")";
  }  
  
  /**
   * Return space separated list of uris.
   */
  public String toString()
  {             
    if (string == null)
    {
      StringBuffer s = new StringBuffer();
      for (int i=0; i<list.length; ++i)
      {
        if (i > 0) s.append(' ');
        s.append(list[i].encodeVal());
      }
      string = s.toString();
    }                  
    return string;
  }  

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  static final Contract Obj = new Contract("obix:obj");

  Uri[] list;    
  String string;
  
}
