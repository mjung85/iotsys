/*
 * Copyright 2007, Tridium, Inc. All Rights Reserved.
 */
package obix.net;

import java.io.IOException;
import java.io.InputStream;

/**
 * The HttpInputStream is used to read the entity of and HTTP 1.1
 * request or response.  It will bound the number of bytes read
 * from the underlying stream based on content length, or it can
 * be used to read chunked transfer encoding.
 *
 * @author    John Sublett
 * @author    Craig Gemmill
 * @creation  20 Sep 2000
 * @version   $Revision: 1$ $Date: 3/28/2005 10:49:34 AM$
 */
public class HttpInputStream
  extends InputStream
{
  /**
   * Constructor.
   *
   * @param in the input stream on which this one
   *   is built
   */
  public HttpInputStream(InputStream in)
  {
    this(in, false);
  }

  /**
   * Constructor.
   *
   * @param in the input stream on which this one
   *   is built
   * @param contentLength the length of the input
   *   content; if contentLength <= then the content
   *   length is considered to be unbounded
   */
  public HttpInputStream(InputStream in, int contentLength)
  {
    this.in =  in;
    this.maxBytes = contentLength;
    finiteLength  = contentLength >= 0;
  }

  /**
   * Constructor.
   *
   * @param in the input stream on which this one
   *   is built
   */
  public HttpInputStream(InputStream in, boolean chunked)
  {
    this.finiteLength = false;

    if (chunked)
      this.in = new ChunkedInputStream(in);
    else
      this.in = in;
  }

  /**
   * Reads the next byte of data from this input stream. 
   */
  public int read()
  throws IOException
  {
    if (finiteLength && (bytesRead == maxBytes))
      return -1;

    int byteVal = in.read();

    if (finiteLength)
      bytesRead++;

    return byteVal;
  }

  /**
   * Read up to len bytes into the specified buffer
   * starting at the specified offset.
   */
  public int read(byte[] buf, int offset, int len)
  throws IOException
  {
    if (finiteLength && (bytesRead == maxBytes))
      return -1;

    int toRead = len;

    if (finiteLength)
      toRead = Math.min(maxBytes - bytesRead, toRead);

    int thisRead = in.read(buf, offset, toRead);
    if (finiteLength)
      bytesRead += thisRead;

    return thisRead;
  }

  /**
   * Close this input stream.  Important, this does
   * not close the underlying input stream.  It
   * simply prevents any more bytes from being read
   * from the underlying input stream.
   */
  public void close()
  {
    finiteLength = true;
    bytesRead = maxBytes;
  }

///////////////////////////////////////////////////////////
//Attributes
///////////////////////////////////////////////////////////

  private InputStream in;
  private boolean     finiteLength;
  private int         maxBytes;
  private int         bytesRead;
}

