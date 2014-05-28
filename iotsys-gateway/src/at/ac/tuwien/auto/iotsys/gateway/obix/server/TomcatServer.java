package at.ac.tuwien.auto.iotsys.gateway.obix.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import obix.Obj;
import obix.io.BinObixDecoder;
import obix.io.ObixEncoder;
import obix.io.RelativeObixEncoder;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.json.JSONException;

import at.ac.tuwien.auto.iotsys.commons.interceptor.InterceptorBroker;
import at.ac.tuwien.auto.iotsys.gateway.interceptor.InterceptorBrokerImpl;
import at.ac.tuwien.auto.iotsys.gateway.util.ExiUtil;
import at.ac.tuwien.auto.iotsys.gateway.util.JsonUtil;

public class TomcatServer {

	private Tomcat tomcat;

	private static final Logger log = Logger.getLogger(TomcatServer.class
			.getName());

	public TomcatServer(int port, ObixServer obixServer) throws IOException,
			ServletException {

		this.tomcat = new Tomcat();

		tomcat.setPort(port);

		Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());

		Tomcat.addServlet(ctx, "obix", new ObixServlet(obixServer));
		ctx.addServletMapping("/*", "obix");

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

		StringBuffer obixResponse;

		Obj responseObj = null;

		String resourcePath;

		Map<String, String> header;

		String data;

		String ipv6Address;

		public ObixServlet(ObixServer obixServer) throws IOException {
			try {
				this.exiUtil = ExiUtil.getInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.obixServer = obixServer;
			soapHandler = new SOAPHandler(this.obixServer);
		}

		@Override
		public void service(HttpServletRequest req, HttpServletResponse res)
				throws ServletException, IOException {

			// Get request uri
			String requestUri = req.getRequestURI();

			// Get IPv6 address
			String remoteAddress = req.getRemoteAddr();
			InetAddress inetAddress = InetAddress.getByName(remoteAddress);

			String subject = inetAddress.getHostAddress();
			ipv6Address = "/" + getIPv6Address(req);

			// Get request header
			header = new HashMap<String, String>();

			Enumeration<String> headerNames = req.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String key = (String) headerNames.nextElement();
				String value = req.getHeader(key);
				header.put(key, value);
			}

			// Get request parameters
			Map<String, String> parms = new HashMap<String, String>();

			Enumeration<String> parmNames = req.getParameterNames();
			while (parmNames.hasMoreElements()) {
				String key = (String) parmNames.nextElement();
				String value = req.getParameter(key);
				parms.put(key, value);
			}

			// Get data
			data = getData(header, parms);

			if (data != null)
				log.finest("serve: " + requestUri + ", method: "
						+ req.getMethod() + ", data.length(): " + data.length());
			log.finest("Data: " + data);

			// Get resource path
			resourcePath = getResourcePath(requestUri, ipv6Address);

			super.service(req, res);
		}

		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			PrintWriter w = resp.getWriter();

			try {
				responseObj = obixServer.readObj(new URI(resourcePath), true);
				obixResponse = getObixResponse(ipv6Address, data, header,
						responseObj);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}

			w.println(obixResponse.toString());
		}

		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			PrintWriter w = resp.getWriter();

			try {
				responseObj = obixServer.invokeOp(new URI(resourcePath), data);
				obixResponse = getObixResponse(ipv6Address, data, header,
						responseObj);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			
			w.println(obixResponse.toString());
		}

		@Override
		protected void doPut(HttpServletRequest req, HttpServletResponse resp)
				throws ServletException, IOException {
			PrintWriter w = resp.getWriter();

			try {
				responseObj = obixServer.writeObj(new URI(resourcePath), data);
				obixResponse = getObixResponse(ipv6Address, data, header,
						responseObj);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			
			w.println(obixResponse.toString());
		}

		private StringBuffer getObixResponse(String ipv6Address, String data,
				Map<String, String> header, Obj obj) throws URISyntaxException {

			StringBuffer response = null;

			URI rootUri, baseUri;

			if (ipv6Address == null)
				rootUri = new URI("/");
			else
				rootUri = new URI(getResourcePath("", ipv6Address));

			baseUri = new URI(resourcePath.substring(0,
					resourcePath.lastIndexOf('/') + 1));
			response = new StringBuffer(RelativeObixEncoder.toString(obj,
					rootUri, baseUri, header.get("accept-language")));

			return response;
		}

		private String getIPv6Address(ServletRequest req) {
			String localAddress = req.getLocalAddr().toString();
			int lastIndex = localAddress.lastIndexOf(":");
			String localSocketSplitted = "";

			if (lastIndex > 0) {
				localSocketSplitted = localAddress.substring(0, lastIndex);
			}

			lastIndex = localSocketSplitted.lastIndexOf("%");

			if (lastIndex > 0) {
				localSocketSplitted = localSocketSplitted.substring(0,
						lastIndex);
			}

			String ipv6Address = localSocketSplitted.substring(1);
			return ipv6Address;
		}

		private String getResourcePath(String uri, String ipv6Address) {
			return new CoAPHelper(obixServer).getResourcePath(uri, ipv6Address);
		}

		private String getData(Map<String, String> header,
				Map<String, String> parms) {
			if (header.containsKey("content-type")) {
				// check for EXI content
				if (header.get("content-type").contains(MIME_EXI)) {
					String payload = parms.get("payload");

					try {
						return ExiUtil.getInstance().decodeEXI(unbox(payload));
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				} else if (header.get("content-type").contains(
						MIME_DEFAULT_BINARY)) {
					String payload = parms.get("payload");

					try {
						return ExiUtil.getInstance().decodeEXI(unbox(payload),
								true);
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				} else if (header.get("content-type").contains(
						MIME_X_OBIX_BINARY)) {
					String payload = parms.get("payload");

					try {
						Obj obj = BinObixDecoder.fromBytes(unbox(payload));
						return ObixEncoder.toString(obj,
								parms.get("accept-language"));
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				} else if (header.get("content-type").contains(MIME_JSON)) {
					String jsonData = parms.get("data");
					try {
						return JsonUtil.fromJSONtoXML(jsonData);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
			}

			if (parms.containsKey("data"))
				return parms.get("data");

			return "";
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

	}
}