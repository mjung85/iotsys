/*
 * Copyright 2007 Tridium, Inc. All Rights Reserved.
 */
package obix.net;

import java.util.Hashtable;

/**
 * Http contains constant definitions and utility methods for use
 * with HTTP 1.1.
 *
 * @author    John Sublett
 * @author    Craig Gemmill
 * @creation  17 Mar 00
 * @version   $Revision$ $Date: 3/28/2005 10:49:34 AM$
 */
public class Http
{
  public static final String PROTOCOL_HTTP      = "http";
  public static final int    DEFAULT_HTTP_PORT  = 80;
  public static final String PROTOCOL_HTTPS     = "https";
  public static final int    DEFAULT_HTTPS_PORT = 443;

  public static final int CR = 13;
  public static final int LF = 10;
  public static final String CRLF = "" + (char)CR + (char)LF;

  public static final String METHOD_OPTIONS = "OPTIONS";
  public static final String METHOD_GET     = "GET";
  public static final String METHOD_HEAD    = "HEAD";
  public static final String METHOD_POST    = "POST";
  public static final String METHOD_PUT     = "PUT";
  public static final String METHOD_DELETE  = "DELETE";
  public static final String METHOD_TRACE   = "TRACE";

  public static final String TRANSFER_CHUNKED = "chunked";
  public static final String SESSION_ID       = "sessionId";
  
  public static final String FORM_CONTENT_TYPE = "application/x-www-form-encoded";

  public static final int SC_CONTINUE                      = 100;
  public static final int SC_SWITCHING_PROTOCOLS           = 101;
  public static final int SC_OK                            = 200;
  public static final int SC_CREATED                       = 201;
  public static final int SC_ACCEPTED                      = 202;
  public static final int SC_NON_AUTHORITATIVE             = 203;
  public static final int SC_NO_CONTENT                    = 204;
  public static final int SC_RESET_CONTENT                 = 205;
  public static final int SC_PARTIAL_CONTENT               = 206;
  public static final int SC_MULTIPLE_CHOICES              = 300;
  public static final int SC_MOVED_PERMANENTLY             = 301;
  public static final int SC_MOVED_TEMPORARILY             = 302;
  public static final int SC_SEE_OTHER                     = 303;
  public static final int SC_NOT_MODIFIED                  = 304;
  public static final int SC_USE_PROXY                     = 305;
  public static final int SC_BAD_REQUEST                   = 400;
  public static final int SC_UNAUTHORIZED                  = 401;
  public static final int SC_PAYMENT_REQUIRED              = 402;
  public static final int SC_FORBIDDEN                     = 403;
  public static final int SC_NOT_FOUND                     = 404;
  public static final int SC_METHOD_NOT_ALLOWED            = 405;
  public static final int SC_NOT_ACCEPTABLE                = 406;
  public static final int SC_PROXY_AUTHENTICATION_REQUIRED = 407;
  public static final int SC_REQUEST_TIME_OUT              = 408;
  public static final int SC_CONFLICT                      = 409;
  public static final int SC_GONE                          = 410;
  public static final int SC_LENGTH_REQUIRED               = 411;
  public static final int SC_PRECONDITION_FAILED           = 412;
  public static final int SC_REQUEST_ENTITY_TOO_LARGE      = 413;
  public static final int SC_REQUEST_URI_TOO_LARGE         = 414;
  public static final int SC_UNSUPPORTED_MEDIA_TYPE        = 415;
  public static final int SC_INTERNAL_SERVER_ERROR         = 500;
  public static final int SC_NOT_IMPLEMENTED               = 501;
  public static final int SC_BAD_GATEWAY                   = 502;
  public static final int SC_SERVICE_UNAVAILABLE           = 503;
  public static final int SC_GATEWAY_TIME_OUT              = 504;
  public static final int SC_HTTP_VERSION_NOT_SUPPORTED    = 505;

//  private static Lexicon lex = Lexicon.make("net");

  /**********************************************
  * Get a reason phrase for the specified status code.
  ***********************************************/
  public static final String getReasonPhrase(int statusCode)
  {
    String phrase = codeToPhrase(statusCode);
    if (phrase == null)
    {
      phrase = codeToPhrase((statusCode / 100) * 100);
      if (phrase == null)
//        phrase = lex.getText("Http.statusCode.unknown", 
//                             new Object[] 
//                             { 
//                               String.valueOf(statusCode)
//                             });
        phrase = UNKNOWN_STATUS_CODE + statusCode;
    }
    return phrase;
  }

  /**********************************************
  * Rebuild the reason phrase table or build it for
  * the first time if it doesn't exist.
  ***********************************************/
  public static String codeToPhrase(int statusCode)
  {
//    String result = lex.get("Http.statusCode." + statusCode);
    String result = (String)phrasesByCode.get(""+statusCode);

    return (result == null) ? null : result;
  }

  private static final Hashtable phrasesByCode = new Hashtable();
  private static final String UNKNOWN_STATUS_CODE = "Unknown Status Code: ";
  
  // from fw/net/module.lexicon
  static
  {
    phrasesByCode.put("100", "Continue");
    phrasesByCode.put("101", "Switching Protocols");
    phrasesByCode.put("200", "OK");
    phrasesByCode.put("201", "Created");
    phrasesByCode.put("202", "Accepted");
    phrasesByCode.put("203", "Non-Authoritative Information");
    phrasesByCode.put("204", "No Content");
    phrasesByCode.put("205", "Reset Content");
    phrasesByCode.put("206", "Partial Content");
    phrasesByCode.put("300", "Multiple Choices");
    phrasesByCode.put("301", "Moved Permanently");
    phrasesByCode.put("302", "Moved Temporarily");
    phrasesByCode.put("303", "See Other");
    phrasesByCode.put("304", "Not Modified");
    phrasesByCode.put("305", "Use Proxy");
    phrasesByCode.put("400", "Bad Request");
    phrasesByCode.put("401", "Access denied");
    phrasesByCode.put("402", "Payment Required");
    phrasesByCode.put("403", "Access denied");
    phrasesByCode.put("404", "Not Found");
    phrasesByCode.put("405", "Method Not Allowed");
    phrasesByCode.put("406", "Not Acceptable");
    phrasesByCode.put("407", "Proxy Authentication Required");
    phrasesByCode.put("408", "Request Time-out");
    phrasesByCode.put("409", "Conflict");
    phrasesByCode.put("410", "Gone");
    phrasesByCode.put("411", "Length Required");
    phrasesByCode.put("412", "Precondition Failed");
    phrasesByCode.put("413", "Request Entity Too Large");
    phrasesByCode.put("414", "Request URI Too Large");
    phrasesByCode.put("415", "Unsupported Media Type");
    phrasesByCode.put("500", "Internal Server Error");
    phrasesByCode.put("501", "Not Implemented");
    phrasesByCode.put("502", "Bad Gateway");
    phrasesByCode.put("503", "Service Unavailable");
    phrasesByCode.put("504", "Gateway Time-out");
    phrasesByCode.put("505", "HTTP Version Not Supported");
  }
}