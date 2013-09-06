/*
 * This code licensed to public domain
 */
package obix.net;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.net.ssl.HttpsURLConnection;

import obix.*;
import obix.io.Base64;
import obix.io.ObixDecoder;
import obix.io.ObixEncoder;

/**
 * ObixSession models a client side logic connection to an
 * obix server using the HTTP REST protocol.  Only HTTP basic
 * authentication is supported.
 *
 * @author    Brian Frank
 * @creation  12 Sept 05
 * @version   $Revision$ $Date$
 */
public class ObixSession
{

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////

  /**
   * Construct a session where uri specifies the lobby
   * URL of a obix server.  For example lobby typically
   * looks like "http://foo/obix".  Username and password
   * provide the credentials needed to access the server.
   */
  public ObixSession(Uri lobbyUri, String username, String password)
  {
    this.authority  = lobbyUri.getAuthorityUri();
    this.lobbyUri   = lobbyUri;
    this.username   = username;
    this.password   = password;
    this.authHeader = "Basic " + Base64.encode(username + ":" + password);
  }

  /**
   * Construct a session where uri specifies the lobby
   * URL of a obix server.  For example lobby typically
   * looks like "http://foo/obix".  Username and password
   * provide the credentials needed to access the server.
   * @param lobbyUri URI of oBIX Lobby
   * @param username
   * @param password
   * @param timeout the timeout used for both connectTimeout and readTimeout (default is 0).
   */
  public ObixSession(Uri lobbyUri, String username, String password, int timeout)
  {
    this.authority  = lobbyUri.getAuthorityUri();
    this.lobbyUri   = lobbyUri;
    this.username   = username;
    this.password   = password;
    this.authHeader = "Basic " + Base64.encode(username + ":" + password);
    this.timeout = timeout;
  }



////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

  /**
   * Get the authority uri of this session, which is
   * basically the domain name: "http://host/".
   * All requests to this session must be within the
   * authority see the contains(Uri) method.
   */
  public Uri getAuthority()
  {
    return authority;
  }

  /**
   * Return if the specified uri can be processed
   * by this session.  A session can only process uris
   * within it's authority.  For example a session for
   * http://foo/ cannot be used to process requests
   * for http://bar/.  This method will return true
   * for any relative uri.  This method is really a
   * convenience for getAuthority().contains(uri).
   */
  public boolean contains(Uri uri)
  {
    return authority.contains(uri);
  }

  /**
   * Return the lobby uri of this session.
   */
  public Uri getLobbyUri()
  {
    return lobbyUri;
  }

  /**
   * Return the lobby object.  This object is first
   * read on session open().
   */
  public Obj getLobby()
  {
    if (lobby == null) throw new IllegalStateException("Session not open");
    return lobby;
  }

  /**
   * Get username we are using to authenticate.
   */
  public String getUsername()
  {
    return username;
  }

  /**
   * Return authority string.
   */
  public String toString()
  {
    return authority.toString();
  }

  public int getTimeout() { return timeout; }

  
////////////////////////////////////////////////////////////////
// Lifecycle
////////////////////////////////////////////////////////////////

  /**
   * Open a session.
   */
  public void open()
    throws Exception
  {
    // read lobby
    lobby = read(lobbyUri);

    // batch URI
    batchUri = lobby.get("batch").getNormalizedHref();

    // read about
    Obj about = lobby.get("about");
    if (about != null && about.getHref() != null)
      this.about = read(about.getNormalizedHref());

    // read watch service
    Obj watchService = lobby.get("watchService");
    if (watchService != null && watchService.getHref() != null)
      this.watchService = read(watchService.getNormalizedHref());
  }

  /**
   * Close a session.
   */
  public void close()
  {
  }

  
////////////////////////////////////////////////////////////////
// Requests
////////////////////////////////////////////////////////////////

  /**
   * Attempt to read the server's about object.  If
   * successful return it, otherwise throw an exception.
   */
  public Obj ping()
    throws Exception
  {
    return read(about.getNormalizedHref());
  }

  /**
   * Read the obix document as an Obj instance using the
   * specified uri relative to the base address of the sesssion.
   */
  public Obj read(Uri uri)
    throws Exception
  {
    return new ObixDecoder(open(uri)).decodeDocument();
  }

  /**
   * Write the specified obj back to the server using the
   * obj's href.  Return a new object containing the
   * server's result.
   */
  public Obj write(Obj obj)
    throws Exception
  {
    Uri href = obj.getNormalizedHref();
    if (href == null) throw new Exception("obj.href is null");

    return send(href, "PUT", obj);
  }

  /**
   * Convenience for <code>invoke(op.getHref(), in)</code>.
   */
  public Obj invoke(Op op, Obj in)
    throws Exception
  {
    Uri href = op.getNormalizedHref();
    if (href == null) throw new Exception("op.href is null");

    return invoke(href, in);
  }

  /**
   * Invoke the op at the given href with the given input
   * parameter and return the output parameter.
   */
  public Obj invoke(Uri href, Obj in)
    throws Exception
  {
    if (in == null) { in = new Obj(); in.setNull(true); }
    return send(href, "POST", in);
  }

  /**
   * Make a BatchIn request which can be used to store
   * up a bunch of read, write, invoke requests then
   * sent over the network in one shot by calling
   * BatchIn.commit().
   */
  public BatchIn makeBatch()
    throws Exception
  {
    if (batchUri == null)
      throw new Exception("Lobby missing batch op");
    return new BatchIn(this);
  }

  /**
   * Perform an HTTP get request on the specified uri relative
   * to the base address of the session and return an input
   * stream to read the resource.
   */
  public InputStream open(Uri uri)
    throws Exception
  {
    if (useHttpURLConnection)
    {
      HttpURLConnection conn = setupHttpURLConnection(uri,null);
      conn.connect();
      int rc = conn.getResponseCode();
      if ((rc / 200) != 1)
        throw new IOException("HTTP connection failed resp code=" + rc);
      return conn.getInputStream();
    }
    else
    {
      HttpConnection conn = setupHttpConnection(uri);
      conn.connect();
      int rc = conn.getStatusCode();
      if ((rc / 200) != 1)
        throw new IOException("HTTP connection failed resp code=" + rc);
      return conn.getInputStream();
    }
  }

////////////////////////////////////////////////////////////////
// Subscriptions
////////////////////////////////////////////////////////////////

  /**
   * Get the list of active watches for this session.
   */
  public SessionWatch[] getWatches()
  {
    return (SessionWatch[])watches.values().toArray(new SessionWatch[watches.size()]);
  }

  /**
   * Get a watch by name or return null if no watched
   * mapped to the specified name.
   */
  public SessionWatch getWatch(String name)
  {
    return (SessionWatch)watches.get(name);
  }

  /**
   * Convenience for <code>makeWatch(name, pollPeriod, null)</code>.
   */
  public SessionWatch makeWatch(String name, long pollPeriod)
    throws Exception
  {
    return makeWatch(name, pollPeriod, null);
  }

  /**
   * Make a new empty Watch for this session.  Name is unique key
   * used as programmer's moniker for debugging (used by UI code too).
   * The pollPeriod is used to specify how fast the watch should
   * poll for changes.  This value will automatically be used to
   * request a longer lease time if needed.  If listener is not null,
   * automatically add it to receive changed callbacks.
   */
  public SessionWatch makeWatch(String name, long pollPeriod, WatchListener listener)
    throws Exception
  {
    if (watches.get(name) != null)
      throw new IllegalArgumentException("Duplicate watch name: " + name);
    SessionWatch watch = SessionWatch.make(this, name, pollPeriod);
    watches.put(name, watch);
    if (listener != null) watch.addListener(listener);
    return watch;
  }

////////////////////////////////////////////////////////////////
// HTTP
////////////////////////////////////////////////////////////////

  /**
   * Return a fully configured HttpURLConnection for the given URI and
   * HTTP method.
   */
  protected HttpURLConnection setupHttpURLConnection(Uri uri, String method)
    throws Exception
  {
    Uri abs = uri.normalize(lobbyUri);

    if (!contains(abs))
      throw new IllegalArgumentException("Uri not contained by this session: " + abs);

    HttpURLConnection conn = (HttpURLConnection)abs.toURL().openConnection();
    conn.setInstanceFollowRedirects(false);
    conn.setRequestProperty("Authorization", authHeader);

    // Check VM version to see if we can set timeouts.
    String vmVersion = System.getProperty("java.vm.version", "");
    if (vmVersion.startsWith("1."))
    {
      StringTokenizer st = new StringTokenizer(vmVersion, ".");
      st.nextToken();
      int minorVersion = Integer.parseInt(st.nextToken());
      if (minorVersion >= 5)
      {
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);
      }
    }

    // Configure POST and PUT
    if ((method != null) && !method.equalsIgnoreCase("GET"))
    {
      conn.setDoOutput(true);
      conn.setDoInput(true);
      conn.setRequestMethod(method);
      conn.setRequestProperty("Content-Type","text/xml");
    }
    // Enable anonymous SSL.  If the platform doesn't have
    // javax.net.ssl, the exception will set the testHttps flag
    // therefore bypassing future classloader exceptions.
    if (testHttps)
    {
      testHttps = false;
      try
      {
        if (conn instanceof HttpsURLConnection)
        {
          Class clazz = Class.forName("obix.net.AnonymousVerifier");
          Constructor init =
              clazz.getConstructor(new Class[] {URLConnection.class});
          init.newInstance(new Object[] {conn});
        }
        testHttps = true;
      }
      catch (Throwable t) {}
    }
    return conn;
  }

  /**
   * Return a fully configured HttpConnection for the given URI.
   */
  protected HttpConnection setupHttpConnection(Uri uri)
    throws Exception
  {
    Uri abs = uri.normalize(lobbyUri);

    if (!contains(abs))
      throw new IllegalArgumentException("Uri not contained by this session: " + abs);

    String noAuth = uri.get();
    noAuth = noAuth.substring(noAuth.indexOf(uri.getPath()));
    HttpConnection conn = new HttpConnection(uri.getHost(), uri.getPort(), noAuth);
    conn.setFollowRedirects(false);
    conn.setRequestHeader("Authorization", authHeader);
    conn.setTimeout(timeout);

    return conn;
  }

  /**
   * POST or PUT the specified payload to the given uri.
   */
  private Obj send(Uri href, String method, Obj payload)
    throws Exception
  {
    if (useHttpURLConnection)
    {
      HttpURLConnection conn = setupHttpURLConnection(href,method);
      conn.connect();
      OutputStream out = conn.getOutputStream();
      try
      {
        // encode request
        new ObixEncoder(out).encodeDocument(payload);
        out.flush();

        // read response
        int rc = conn.getResponseCode();
        if ((rc / 200) != 1)
          throw new Exception("Invalid response code " + rc);
        InputStream in = conn.getInputStream();
        Obj result = new ObixDecoder(in).decodeDocument();
        try {in.close();} catch (Exception x) {}
        if (result instanceof Err) throw new ErrException((Err)result);

        // if the result doesn't have an href, then use
        // server authority for normalization puroseses
        if (result.getHref() == null)
          result.setHref(authority);

        return result;
      }
      finally
      {
        try {out.close();} catch (Exception x) {}
      }
    }
    else
    {
      OutputStream out = null;
      int rc = -1;
      try
      {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        new ObixEncoder(os).encodeDocument(payload);
        HttpConnection conn = setupHttpConnection(href);
        if (method.equalsIgnoreCase("PUT"))
          rc = conn.put(href.get(), "text/xml", os.toByteArray());
        else
          rc = conn.post(href.get(), "text/xml", os.toByteArray());

        if ((rc / 200) != 1)
          throw new Exception("Invalid response code " + rc);

        InputStream in = conn.getInputStream();
        Obj result = new ObixDecoder(in).decodeDocument();
        try {in.close();} catch (Exception x) {}
        if (result instanceof Err) throw new ErrException((Err)result);

        // if the result doesn't have an href, then use
        // server authority for normalization puroseses
        if (result.getHref() == null)
          result.setHref(authority);

        return result;
      }
      finally
      {
        try {if (out != null) out.close();} catch (Exception x) {}
      }
    }
  }


////////////////////////////////////////////////////////////////
// Main
////////////////////////////////////////////////////////////////

  /**
   * Provides a simple interactive command line tool which reads obix
   * objects using an ObixSession and dumps the result to stdout.
   */
  public static void main(String args[])
    throws Exception
  {
    // parse command line into params
    if (args.length < 3)
    {
      System.out.println("usage: ObixSession <addr> <user> <pass>");
      System.out.println("  Iterative utility to read and dump obix objects");
      System.out.println("Params:");
      System.out.println("  <addr>   URL of the obix server's lobby object");
      System.out.println("  <user>   Username to use for HTTP basic authentication");
      System.out.println("  <pass>   Password to use for HTTP basic authentication");
      return;
    }
    Uri lobbyUri = new Uri(args[0]);
    String user = args[1];
    String pass = args[2];

    // map to session
    ObixSession session = new ObixSession(lobbyUri, user, pass);
    Obj cur = dumpObj(session, lobbyUri);

    // command loop
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    while(true)
    {
      // prompt for new command
      System.out.println();
      System.out.print("" + cur.getHref().get() + "> ");
      String cmd = in.readLine().trim();

      // process commands
      if (cmd.equals("")) continue;
      else if (cmd.equals("quit") || cmd.equals("bye")) break;

      // otherwise assume it is uri to attempt to read
      Uri uri = new Uri(cmd).normalize(cur.getHref());

      // read and dump current uri
      try
      {
        cur = dumpObj(session, uri);
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
    }
  }

  static void dumpContent(ObixSession session, Uri uri)
    throws Exception
  {
    InputStream in = session.open(uri);
    int c;
    while((c = in.read()) >= 0) System.out.print((char)c);
    System.out.println();
  }

  static Obj dumpObj(ObixSession session, Uri uri)
    throws Exception
  {
    System.out.println("-- Read [" + uri + "]");

    long t1 = System.currentTimeMillis();
    Obj obj = session.read(uri);
    long t2 = System.currentTimeMillis(); // time to read, not dump too

    ObixEncoder.dump(obj);
    System.out.println("-- Success [Read " + (t2-t1) + "ms]");
    return obj;
  }

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  Uri authority;
  Uri lobbyUri;
  Uri batchUri;
  Obj lobby;
  Obj about;
  Obj watchService;
  String username;
  String password;
  String authHeader;
  HashMap watches = new HashMap();
  boolean testHttps = true;
  boolean useHttpURLConnection = true;
  int timeout = 0;

}
