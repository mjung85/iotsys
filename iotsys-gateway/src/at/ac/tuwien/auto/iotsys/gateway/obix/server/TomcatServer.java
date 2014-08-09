package at.ac.tuwien.auto.iotsys.gateway.obix.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import obix.Obj;
import obix.io.BinObixDecoder;
import obix.io.BinObixEncoder;
import obix.io.ObixDecoder;
import obix.io.ObixEncoder;
import obix.io.RelativeObixEncoder;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONException;

import at.ac.tuwien.auto.iotsys.commons.PropertiesLoader;
import at.ac.tuwien.auto.iotsys.commons.interceptor.InterceptorBroker;
import at.ac.tuwien.auto.iotsys.commons.interceptor.InterceptorRequest;
import at.ac.tuwien.auto.iotsys.commons.interceptor.InterceptorRequestImpl;
import at.ac.tuwien.auto.iotsys.commons.interceptor.InterceptorResponse;
import at.ac.tuwien.auto.iotsys.commons.interceptor.InterceptorResponse.StatusCode;
import at.ac.tuwien.auto.iotsys.commons.interceptor.Parameter;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.User;
import at.ac.tuwien.auto.iotsys.gateway.interceptor.InterceptorBrokerImpl;
import at.ac.tuwien.auto.iotsys.gateway.util.ExiUtil;
import at.ac.tuwien.auto.iotsys.gateway.util.JsonUtil;

public class TomcatServer {

	private Tomcat tomcat;

	private static final Logger log = Logger.getLogger(TomcatServer.class
			.getName());

	private String password = "123456";
	private String alias = "tomcat";
	// private String certificatePath = "ssl/certs/tomcatcert.cer";
	private String keyStorePath = "ssl/certs/tomcatkey.jks";
	private String keyStoreType = "JKS";

	public TomcatServer(int port, boolean enableClientCert,
			boolean enableAuthen, ObixServer obixServer) throws IOException,
			ServletException {

		this.tomcat = new Tomcat();

//		tomcat.setPort(port);

		tomcat.setBaseDir(".");

		Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());

		Connector connector = new Connector();
		connector.setPort(port);
		connector.setProtocol("HTTP/1.1");
		connector.setScheme("https");
		connector.setAttribute("SSLEnabled", true);
		connector.setAttribute("sslProtocol", "TLS");

		connector.setSecure(true);
		connector.setAttribute("keyAlias", alias);
		connector.setAttribute("keystorePass", password);
		connector.setAttribute("keystoreFile", keyStorePath);
		connector.setAttribute("keystoreType", keyStoreType);

		if (enableClientCert) {
			connector.setAttribute("clientAuth", "true");
			connector.setAttribute("truststorePass", password);
			connector.setAttribute("truststoreFile", keyStorePath);
		}

		Service service = tomcat.getService();
		service.addConnector(connector);

		Connector defaultConnector = tomcat.getConnector();
		defaultConnector.setRedirectPort(8443);

		Tomcat.addServlet(ctx, "obix",
				new ObixServlet(enableAuthen, obixServer));
		ctx.addServletMapping("/*", "obix");
		Tomcat.addServlet(ctx, "uidb", new UIDbServlet(enableAuthen, obixServer));
		ctx.addServletMapping("/uidb/*", "uidb");

		try {
			tomcat.start();
			log.info("Tomcat Server is started!");
		} catch (LifecycleException e) {
			e.printStackTrace();
		}
		tomcat.getServer().await();

	}

	public class ObixServlet extends HttpServlet {

		private static final long serialVersionUID = 1L;

		/**
		 * Standard XML header
		 */
		public static final String XML_HEADER = "";

		private ExiUtil exiUtil = null;

		private ObixServer obixServer = null;

		private SOAPHandler soapHandler = null;

		private InterceptorBroker interceptorBroker = InterceptorBrokerImpl
				.getInstance();

		private boolean enableAuthen = false;

		String hostAddress = "127.0.0.1";
		String hostName = "localhost";

		public ObixServlet(boolean enableAuthen, ObixServer obixServer)
				throws IOException {
			try {
				this.exiUtil = ExiUtil.getInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.obixServer = obixServer;
			soapHandler = new SOAPHandler(this.obixServer);
			this.enableAuthen = enableAuthen;
		}

		@Override
		public void service(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			// Get request uri
			String uri = req.getRequestURI();

			// Get subject host address
			String subject = req.getRemoteAddr();
			
			if (enableAuthen) {
				if (uri.endsWith("authenticate")) {
					String username = req.getParameter("username");
					String password = req.getParameter("password");

					log.info("Service username : " + username);

					User u = obixServer.getUidb().getUser(username);
					if ((u != null) && u.getRole().equals("admin")){
						HttpSession session = req.getSession(true);
						session.setAttribute("authenticated", true);
						resp.sendRedirect("/");
						return;
					} else {
						resp.sendRedirect("/login_error");
						return;
					}
				} else if (uri.endsWith("logout")) {
					HttpSession session = req.getSession(true);
					session.setAttribute("authenticated", false);

					resp.sendRedirect("/");
					return;
				}
			}

			super.service(req, resp);

			log.info("Serving: " + uri + " for " + subject + " done.");
		}

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			String ipv6Address = "/" + getIPv6Address(req);
			String uri = req.getRequestURI();

			if (enableAuthen) {
				HttpSession session = req.getSession(true);
				if ((session.getAttribute("authenticated") == null || Boolean
						.parseBoolean(session.getAttribute("authenticated")
								.toString()) != true)
						&& !uri.endsWith("login_error")) {
					if (uri.endsWith("/")) {
						uri += "login";
					} else {
						uri += "/login";
					}
				}
			}

			String response = getDoGetResponse(req, resp, uri, ipv6Address);

			PrintWriter w = resp.getWriter();
			w.println(response);

			w.flush();
			w.close();
		}

		public String getDoGetResponse(HttpServletRequest req,
				HttpServletResponse resp, String uri, String ipv6Address) {
			StringBuffer obixResponse = null;
			Obj responseObj = null;
			String resourcePath = getResourcePath(uri, ipv6Address);

			String response = serveStatic(req, resp, uri, ipv6Address);
			if (response != null) {
				return response;
			}

			// call interceptors
			response = intercept(req, resp, hostName, hostAddress);
			if (response != null) {
				return response;
			}

			try {
				responseObj = obixServer.readObj(new URI(resourcePath), true);
				obixResponse = getObixResponse(req, ipv6Address, responseObj,
						resourcePath);
				response = encodeResponse(req, resp, uri, obixResponse);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

			return response;
		}

		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			StringBuffer obixResponse = null;
			Obj responseObj = null;
			String ipv6Address = "/" + getIPv6Address(req);
			String uri = req.getRequestURI();
			String data = getData(req);
			String resourcePath = getResourcePath(uri, ipv6Address);

			if (uri.endsWith("soap")) {
				log.finest("Forward to SOAP handler!");

				obixResponse = new StringBuffer(soapHandler.process(data,
						req.getHeader("soapaction")));
				log.finest("oBIX Response: " + obixResponse);

			} else {

				if (enableAuthen) {
					HttpSession session = req.getSession(true);
					if (session.getAttribute("authenticated") != null
							&& Boolean.parseBoolean(session.getAttribute(
									"authenticated").toString()) == true) {

						try {
							responseObj = obixServer.invokeOp(new URI(
									resourcePath), data);
							obixResponse = getObixResponse(req, ipv6Address,
									responseObj, resourcePath);
						} catch (URISyntaxException e) {
							e.printStackTrace();
						}
					} else {
						resp.sendRedirect("/");
						return;
					}
				} else {
					try {
						responseObj = obixServer.invokeOp(new URI(
								resourcePath), data);
						obixResponse = getObixResponse(req, ipv6Address,
								responseObj, resourcePath);
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				}
			}

			String response = encodeResponse(req, resp, uri, obixResponse);

			PrintWriter w = resp.getWriter();
			w.println(response);

			w.flush();
			w.close();
		}

		@Override
		protected void doPut(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {

			StringBuffer obixResponse = null;
			Obj responseObj = null;
			String ipv6Address = "/" + getIPv6Address(req);
			String uri = req.getRequestURI();
			String data = getData(req);
			String resourcePath = getResourcePath(uri, ipv6Address);

			try {
				responseObj = obixServer.writeObj(new URI(resourcePath), data);
				obixResponse = getObixResponse(req, ipv6Address, responseObj,
						resourcePath);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

			String response = encodeResponse(req, resp, uri, obixResponse);

			PrintWriter w = resp.getWriter();
			w.println(response);

			w.flush();
			w.close();
		}

		private String encodeResponse(HttpServletRequest req,
				HttpServletResponse resp, String uri, StringBuffer obixResponse) {
			String response = null;

			boolean exiRequested = exiUtil != null
					&& contains(req.getHeader("Accept"), MIME_EXI);
			boolean exiSchemaRequested = contains(req.getHeader("Accept"),
					MIME_DEFAULT_BINARY);
			boolean obixBinaryRequested = contains(req.getHeader("Accept"),
					MIME_X_OBIX_BINARY);
			boolean jsonRequested = contains(req.getHeader("Accept"), MIME_JSON);

			if (exiRequested || exiSchemaRequested) {
				try {
					byte[] exiData = ExiUtil.getInstance().encodeEXI(
							XML_HEADER + obixResponse, exiSchemaRequested);
					// try to decode it immediately
					resp.setStatus(HttpStatus.SC_OK);
					resp.setContentType(MIME_EXI);
					InputStream in = new ByteArrayInputStream(exiData);

					int pending = in.available();

					OutputStream out = resp.getOutputStream();

					byte[] buff = new byte[exiData.length];
					while (pending > 0) {
						int read = in.read(buff, 0,
								((pending > exiData.length) ? exiData.length
										: pending));
						if (read <= 0)
							break;
						out.write(buff, 0, read);
						pending -= read;
					}

					PrintWriter w = new PrintWriter(out);

					w.flush();
					w.close();

				} catch (Exception e1) {
					e1.printStackTrace();
					// fall back
					resp.setStatus(HttpStatus.SC_OK);
					resp.setContentType(MIME_XML);
					response = obixResponse.toString();
				}
			} else if (obixBinaryRequested) {
				byte[] obixBinaryData = BinObixEncoder.toBytes(
						ObixDecoder.fromString(obixResponse.toString()),
						req.getHeader("accept-language"));
				try {
					resp.setStatus(HttpStatus.SC_OK);
					resp.setContentType(MIME_X_OBIX_BINARY);
					response = new String(obixBinaryData, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else if (jsonRequested) {
				try {
					String jsonData = JsonUtil.fromXMLtoJSON(obixResponse
							.toString());

					resp.setStatus(HttpStatus.SC_OK);
					resp.setContentType(MIME_JSON);
					resp.addHeader("Content-Length",
							"" + String.valueOf(jsonData).length());
					response = XML_HEADER + jsonData;
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			} else {
				if (uri.endsWith(".well-known/core")) {
					resp.setStatus(HttpStatus.SC_OK);
					resp.setContentType(MIME_XML);
					response = obixResponse.toString();
				} else {
					// response with content-length
					resp.setStatus(HttpStatus.SC_OK);
					resp.setContentType(MIME_XML);
					resp.addHeader("Content-Length",
							"" + String.valueOf(obixResponse).length());
					response = XML_HEADER + obixResponse.toString();
				}
			}
			return response;
		}

		public boolean contains(String hearder, String element) {

			StringTokenizer st = new StringTokenizer(hearder, ",");

			while (st.hasMoreTokens()) {
				String s = st.nextToken();

				if (s.equalsIgnoreCase(element)) {
					return true;
				}
			}

			return false;
		}

		private String getIPv6Address(HttpServletRequest req) {

			String localSocket = req.getLocalAddr().toString();
			String localSocketSplitted = "";

			int lastIndex = localSocket.lastIndexOf(":");

			if (lastIndex > 0) {
				localSocketSplitted = localSocket.substring(0, lastIndex);
			}

			lastIndex = localSocketSplitted.lastIndexOf("%");

			if (lastIndex > 0) {
				localSocketSplitted = localSocketSplitted.substring(0,
						lastIndex);
			}

			if (localSocketSplitted != "") {
				String splittedString = localSocketSplitted.substring(1);
				return splittedString;
			} else {
				return localSocketSplitted;
			}
		}

		private String serveStatic(HttpServletRequest req,
				HttpServletResponse resp, String uri, String ipv6Address) {

			String host = req.getHeader("host");
			String path = getResourcePath(uri, ipv6Address);

			if (path.endsWith("soap") && req.getParameter("wsdl") != null) {
				// serve wsdl file
				resp.setStatus(HttpStatus.SC_OK);
				resp.setContentType(MIME_XML);
				return soapHandler
						.getWSDLFileContent()
						.replaceAll("localhost", host)
						.replaceAll("./obix.xsd",
								"http://" + host + path + "?xsd=1");

			} else if (path.endsWith("soap") && req.getParameter("xsd") != null) {
				// serve schema file
				resp.setStatus(HttpStatus.SC_OK);
				resp.setContentType(MIME_XML);
				return soapHandler.getSchemaFileContent();
			}

			if (obixServer.containsIPv6(ipv6Address))
				return null;

			if (path.endsWith(".well-known/core")) {
				resp.setStatus(HttpStatus.SC_OK);
				resp.setContentType(MIME_PLAINTEXT);
				return obixServer.getCoRELinks();

			} else if (path.equalsIgnoreCase("/") || path.isEmpty()
					|| path.endsWith(".js") || path.endsWith(".css")) {
				if (path.isEmpty()) {
					path = "/index.html";
				}

				log.info("[serveStatic] path : " + path);

				return serveFile(req, resp, path, new File("res/obelix"), false);
			} else if (path.endsWith("login")) {
				path = "/";
				return serveFile(req, resp, path, new File("res/login"), false);
			} else if (path.endsWith("login_error")) {
				path = "/";
				return serveFile(req, resp, path, new File("res/login_error"),
						false);
			}

			return null;
		}

		/**
		 * Serves file from homeDir and its' subdirectories (only). Uses only
		 * URI, ignores all headers and HTTP parameters.
		 */
		public String serveFile(HttpServletRequest req,
				HttpServletResponse resp, String uri, File homeDir,
				boolean allowDirectoryListing) {
			String response = null;

			log.info("Serve file : " + uri);

			// Make sure we won't die of an exception later
			if (!homeDir.isDirectory()) {
				resp.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
				resp.setContentType(MIME_PLAINTEXT);
				return response = "INTERNAL ERRROR: serveFile(): given homeDir is not a directory.";
			}

			if (response == null) {
				// Remove URL arguments
				uri = uri.trim().replace(File.separatorChar, '/');
				if (uri.indexOf('?') >= 0)
					uri = uri.substring(0, uri.indexOf('?'));

				// Prohibit getting out of current directory
				if (uri.startsWith("..") || uri.endsWith("..")
						|| uri.indexOf("../") >= 0) {

					resp.setStatus(HttpStatus.SC_FORBIDDEN);
					resp.setContentType(MIME_PLAINTEXT);
					return response = "FORBIDDEN: Won't serve ../ for security reasons.";
				}
			}

			File f = new File(homeDir, uri);
			if (response == null && !f.exists()) {
				resp.setStatus(HttpStatus.SC_NOT_FOUND);
				resp.setContentType(MIME_PLAINTEXT);
				return response = "Error 404, file not found.";
			}

			// List the directory, if necessary
			if (response == null && f.isDirectory()) {
				// Browsers get confused without '/' after the
				// directory, send a redirect.
				if (!uri.endsWith("/")) {
					uri += "/";
					resp.setStatus(HttpStatus.SC_MOVED_PERMANENTLY);
					resp.setContentType(MIME_HTML);
					resp.addHeader("Location", uri);
					return response = "<html><body>Redirected: <a href=\""
							+ uri + "\">" + uri + "</a></body></html>";
				}

				if (response == null) {
					// First try login.html
					if (new File(f, "login.html").exists())
						f = new File(homeDir, uri + "/login.html");
					else if (new File(f, "login-failed.html").exists())
						f = new File(homeDir, uri + "/login-failed.html");

					// Then try index.html and index.htm
					else if (new File(f, "index.html").exists())
						f = new File(homeDir, uri + "/index.html");
					else if (new File(f, "index.htm").exists())
						f = new File(homeDir, uri + "/index.htm");

					// No index file, list the directory if it is readable
					else if (allowDirectoryListing && f.canRead()) {
						String[] files = f.list();
						String msg = "<html><body><h1>Directory " + uri
								+ "</h1><br/>";

						if (uri.length() > 1) {
							String u = uri.substring(0, uri.length() - 1);
							int slash = u.lastIndexOf('/');
							if (slash >= 0 && slash < u.length())
								msg += "<b><a href=\""
										+ uri.substring(0, slash + 1)
										+ "\">..</a></b><br/>";
						}

						if (files != null) {
							for (int i = 0; i < files.length; ++i) {
								File curFile = new File(f, files[i]);
								boolean dir = curFile.isDirectory();
								if (dir) {
									msg += "<b>";
									files[i] += "/";
								}

								msg += "<a href=\"" + encodeUri(uri + files[i])
										+ "\">" + files[i] + "</a>";

								// Show file size
								if (curFile.isFile()) {
									long len = curFile.length();
									msg += " &nbsp;<font size=2>(";
									if (len < 1024)
										msg += len + " bytes";
									else if (len < 1024 * 1024)
										msg += len / 1024 + "."
												+ (len % 1024 / 10 % 100)
												+ " KB";
									else
										msg += len / (1024 * 1024) + "." + len
												% (1024 * 1024) / 10 % 100
												+ " MB";

									msg += ")</font>";
								}
								msg += "<br/>";
								if (dir)
									msg += "</b>";
							}
						}
						msg += "</body></html>";
						resp.setStatus(HttpStatus.SC_OK);
						resp.setContentType(MIME_HTML);
						return response = msg;
					} else {
						resp.setStatus(HttpStatus.SC_FORBIDDEN);
						resp.setContentType(MIME_PLAINTEXT);
						return response = "FORBIDDEN: No directory listing.";
					}
				}
			}

			if (response == null) {
				// Get MIME type from file name extension, if possible
				String mime = null;
				int dot;
				try {
					dot = f.getCanonicalPath().lastIndexOf('.');
					if (dot >= 0)
						mime = (String) theMimeTypes.get(f.getCanonicalPath()
								.substring(dot + 1).toLowerCase());
					if (mime == null)
						mime = MIME_DEFAULT_BINARY;
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				// Calculate etag
				String etag = Integer.toHexString((f.getAbsolutePath()
						+ f.lastModified() + "" + f.length()).hashCode());

				// Support (simple) skipping:
				long startFrom = 0;
				long endAt = -1;
				String range = req.getHeader("range");
				if (range != null) {
					if (range.startsWith("bytes=")) {
						range = range.substring("bytes=".length());
						int minus = range.indexOf('-');
						try {
							if (minus > 0) {
								startFrom = Long.parseLong(range.substring(0,
										minus));
								endAt = Long.parseLong(range
										.substring(minus + 1));
							}
						} catch (NumberFormatException nfe) {
						}
					}
				}

				long fileLen = f.length();
				if (range != null && startFrom >= 0) {
					if (startFrom >= fileLen) {
						resp.setStatus(HttpStatus.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
						resp.setContentType(MIME_PLAINTEXT);
						resp.addHeader("Content-Range", "bytes 0-0/" + fileLen);
						resp.addHeader("ETag", etag);
						return response = "";
					} else {
						if (endAt < 0)
							endAt = fileLen - 1;
						long newLen = endAt - startFrom + 1;
						if (newLen < 0)
							newLen = 0;

						final long dataLen = newLen;
						FileInputStream fis = null;
						try {
							fis = new FileInputStream(f) {
								public int available() throws IOException {
									return (int) dataLen;
								}
							};
							fis.skip(startFrom);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

						resp.setStatus(HttpStatus.SC_PARTIAL_CONTENT);
						resp.setContentType(mime);
						resp.addHeader("Content-Length", "" + dataLen);
						resp.addHeader("Content-Range", "bytes " + startFrom
								+ "-" + endAt + "/" + fileLen);
						resp.addHeader("ETag", etag);
						try {
							return fisToString(fis);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else {
					if (etag.equals(req.getHeader("if-none-match"))) {
						resp.setStatus(HttpStatus.SC_NOT_MODIFIED);
						resp.setContentType(mime);
						return response = "";
					} else {
						resp.setStatus(HttpStatus.SC_OK);
						resp.setContentType(mime);
						resp.addHeader("Content-Length", "" + fileLen);
						resp.addHeader("ETag", etag);

						try {
							return response = fisToString(new FileInputStream(f));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

			resp.addHeader("Accept-Ranges", "bytes");

			return response;
		}

		public String fisToString(FileInputStream fis) throws IOException {
			StringBuilder builder = new StringBuilder();
			int ch;
			while ((ch = fis.read()) != -1) {
				builder.append((char) ch);
			}

			return builder.toString();
		}

		/**
		 * URL-encodes everything between "/"-characters. Encodes spaces as
		 * '%20' instead of '+'.
		 */
		private String encodeUri(String uri) {
			String newUri = "";
			StringTokenizer st = new StringTokenizer(uri, "/ ", true);
			while (st.hasMoreTokens()) {
				String tok = st.nextToken();
				if (tok.equals("/"))
					newUri += "/";
				else if (tok.equals(" "))
					newUri += "%20";
				else {
					// newUri += URLEncoder.encode(tok);
					// For Java 1.4 you'll want to use this instead:
					try {
						newUri += URLEncoder.encode(tok, "UTF-8");
					} catch (java.io.UnsupportedEncodingException uee) {
					}
				}
			}
			return newUri;
		}

		private String intercept(HttpServletRequest req,
				HttpServletResponse resp, String hostName, String hostAddress) {

			boolean interceptorsActive = Boolean.parseBoolean(PropertiesLoader
					.getInstance().getProperties()
					.getProperty("iotsys.gateway.interceptors.enable", "true"));

			if (!interceptorsActive || interceptorBroker == null
					|| !interceptorBroker.hasInterceptors())
				return null;

			log.info("Interceptors found ... starting to prepare.");

			InterceptorRequest interceptorRequest = new InterceptorRequestImpl();
			HashMap<Parameter, String> interceptorParams = new HashMap<Parameter, String>();

			String uri = req.getRequestURI();
			String subject = req.getRemoteAddr();
			String host = req.getHeader("host");
			String resource = "http://" + host + uri;

			interceptorParams.put(Parameter.SUBJECT, subject);
			interceptorParams.put(Parameter.SUBJECT_IP_ADDRESS, subject);
			interceptorParams.put(Parameter.RESOURCE, resource);
			interceptorParams.put(Parameter.RESOURCE_PROTOCOL, "http");
			interceptorParams.put(Parameter.RESOURCE_IP_ADDRESS, hostAddress);
			interceptorParams.put(Parameter.RESOURCE_HOSTNAME, hostName);
			interceptorParams.put(Parameter.RESOURCE_PATH, uri);
			interceptorParams.put(Parameter.ACTION, req.getMethod());

			interceptorRequest.setInterceptorParams(interceptorParams);

			Enumeration<String> headers = req.getHeaderNames();

			while (headers.hasMoreElements()) {
				String k = headers.nextElement();
				interceptorRequest.setHeader(k, req.getHeader(k));
			}

			Enumeration<String> params = req.getParameterNames();

			while (params.hasMoreElements()) {
				String k = params.nextElement();
				interceptorRequest.setRequestParam(k, req.getParameter(k));
			}

			log.info("Calling interceptions ...");

			InterceptorResponse interceptorResp = interceptorBroker
					.handleRequest(interceptorRequest);

			if (!interceptorResp.getStatus().equals(StatusCode.OK)) {
				if (interceptorResp.forward()) {
					resp.setStatus(HttpStatus.SC_FORBIDDEN);
					resp.setContentType(MIME_PLAINTEXT);
					return interceptorResp.getMessage();
				}
			}

			return null;
		}

		private StringBuffer getObixResponse(HttpServletRequest req,
				String ipv6Address, Obj obj, String resourcePath)
				throws URISyntaxException {

			StringBuffer response = null;

			URI rootUri, baseUri;

			if (ipv6Address == null)
				rootUri = new URI("/");
			else
				rootUri = new URI(getResourcePath("", ipv6Address));

			baseUri = new URI(resourcePath.substring(0,
					resourcePath.lastIndexOf('/') + 1));
			response = new StringBuffer(RelativeObixEncoder.toString(obj,
					rootUri, baseUri, req.getHeader("accept-language")));

			return response;
		}

		private String getResourcePath(String uri, String ipv6Address) {
			return new CoAPHelper(obixServer).getResourcePath(uri, ipv6Address);
		}

		private String getData(HttpServletRequest req) throws IOException {
			String payload = null;

			int contentLen = req.getContentLength();

			if (contentLen >= 1) {

				StringBuilder sb = new StringBuilder();
				BufferedReader reader = req.getReader();

				String line;
				do {
					line = reader.readLine();
					if (line != null) {
						sb.append(line);
					}
				} while (line != null);
				reader.reset();

				payload = sb.toString();

			}

			if (req.getHeader("content-type") != null) {
				// check for EXI content

				if (contains(req.getHeader("content-type"), MIME_EXI)) {

					try {
						return ExiUtil.getInstance().decodeEXI(unbox(payload));
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				} else if (contains(req.getHeader("content-type"),
						MIME_DEFAULT_BINARY)) {

					try {
						return ExiUtil.getInstance().decodeEXI(unbox(payload),
								true);
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				} else if (contains(req.getHeader("content-type"),
						MIME_X_OBIX_BINARY)) {

					try {
						Obj obj = BinObixDecoder.fromBytes(unbox(payload));
						return ObixEncoder.toString(obj,
								req.getParameter("accept-language"));
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				} else if (contains(req.getHeader("content-type"), MIME_JSON)) {
					String jsonData = payload;
					try {
						return JsonUtil.fromJSONtoXML(jsonData);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
			}

			return payload;
		}

		public Byte[] box(byte[] byteArray) {
			Byte[] box = new Byte[byteArray.length];
			for (int i = 0; i < box.length; i++) {
				box[i] = byteArray[i];
			}
			return box;
		}

		public byte[] unbox(Byte[] byteArray) {
			byte[] ret = new byte[byteArray.length];

			for (int i = 0; i < ret.length; i++) {
				ret[i] = byteArray[i];
			}
			return ret;
		}

		public byte[] unbox(String s) {
			byte[] ret = new byte[s.length()];

			for (int i = 0; i < ret.length; i++) {
				ret[i] = (byte) s.charAt(i);
			}
			return ret;
		}

		/**
		 * Common mime types for dynamic content
		 */
		public static final String MIME_PLAINTEXT = "text/plain",
				MIME_HTML = "text/html",
				MIME_DEFAULT_BINARY = "application/octet-stream",
				MIME_XML = "text/xml", MIME_EXI = "application/exi",
				MIME_X_OBIX_BINARY = "application/x-obix-binary",
				MIME_JSON = "application/json";

		/**
		 * Hashtable mapping (String)FILENAME_EXTENSION -> (String)MIME_TYPE
		 */
		private Hashtable<String, String> theMimeTypes = new Hashtable<String, String>();
		{
			StringTokenizer st = new StringTokenizer("css		text/css "
					+ "htm		text/html " + "html		text/html " + "xml		text/xml "
					+ "txt		text/plain " + "asc		text/plain "
					+ "gif		image/gif " + "jpg		image/jpeg "
					+ "jpeg		image/jpeg " + "png		image/png "
					+ "mp3		audio/mpeg " + "m3u		audio/mpeg-url "
					+ "mp4		video/mp4 " + "ogv		video/ogg "
					+ "flv		video/x-flv " + "mov		video/quicktime "
					+ "swf		application/x-shockwave-flash "
					+ "js			application/javascript " + "pdf		application/pdf "
					+ "doc		application/msword " + "ogg		application/x-ogg "
					+ "zip		application/octet-stream "
					+ "exe		application/octet-stream "
					+ "class		application/octet-stream ");
			while (st.hasMoreTokens())
				theMimeTypes.put(st.nextToken(), st.nextToken());
		}

	}

	public void shutdown() {
		try {
			this.tomcat.stop();
		} catch (LifecycleException e) {
			e.printStackTrace();
		}
	}
}