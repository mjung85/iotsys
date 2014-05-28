package at.ac.tuwien.auto.iotsys.appscale.demos.modify;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public abstract class BaseServlet extends HttpServlet {
	
	private static final String PARAM_HOST = "host";
	private static final String PARAM_PORT = "port";
	private static final String PARAM_DISPLAY_URI = "duri";
	private static final String PARAM_MODIFY_URI = "muri";
	private static final String PARAM_VALUE = "val";
	
	private String host = null;
	private int port = 0;
	private String uri = null;
	private String mUri = null;
	private String value = null;
	
	protected String getHost() {
		return host;
	}
	
	protected int getPort() {
		return port;
	}
	
	protected String getUri() {
		return uri;
	}
	
	protected String getModifyUri() {
		return mUri;
	}
	
	protected String getValue() {
		return value;
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		host = req.getParameter(PARAM_HOST);
		if(req.getParameter(PARAM_PORT) != null) {
			port = Integer.valueOf(req.getParameter(PARAM_PORT));
		}
		uri = req.getParameter(PARAM_DISPLAY_URI);
		mUri = req.getParameter(PARAM_MODIFY_URI);
		value = req.getParameter(PARAM_VALUE);
		
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta http-equiv='Content-Type' content='text/html;' charset='UTF-8'>");
        out.println("<link href='/bootstrap.css' rel='stylesheet' media='screen'/>");
        out.println("<link href='/global.css' rel='stylesheet' media='screen'/>");
        out.println("<title>Modify Objects</title>");
        out.println("</head>");
        out.println("<body>");
        
        writeBody(req, resp);
  
        out.println("<script src='/jquery-2.0.3.min.js'></script>");
        out.println("<script src='/bootstrap.js'></script>");
        out.println("<script src='/global.js'></script>");
        out.println("</body>");
        out.println("</html>");
        out.close();
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doGet(req, resp);
	}
	
	protected void writeError(PrintWriter out, String message) {
		out.println("<div class='error'>");
		out.println("<span>");
		out.println(message);
		out.println("</span>");
		out.println("</div>");
	}
	
	protected void writeInputForm(PrintWriter out, String action) {
		String formHost = "";
		String formPort = "";
		String formUri = "";	
		if(getHost() != null) {
			formHost = getHost();
		}
		if(getPort() != 0) {
			formPort = "" + port;
		}
		if(getUri() != null) {
			formUri = getUri();
		}
	
		out.println("<div class='container'>");		
		out.println("<div class='row'>");	
		out.println("<div class='span8 center'>");	
		
		out.println("<div id='form-wrapper' class='well'>");
		out.println("<form class='form-horizontal' id='reqform' action='" + action + "' method='get'>");	
		out.println("<div class='control-group'>");
		out.print("<label class='control-label' for='host'>");
		out.print("Host:");
		out.println("</label>");
		out.println("<div class='controls'>");
		out.println("<input id='host' type='text' name='" + PARAM_HOST + "' value='" + formHost + "' placeholder='Hostname'>");
		out.println("</div>");
		out.println("</div>");
	
		out.println("<div class='control-group'>");
		out.print("<label class='control-label' for='port'>");
		out.print("Port:");
		out.println("</label>");
		out.println("<div class='controls'>");
		out.println("<input id='port' type='text' name='" + PARAM_PORT + "' value='" + formPort + "' placeholder='Port'>");
		out.println("</div>");
		out.println("</div>");
		
		out.println("<div class='control-group'>");
		out.print("<label class='control-label' for='uri'>");
		out.print("Uri:");
		out.println("</label>");
		out.println("<div class='controls'>");
		out.println("<input id='duri' type='text' name='" + PARAM_DISPLAY_URI + "' value='" + formUri + "' placeholder='Uri to resource'>");
		out.println("</div>");
		out.println("</div>");
		
		out.println("<div class='control-group hidden'>");
		out.print("<label class='control-label' for='muri'>");
		out.print("Modify Uri:");
		out.println("</label>");
		out.println("<div class='controls'>");
		out.println("<input id='muri' type='text' name='" + PARAM_MODIFY_URI + "' value='' placeholder='Uri to modify'>");
		out.println("</div>");
		out.println("</div>");
		
		out.println("<div class='control-group hidden'>");
		out.print("<label class='control-label' for='val'>");
		out.print("Value for Modify Uri:");
		out.println("</label>");
		out.println("<div class='controls'>");
		out.println("<input id='val' type='text' name='" + PARAM_VALUE + "' value='' placeholder='Value to set for Modify Uri'>");
		out.println("</div>");
		out.println("</div>");
		
		out.println("<div class='control-group'>");
		out.println("<div class='controls'>");
		out.println("<button id='#form-send' class='btn btn-primary btn-block'>Request Object</button>");
		out.println("</div>");
		out.println("</div>");
		out.println("</form>");
		out.println("</div>");
		
		out.println("</div>");
		out.println("</div>");
		out.println("</div>");
	}
	
	public abstract void writeBody(HttpServletRequest req, HttpServletResponse resp) throws IOException; 

}
