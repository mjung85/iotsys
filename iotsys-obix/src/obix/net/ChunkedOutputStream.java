/*
 * Copyright 2007 Tridium, Inc. All Rights Reserved.
 */
package obix.net;

import java.io.*;
import java.util.Vector;

/**
 * The ChunkedOutputStream handles writing a stream of bytes
 * using the chunked transfer encoding.
 *
 * @author    John Sublett
 * @author    Craig Gemmill
 * @creation  31 Mar 2000
 * @version   $Revision$ $Date: 4/13/2005 1:19:10 PM$
 * @since     Niagara 3.0
 */
public class ChunkedOutputStream
  extends OutputStream
{
  /**
   * Constructor.
   *
   * @param out the output stream on which this
   *   one is being built
   * @param maxChunkSize the maximum number of bytes
   *   for a single chunk in the encoding
   */
  public ChunkedOutputStream(OutputStream out, int maxChunkSize)
  {
    this.out          = out;

    chunkBuf = new byte[maxChunkSize];
    pw       = new PrintWriter(out, false);
  }

  /**
   * Set the value for an entity header field to appear
   * in the footer.
   */
  public void setHeader(String name, String value)
  {
    if (entityHeaders == null)
      entityHeaders = new Vector(2);

    String[] nv = {name, value};
    entityHeaders.addElement(nv);
  }

  /**
   * Write a byte to the output stream.
   */
  public synchronized void write(int b)
    throws IOException
  {
    if (closed)
      throw new IOException("The output stream has been closed.");

    chunkBuf[byteCount++] = (byte)(0xFF & b);
    if (byteCount == chunkBuf.length)
      writeChunk();
  }

  /**
   * Write the current chunk to the output.
   */
  private void writeChunk()
    throws IOException
  {
    if (byteCount == 0)
      return;

    String sizeStr = Integer.toHexString(byteCount);
    pw.print(sizeStr);
    pw.print(Http.CRLF);
    pw.flush();
    out.write(chunkBuf, 0, byteCount);
    out.flush();
    pw.print(Http.CRLF);
    pw.flush();

    byteCount = 0;
  }

  /**
   * Write the current chunk to the output.
   */
  public synchronized void flush()
    throws IOException
  {
    writeChunk();
  }

  /**
   * Complete the chunked response.  This does not
   * close the internal output stream as it may be
   * needed for subsequent requests. Writing to
   * the stream after a close will throw an IOException.
   */
  public synchronized void close()
    throws IOException
  {
    flush();

    pw.print("0");
    pw.print(Http.CRLF);
    if (entityHeaders != null)
    {
      for (int i = 0; i < entityHeaders.size(); i++)
      {
        String[] nv = (String[])entityHeaders.elementAt(i);
        pw.print(nv[0]);
        pw.print(": ");
        pw.print(nv[1]);
        pw.print(Http.CRLF);
      }
    }
    pw.print(Http.CRLF);
    pw.flush();

    closed        = true;
    entityHeaders = null;
    chunkBuf      = null;
  }

  public int getChunkSize()
  {
    return chunkBuf.length;
  }

///////////////////////////////////////////////////////////
// Attributes
///////////////////////////////////////////////////////////

  private OutputStream out;
  private PrintWriter  pw;

  private boolean closed = false;
  private byte[] chunkBuf;
  private int byteCount = 0;
  private Vector entityHeaders;
}
