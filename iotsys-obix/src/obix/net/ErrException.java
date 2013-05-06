/*
 * This code licensed to public domain
 */
package obix.net;

import obix.Err;

/**
 * ErrException is thrown by ObixSession when a request 
 * returns an unexpected err object.
 *
 * @author    Brian Frank
 * @creation  12 Sept 05
 * @version   $Revision$ $Date$
 */
public class ErrException
  extends RuntimeException
{

  public ErrException(Err err)
  {                    
    super(err.format());
    this.err = err;
  }                

  public final Err err;  
  
} 
