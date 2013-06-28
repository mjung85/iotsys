/*
 * This code licensed to public domain
 */
package obix.net;

import obix.Obj;

/**
 * WatchListener provides callbacks when watch objects are updated.
 *
 * @author    Brian Frank
 * @creation  12 Apr 06
 * @version   $Revision$ $Date$
 */
public interface WatchListener
{

  public void changed(Obj obj); 
  public void closed(SessionWatch watch); 
  
} 
