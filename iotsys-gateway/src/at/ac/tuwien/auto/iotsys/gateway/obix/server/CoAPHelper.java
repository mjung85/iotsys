package at.ac.tuwien.auto.iotsys.gateway.obix.server;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import obix.Obj;
import obix.io.BinObixEncoder;
import obix.io.ObixDecoder;
import obix.io.RelativeObixEncoder;

import org.json.JSONException;

import at.ac.tuwien.auto.iotsys.gateway.util.ExiUtil;
import at.ac.tuwien.auto.iotsys.gateway.util.JsonUtil;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.registries.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry;

public class CoAPHelper {
	private static final Logger log = Logger.getLogger(CoAPHelper.class.getName());
	
	private ObixServer obixServer;
	
	public CoAPHelper(ObixServer obixServer) {
		this.obixServer = obixServer;
	}
	
	/**
	 * Serializes the given Obj to an XML-String. 
	 * @param obj The Obj to encode
	 * @param request A Request. Hrefs are encoded relative to the request's path
	 * @return A String containing the Obj in XML representation
	 */
	public String encodeObj(Obj obj, Request request) {
		try {
			URI rootUri = new URI(getRequestRoot(request));
			URI baseUri = new URI(getRequestBase(request));
			
			return RelativeObixEncoder.toString(obj, rootUri, baseUri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Sets the response to the given Request.
	 * The response is encoded appropriately based on the request's accept-header
	 * @param obixResponse The response to encode
	 * @param request The request which is being responded to
	 */
	public void encodeResponse(String obixResponse, Request request) {
		if (request.getFirstAccept() == MediaTypeRegistry.APPLICATION_EXI) {
			try {
				byte[] exiData = ExiUtil.getInstance().encodeEXI(obixResponse);
				request.respond(CodeRegistry.RESP_CONTENT, exiData,
						MediaTypeRegistry.APPLICATION_EXI);

			} catch (Exception e) {
				e.printStackTrace();
				request.respond(CodeRegistry.RESP_CONTENT,
						obixResponse, MediaTypeRegistry.TEXT_XML);
			}
		} else if (request.getFirstAccept() == MediaTypeRegistry.APPLICATION_OCTET_STREAM) {
			try {
				byte[] exiData = ExiUtil.getInstance().encodeEXI(
						obixResponse, true);
				request.respond(CodeRegistry.RESP_CONTENT, exiData,
						MediaTypeRegistry.APPLICATION_EXI);

			} catch (Exception e) {
				e.printStackTrace();
				request.respond(CodeRegistry.RESP_CONTENT,
						obixResponse, MediaTypeRegistry.TEXT_XML);
			}
		} else if (request.getFirstAccept() == MediaTypeRegistry.APPLICATION_X_OBIX_BINARY) {
			try {
				byte[] exiData = BinObixEncoder.toBytes(ObixDecoder
						.fromString(obixResponse));
				request.respond(CodeRegistry.RESP_CONTENT, exiData,
						MediaTypeRegistry.APPLICATION_X_OBIX_BINARY);

			} catch (Exception e) {
				e.printStackTrace();
				request.respond(CodeRegistry.RESP_CONTENT,
						obixResponse, MediaTypeRegistry.TEXT_XML);
			}
		}

		else if (request.getFirstAccept() == MediaTypeRegistry.APPLICATION_JSON) {
			try {
				request.respond(CodeRegistry.RESP_CONTENT,
						JsonUtil.fromXMLtoJSON(obixResponse),
						MediaTypeRegistry.APPLICATION_JSON);
			} catch (JSONException e) {
				e.printStackTrace();
				request.respond(CodeRegistry.RESP_CONTENT,
						obixResponse.toString(), MediaTypeRegistry.TEXT_XML);
			}
		} else if (request.getUriPath().endsWith(".well-known/core")  && !obixServer.containsIPv6(getIPv6Address(request))) {
			obixResponse = obixServer.getCoRELinks();
			request.respond(CodeRegistry.RESP_CONTENT,
					obixResponse,
					MediaTypeRegistry.APPLICATION_LINK_FORMAT);

		} else {
			request.respond(CodeRegistry.RESP_CONTENT, obixResponse, MediaTypeRegistry.TEXT_XML);
		}
	}
	
	/**
	 * @param request
	 * @return the ipv6 address of the network interface the request was received on
	 */
	public String getIPv6Address(Request request) {
		String localSocket = request.getNetworkInterface().getHostAddress()
				.toString();

		int lastIndex = localSocket.lastIndexOf("%");
		String localSocketSplitted = request.getNetworkInterface()
				.getHostAddress().toString();

		if (lastIndex > 0) {
			localSocketSplitted = localSocket.substring(0, lastIndex);
		}

		if (!localSocketSplitted.startsWith("/")) {
			localSocketSplitted = "/" + localSocketSplitted;
		}
		
		return localSocketSplitted;
	}
	
	private String getRequestRoot(Request request) {
		String ipv6Address = getIPv6Address(request);
		
		if (ipv6Address == null)
			return "/";
		
		return getResourcePath("", ipv6Address);
	}
	
	private String getRequestBase(Request request) {
		String resourcePath = getResourcePath(request);
		return resourcePath.substring(0, resourcePath.lastIndexOf('/')+1);
	}
	
	public String getResourcePath(Request request) {
		String resourcePath = request.getUriPath();
		String ipv6Address = getIPv6Address(request);
		
		return getResourcePath(resourcePath, ipv6Address);
	}
	
	public String getResourcePath(String uri, String ipv6Address) {
		String resourcePath = uri;
		
		if (obixServer.containsIPv6(ipv6Address)) {
			log.finest("IPv6 Adresse -> IoT6 Object");
			resourcePath = obixServer.getIPv6LinkedHref(ipv6Address);
			resourcePath += uri;
		}
		
		// normalize resource path
		while (resourcePath.startsWith(("/")))  resourcePath = resourcePath.substring(1);
		
		try {
			resourcePath = URI.create("//localhost/" + URLEncoder.encode(resourcePath, "UTF-8")).normalize().getPath();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return resourcePath;
	}
}
