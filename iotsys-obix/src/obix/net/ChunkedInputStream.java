/*
 * Copyright 2007 Tridium, Inc. All Rights Reserved.
 */
package obix.net;

import java.io.IOException;
import java.io.InputStream;

/**
 * The ChunkedInputStream handles reading a stream of bytes
 * using the chunked transfer encoding.
 *
 * @author     John Sublett
 * @author     Craig Gemmill
 * @creation   31 Mar 2000
 * @version    $Revision: 1$ $Date: 3/28/2005 10:49:34 AM$
 */
public class ChunkedInputStream
  extends InputStream
{
  /**
   * Constructor.
   */
  public ChunkedInputStream(InputStream in)
  {
    this.in = in;
  }

  /**
   * Reads the next byte of data from this input stream.
   */
  public int read()
  throws IOException
  {
    return chunkRead();
  }

  /**
   * Process a read when the input is chunked.
   */
  protected int chunkRead()
  throws IOException
  {
    if (chunk == null)
    {
      StringBuffer sbuf = new StringBuffer(8);
      int ch = in.read();
      while((ch != -1) && (ch != Http.CR) && (ch != ' '))
      {
        sbuf.append((char)ch);
        ch = in.read();
      }

      while (ch == ' ')
        ch = in.read();

      ch = match(ch, Http.CR);
      check(ch, Http.LF);

      int chunkSize = Integer.parseInt(sbuf.toString(), 16);

      chunk = new byte[chunkSize];
      chunkOffset = 0;
      if (chunkSize != 0)
      {
        int total     = 0;
        int readBytes = 0;
        do
        {
          readBytes = in.read(chunk, total, chunk.length - total);
          if (readBytes != -1)
            total += readBytes;
        } while((readBytes != -1) && (total < chunk.length));

        ch = in.read();
        ch = match(ch, Http.CR);
        check(ch, Http.LF);
      }
      else
        readFooter();
    }

    if (chunkOffset == chunk.length)
      return -1;
    else
    {
      int byteVal = chunk[chunkOffset++];
      if ((chunk.length != 0) && (chunkOffset == chunk.length))
        chunk = null;

      return byteVal;
    }
  }

  /**
   * Read the footer of a chunked transfer.
   */
  protected void readFooter()
  throws IOException
  {
    entityHeaders = new HttpHeader();
    entityHeaders.read(in);
  }

  /**
   * If the specified character is equal to the target
   * return the next character from the input, otherwise
   * throw an IOException.
   */
  protected int match(int ch, int target)
  throws IOException
  {
    if (ch != target)
      throw new IOException("Expecting " + charToString(target) + ".");
    else
      return in.read();
  }

  /**
   * If the specified character is equal to the target
   * return, otherwise throw an IOException.
   */
  protected void check(int ch, int target)
  throws IOException
  {
    if (ch != target)
      throw new IOException("Expecting " + charToString(target) + ".");
  }

  /**
   * Get a string for the specified character.
   */
  private String charToString(int ch)
  {
    if (ch == Http.CR)
      return "\\n";
    else if (ch == Http.LF)
      return "\\r";
    else
      return "" + (char)ch;
  }

  /**
   * Get the entity headers read from the input stream.
   */
  public HttpHeader getEntityHeaders()
  {
    return entityHeaders;
  }


///////////////////////////////////////////////////////////
// Attributes
///////////////////////////////////////////////////////////

  private InputStream in;

  private byte[]      chunk;
  private int         chunkOffset;
  private HttpHeader  entityHeaders;
}