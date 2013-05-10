/*
 * Copyright 2007 Tridium, Inc. All Rights Reserved.
 */
package obix.net;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * HttpConnection allows client applications to submit requests to
 * an HTTP server.
 *
 * @author    John Sublett
 * @author    Craig Gemmill
 * @creation  20 Sep 2000
 * @version   $Revision$ $Date: 9/26/2003 4:50:30 PM$
 */
public class HttpConnection
{
  /**
   * Constructor.
   * @param hostname
   * @param port
   * @param uri
   */
  public HttpConnection(String hostname, int port, String uri)
  {
    this(hostname, port);
    this.startUri = uri;
  }

  /**
   * Constructor.
   * @param hostname
   * @param port
   */
  public HttpConnection(String hostname, int port)
  {
    this.hostname = hostname;
    this.port = port < 0 ? Http.DEFAULT_HTTP_PORT : port;
    this.requestHeaderFields = new Vector(5);
    this.responseHeaderFields = new Hashtable(5);
    timeout = 0;
  }

  /**
   * Post the specified buffer to the server.
   *
   * @param hostname The destination host.
   * @param port The destination port.
   * @param uri The uri for the posted resource.
   * @param contentType The mime type of the posted resource.
   * @param buf The content buffer.
   */
  public static int post(String hostname, int port, String uri,
                         String contentType, byte[] buf)
    throws IOException
  {
    HttpConnection conn = new HttpConnection(hostname, port, uri);
    conn.setRequestMethod(Http.METHOD_POST);
    if (contentType != null)
      conn.setRequestHeader("content-type", contentType);
    conn.setRequestHeader("content-length", buf.length);
    conn.setRequestHeader("expect", "100-continue");

    int rc = conn.connect();
    if (rc != Http.SC_CONTINUE)
    {
      try { conn.readContent(); } catch(Exception e) {}
      return rc;
    }
    else
    {
      OutputStream out = conn.getOutputStream();
      out.write(buf);
      out.flush();
      rc = conn.postComplete();
      conn.close();
      return rc;
    }
  }

  /**
   * Set the default user agent to use for all connections.
   */
  public static void setDefaultUserAgent(String defUserAgent)
  {
    HttpConnection.defUserAgent = defUserAgent;
  }

  /**
   * Set the user agent to use for this connection.
   */
  public void setUserAgent(String userAgent)
  {
    this.userAgent = userAgent;
  }

  /**
   * Set the SO_TIMEOUT option on the connection.  If
   * this is set to 0 or never set, there is no timeout.
   */
  public void setTimeout(int timeout)
    throws java.net.SocketException
  {
    this.timeout = timeout;
    if (s != null)
      s.setSoTimeout(timeout);
  }

  public int getTimeout()
  {
    try
    {
      if (s != null)
        return s.getSoTimeout();
    } catch (Exception e) { System.out.println("Exception in getTimeout:"+e); }
    return timeout;
  }

  /**
   * Get whether or not this connection should
   * automatically follow redirects.
   */
  public boolean getFollowRedirects()
  {
    return followRedirects;
  }

  /**
   * Get the URI for the first resource retrieved with
   * this connection.
   */
  public String getUri()
  {
    return startUri;
  }

  /**
   * Get the request method.
   */
  public String getRequestMethod()
  {
    return requestMethod;
  }

  /**
   * Set the request method for this connection.
   * The value is a string indicating the HTTP request
   * type (e.g. GET, POST, HEAD, etc.). Constants for
   * common values are defined on the class.
   */
  public void setRequestMethod(String method)
  {
    requestMethod = method;
  }

  /**
   * Set the specified  request header field to the
   * specified value.
   */
  public void setRequestHeader(String name, String value)
  {
    checkHeaderReset();
    requestHeaderFields.addElement(new NameValue(name, value));
  }

  /**
   * Set the specified  request header field to the
   * specified value.
   */
  public void setRequestHeader(String name, int value)
  {
    checkHeaderReset();
    requestHeaderFields.addElement(new NameValue(name, value));
  }

  /**
   * Set the specified  request header field to the
   * specified value.
   */
  public void setRequestHeader(String name, long value)
  {
    checkHeaderReset();
    requestHeaderFields.addElement(new NameValue(name, value));
  }

  /**
   * Set the specified  request header field to the
   * specified value.
   */
  public void setRequestHeader(String name, boolean value)
  {
    checkHeaderReset();
    requestHeaderFields.addElement(new NameValue(name, value));
  }

  /**
   * Check whether or not the header should be reset.
   */
  private void checkHeaderReset()
  {
    if (requestComplete)
    {
      requestHeaderFields.removeAllElements();
      requestComplete = false;
    }
  }

  /**
   * Remove the request header field with the
   * specified name.
   */
  public void removeRequestHeader(String name)
  {
    for (int i = 0; i < requestHeaderFields.size(); i++)
    {
      NameValue nv = (NameValue)requestHeaderFields.elementAt(i);
      if (nv.name.equalsIgnoreCase(name))
      {
        requestHeaderFields.removeElementAt(i);
        return;
      }
    }
  }

  /**
   * Set whether or not this connection should
   * automatically follow redirects.
   */
  public void setFollowRedirects(boolean follow)
  {
    followRedirects = follow;
  }

  /**
   * Has the connection be redirected?
   */
  public boolean isRedirected()
  {
    return redirected;
  }

  /**
   * Open the connection.  If doAuthenticate is
   * true, authentication requests will result
   * in an automatic retry using the userName and
   * password.
   */
  public synchronized int connect()
    throws IOException
  {
    if (hostHeader == null)
    {
      StringBuffer hostBuf = new StringBuffer(hostname.length() + 5);
      hostBuf.append(hostname);
      if (port != Http.DEFAULT_HTTP_PORT)
        hostBuf.append(':').append(port);
      hostHeader = hostBuf.toString();
    }

    // Note that this is not corrected for JDK 1.5 bug #5092063.
    // FW code (BIpHost) uses an elaborate workaround to handle this.
//+ FW HttpConnection checks proxy service before opening socket,
//+ and if used for this host, uses proxy to open socket.
    s = new Socket(InetAddress.getByName(hostname), port);

    in = new BufferedInputStream(s.getInputStream());
    out = new BufferedOutputStream(s.getOutputStream());
    pw = new PrintWriter(out, false);

    if (timeout > 0)
      s.setSoTimeout(timeout);

    return newRequest(startUri);
  }

  /**
   * Initiate a new request on the same connection.
   */
  public synchronized int newRequest(String uri)
    throws IOException
  {
    if (!allowMultipleRequests)
      throw new IOException("This connection doesn't allow multiple requests.");

    if (!isOpen())
    {
      startUri = uri;
      return connect();
    }
    else
    {
      writeRequest(pw, uri);
      return readResponse(in);
    }
  }

  public synchronized int post(String uri, String contentType, byte[] buf)
    throws IOException
  {
    setRequestMethod(Http.METHOD_POST);
    setRequestHeader("content-type", contentType);
    setRequestHeader("content-length", buf.length);
    setRequestHeader("expect", "100-continue");

    int rc = newRequest(uri); System.out.println("RC="+rc);
    if (rc != Http.SC_CONTINUE)
    {
      readContent();
      return rc;
    }
    else
    {
      OutputStream out = getOutputStream();
      out.write(buf);
      return postComplete();
    }
  }

//  public synchronized int put(String uri, String contentType, long contentLength, InputStream contents)
  public synchronized int put(String uri, String contentType, byte[] contents)
    throws IOException
  {
    setRequestMethod(Http.METHOD_PUT);
    setRequestHeader("content-type", contentType);
    setRequestHeader("content-length", /*contentLength*/contents.length);
    int rc = newRequest(uri);
    if (rc != Http.SC_CONTINUE)
    {
      readContent();
      return rc;
    }
    else
    {
      OutputStream out = getOutputStream();
//      pipe(contents, contentLength, out);
      out.write(contents);
      return putComplete();
    }
  }

  /**
   * Read the specified number of bytes off the given input
   * stream to the specified output stream.  This does not
   * close either the input or output stream.
   */
  public static void pipe(InputStream in, long size, OutputStream out)
    throws IOException
  {
    int len = 4096;
    byte[] buf = new byte[len];
    while(size > 0)
    {
      int n = in.read(buf, 0, (int)Math.min(size, len));
      if (n <= 0)
        throw new IOException("Unexpected EOF");
      out.write(buf, 0, n);
      size -= n;
    }
  }

  /**
   * Complete a post.
   */
  public int postComplete()
    throws IOException
  {
    if (!requestMethod.equals(Http.METHOD_POST))
      throw new IllegalStateException("Request was not a post.");

    out.flush();
    responseHeaderFields.clear();
    int rc = readResponse(in);

    return rc;
  }

  public int putComplete()
    throws IOException
  {
    if (!requestMethod.equals(Http.METHOD_PUT))
      throw new IllegalStateException("Request was not a put.");

    out.flush();
    responseHeaderFields.clear();
    int rc = readResponse(in);

    return rc;
  }

  /**
   * Write the request to the output.
   */
  private void writeRequest(PrintWriter pw, String uri)
  {
    pw.print(requestMethod);
    pw.print(' ');
    pw.print(uri);//HttpUtil.encodeUrl(uri));
    pw.print(' ');
    pw.print(HTTP_VERSION);
    pw.print(Http.CRLF);

    // Write the user agent.
    String ua = (userAgent != null) ? userAgent : defUserAgent;
    if (ua != null)
    {
      pw.print("user-agent: ");
      pw.print(ua);
      pw.print(Http.CRLF);
    }

    // Write the host header
    pw.print("host: ");
    pw.print(hostHeader.toString());
    pw.print(Http.CRLF);

    for (int i = 0; i < requestHeaderFields.size(); i++)
    {
      NameValue nv = (NameValue)requestHeaderFields.elementAt(i);
      pw.print(nv.name);
      pw.print(": ");
      pw.print(nv.value);
      pw.print(Http.CRLF);
    }

    pw.print(Http.CRLF);
    pw.flush();
    requestComplete = true;

    responseHeaderFields.clear();
  }

  /**
   * Read the response from the input.
   */
  /*private*/ int readResponse(InputStream in)
    throws IOException
  {
    statusLine = new StatusLine(in);

    StringBuffer sbuf = new StringBuffer(30);
//    boolean done = false;
    while(true)
    {
      String name  = null;
      String value = null;
      sbuf.setLength(0);

      int ch = in.read();
      if ((ch == -1) || (ch == Http.CR))
      {
        ch = in.read();
        break;
      }

      while ((ch != -1) && (ch != ':'))
      {
        sbuf.append((char)ch);
        ch = in.read();
      }
      name = sbuf.toString();

      ch = in.read();
      while ((ch != -1) && (ch == ' '))
        ch = in.read();

      sbuf.setLength(0);
      while ((ch != -1) && (ch != Http.CR))
      {
        sbuf.append((char)ch);
        ch = in.read();
      }
      ch = in.read();

      value = sbuf.toString();

      if ((name != null) && (value != null))
        responseHeaderFields.put(name.toLowerCase(), value);
    }

    String connHeader = getResponseHeader("Connection");
    if ((connHeader != null) && connHeader.equalsIgnoreCase("close"))
    {
      if (statusLine.getStatusCode() != Http.SC_MOVED_TEMPORARILY)
        allowMultipleRequests = false;
    }

    return statusLine.getStatusCode();
  }

  /**
   * Get the version of the HTTP response.
   */
  public String getResponseVersion()
  {
    return statusLine.getVersion();
  }

  /**
   * Get the HTTP response status code.
   */
  public int getStatusCode()
  {
    return statusLine.getStatusCode();
  }

  /**
   * Get the HTTP response status message.
   */
  public String getStatusMessage()
  {
    return statusLine.getMessage();
  }

  /**
   * Get the content type.
   */
  public String getContentType()
  {
    return getResponseHeader("Content-Type");
  }

  /**
   * Get the content length.
   */
  public int getContentLength()
  {
    return getResponseHeaderInt("Content-Length");
  }

  /**
   * Read the content of the current response from the input.
   */
  public byte[] readContent()
    throws IOException
  {
    byte[] result = null;
    int len = getContentLength();

    if (len != -1)
    {
      result = new byte[len];
      DataInputStream dataIn = new DataInputStream(in);
      dataIn.readFully(result);
    }
    else
    {
      byte[] temp = new byte[1024];
      ByteArrayOutputStream bOut = new ByteArrayOutputStream(1024);
      int thisRead = 0;
      while((thisRead = in.read(temp)) != -1)
        bOut.write(temp, 0, thisRead);

      result = bOut.toByteArray();
    }

    return result;
  }

  /**
   * Get the value of the specified request header.
   */
  public String getRequestHeader(String name)
  {
    if (requestHeaderFields == null)
      return null;

    for (int i = 0; i < requestHeaderFields.size(); i++)
    {
      NameValue nv = (NameValue)requestHeaderFields.elementAt(i);
      if (nv.name.equalsIgnoreCase(name))
        return nv.value;
    }

    return null;
  }

  /**
   * Get the specified header field value.
   */
  public String getResponseHeader(String name)
  {
    return (String)responseHeaderFields.get(name.toLowerCase());
  }

  /**
   * Get a list of the response header fields.
   */
  public Enumeration getResponseHeaderNames()
  {
    return responseHeaderFields.keys();
  }

  /**
   * Get the specified header field value as an int.
   */
  public int getResponseHeaderInt(String name)
  {
    String val = (String)responseHeaderFields.get(name.toLowerCase());
    if (val != null)
    {
      try
      {
        return Integer.parseInt(val);
      }
      catch(Exception e)
      {
        return -1;
      }
    }
    else
      return -1;
  }

  /**
   * Get the specified header field value as a date.
   */
  public long getResponseHeaderDate(String name)
  {
    String val = (String)responseHeaderFields.get(name.toLowerCase());
    if (val != null)
    {
      try
      {
        return HttpDateFormat.parse(val);
      }
      catch(Exception e)
      {
        return -1;
      }
    }
    else
      return -1;
  }

  /**
   * Get an input stream for reading from this connection.
   */
  public InputStream getInputStream()
    throws IOException
  {
    if (!isOpen())
      throw new IOException("Connection is not open");

    int contentLength = getResponseHeaderInt("Content-Length");
    String transferEncoding = getResponseHeader("Transfer-Encoding");
    if ((transferEncoding != null) &&
        transferEncoding.equalsIgnoreCase(Http.TRANSFER_CHUNKED))
      return new HttpInputStream(in, true);
    else
      return new HttpInputStream(in, contentLength);
  }

  /**
   * Get an output stream for writing to this connection.
   */
  public OutputStream getOutputStream()
    throws IOException
  {
    if (!isOpen())
      throw new IOException("Connection is not open");

    String transferEncoding = getRequestHeader("Transfer-Encoding");
    if ((transferEncoding != null) &&
        transferEncoding.equalsIgnoreCase(Http.TRANSFER_CHUNKED))
    {

      transferChunked = true;
      return new ChunkedOutputStream(out, 2048);
    }
    else
    {

      transferChunked = false;
      return out;
    }
  }

  /**
   * Is the connection open?
   */
  public boolean isTransferChunked()
  {
    return transferChunked;
  }


  /**
   * Is the connection open?
   */
  public boolean isOpen()
  {
    return s != null;
  }

  /**
   * Should this connection be closed after a single
   * request?
   */
  public boolean shouldClose()
  {
    return !allowMultipleRequests;
  }

  /**
   * Close the connection.
   */
  public void close()
  {
    if (s != null)
    {
      try {pw.flush();} catch(Exception e) {}
      try {in.close();} catch(Exception e) {}
      try {out.flush();} catch(Exception e) {}
      try {out.close();} catch(Exception e) {}
      try {s.close();} catch(Exception e) {}

      pw = null;
      in = null;
      out = null;
      s = null;
    }
  }

  /**
   * Dump the response header.
   */
  public String dumpResponseHeader()
  {
    StringBuffer sbuf = new StringBuffer(100);
    sbuf.append(statusLine).append('\n');
    Enumeration e = responseHeaderFields.keys();
    if (!e.hasMoreElements())
      return sbuf.toString();

    while (e.hasMoreElements())
    {
      String key = (String)e.nextElement();
      sbuf.append("   ")
          .append(key).append(": ")
          .append(responseHeaderFields.get(key)).append('\n');
    }

    return sbuf.toString();
  }

///////////////////////////////////////////////////////////
// Status checks
///////////////////////////////////////////////////////////

  /**
   * Make sure the returned status code is was OK (200).
   */
  public void checkOk()
    throws HttpException
  {
    checkStatus(Http.SC_OK);
  }

  /**
   * Make sure the returned status code is what
   * was expected.  If the actual status is
   * unexpected, the connection is closed and
   * an HttpException is thrown.
   */
  public void checkStatus(int expectedStatus)
    throws HttpException
  {
    int statusCode = getStatusCode();
    if (statusCode != expectedStatus)
    {
      close();
      throw new HttpException(expectedStatus, statusCode);
    }
  }

///////////////////////////////////////////////////////////
// Inner classes
///////////////////////////////////////////////////////////

  /**
   * The StatusLine handles parsing of the HTTP
   * response status line.
   */
  private class StatusLine
  {
    /**
     * Constructor.
     */
    public StatusLine(InputStream in)
      throws IOException
    {
      this.in = in;
      readVersion();
      readStatusCode();
      readMessage();
    }

    /**
     * Read the HTTP version.
     */
    protected void readVersion()
      throws IOException
    {
      StringBuffer sbuf = new StringBuffer(8);
      int ch = in.read();
      while ((ch != -1) && (ch != ' '))
      {
        sbuf.append((char)ch);
        ch = in.read();
      }

      if (ch == -1)
      {
        close();
        throw new EOFException("End of input while reading version.");
      }

      version = sbuf.toString();
    }

    /**
     * Get the HTTP version of the response.
     */
    public String getVersion()
    {
      return version;
    }

    /**
     * Read the status code.
     */
    protected void readStatusCode()
      throws IOException
    {
      StringBuffer sbuf = new StringBuffer(3);
      int ch = in.read();
      while ((ch != -1) && (ch != ' '))
      {
        sbuf.append((char)ch);
        ch = in.read();
      }

      if (ch == -1)
      {
        close();
        throw new EOFException("End of input while reading status code.");
      }

      statusCode = Integer.parseInt(sbuf.toString());
    }

    /**
     * Get the status code.
     */
    public int getStatusCode()
    {
      return statusCode;
    }

    /**
     * Read the reason message.
     */
    protected void readMessage()
      throws IOException
    {
      StringBuffer sbuf = new StringBuffer(25);
      int ch = in.read();
      while ((ch != -1) && (ch != Http.CR))
      {
        sbuf.append((char)ch);
        ch = in.read();
      }

      if (ch == -1)
      {
        close();
        throw new EOFException("End of input while reading reason phrase.");
      }

      ch = in.read();
      message = sbuf.toString();
    }

    /**
     * Get the response message.
     */
    public String getMessage()
    {
      return message;
    }

    /**
     * Get this status line as a string.
     */
    public String toString()
    {
      return version + " " + statusCode + " " + message;
    }

  ///////////////////////////////////////////////////////////
  // Attributes
  ///////////////////////////////////////////////////////////

    private InputStream in;

    private String version;
    private int    statusCode;
    private String message;
  }

  /**
   * Encapsulation of a name/value pair.
   */
  private class NameValue
  {
    public NameValue(String name, String value)
    {
      this.name  = name;
      this.value = value;
    }

    public NameValue(String name, int value)
    {
      this.name  = name;
      this.value = Integer.toString(value);
    }

    public NameValue(String name, long value)
    {
      this.name  = name;
      this.value = String.valueOf(value);
    }

    public NameValue(String name, boolean value)
    {
      this.name = name;
      this.value = "" + value;
    }

    public String name;
    public String value;
  }

///////////////////////////////////////////////////////////
// Constants
///////////////////////////////////////////////////////////

  public static final String HTTP_VERSION = "HTTP/1.1";

///////////////////////////////////////////////////////////
// Attributes
///////////////////////////////////////////////////////////

  private String       startUri;
  private String       requestMethod = Http.METHOD_GET;
  private String       hostHeader;
  private StatusLine   statusLine;
  private boolean      allowMultipleRequests = true;
  private boolean      requestComplete = true;
  private boolean      transferChunked = false;
  private boolean      followRedirects = false;
  private boolean      redirected = false;

  private String userAgent;
  private static String defUserAgent = "";

  private Socket               s;
  private BufferedOutputStream out;
  private PrintWriter          pw;
  private InputStream          in;
  private int                  timeout = 0;
  private int                  port = Http.DEFAULT_HTTP_PORT;
  private String               hostname;

  private Vector    requestHeaderFields;
  private Hashtable responseHeaderFields;

}