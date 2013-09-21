/*
 * Copyright 2007 Tridium, Inc. All Rights Reserved.
 */
package obix.net;

import java.io.IOException;

/**
 * HttpException wraps an HTTP status code.
 *
 * @author    Brian Frank
 * @author    Craig Gemmill
 * @creation  28 Jun 01
 * @version   $Revision$ $Date: 1/29/2003 1:46:16 PM$
 * @since     Baja 1.0 
 */
public class HttpException
  extends IOException
{

  /**
   * Constructor with expected and actual status code.
   */
  public HttpException(int expectedStatusCode, int statusCode)
  {  
    super(statusCode + ": " + Http.getReasonPhrase(statusCode));
    this.expectedStatusCode = expectedStatusCode;
    this.statusCode = statusCode;
  }

  /**
   * Constructor with actual status code.
   */
  public HttpException(int statusCode)
  {  
    this(-1, statusCode);
  }

  /**
   * Get the expected status or -1 if unknown.
   */
  public int getExpectedStatusCode()
  {
    return expectedStatusCode;
  }

  /**
   * Get the status code.
   */
  public int getStatusCode()
  {
    return statusCode;
  }
  
  private int expectedStatusCode;
  private int statusCode;
  
}
