package at.ac.tuwien.auto.iotsys.appscale.demos.history;

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
	private static final String PARAM_HISTORY_TYPE = "type";
	private static final String PARAM_STOP = "stop";
	private static final String PARAM_DELETE = "del";
	
	private String host = null;
	private int port = 0;
	private String uri = null;
	private boolean stop = false;
	private boolean delete = false;
	private String type = null; 
	
	protected String getHost() {
		return host;
	}
	
	protected int getPort() {
		return port;
	}
	
	protected String getUri() {
		return uri;
	}
	
	protected boolean getStop() {
		return stop;
	}
	
	protected boolean getDelete() {
		return delete;
	}
	
	protected String getType() {
		return type;
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		host = req.getParameter(PARAM_HOST);
		if(req.getParameter(PARAM_PORT) != null) {
			port = Integer.valueOf(req.getParameter(PARAM_PORT));
		}
		uri = req.getParameter(PARAM_DISPLAY_URI);		
		stop = Boolean.valueOf(req.getParameter(PARAM_STOP));
		delete = Boolean.valueOf(req.getParameter(PARAM_DELETE));
		type = req.getParameter(PARAM_HISTORY_TYPE);
		
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
        out.println("<link href='/bootstrap.css' rel='stylesheet' media='screen'/>");
        out.println("<link href='/global.css' rel='stylesheet' media='screen'/>");
        out.println("<link href='/jquery.jqplot.min.css' rel='stylesheet' media='screen'/>");
        out.println("<title>Record History</title>");
        out.println("</head>");
        out.println("<body>");
        
        writeBody(req, resp); 
  
        out.println("<script src='/jquery-2.0.3.min.js'></script>");
        out.println("<script src='/jquery.jqplot.min.js'></script>");
        out.println("<script src='/jqplot.dateAxisRenderer.min.js'></script>");
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
		out.println("<input id='uri' type='text' name='" + PARAM_DISPLAY_URI + "' value='" + formUri + "' placeholder='Uri to resource'>");
		out.println("</div>");
		out.println("</div>");
		
		out.println("<div class='control-group hidden'>");
		out.print("<label class='control-label' for='stop'>");
		out.print("Set stop recording flag:");
		out.println("</label>");
		out.println("<div class='controls'>");
		out.println("<input id='stop' type='text' name='" + PARAM_STOP + "' value='' placeholder=''>");
		out.println("</div>");
		out.println("</div>");
		
		out.println("<div class='control-group hidden'>");
		out.print("<label class='control-label' for='del'>");
		out.print("Set Delete records flag:");
		out.println("</label>");
		out.println("<div class='controls'>");
		out.println("<input id='del' type='text' name='" + PARAM_DELETE + "' value='' placeholder=''>");
		out.println("</div>");
		out.println("</div>");
		
		out.println("<div class='control-group hidden'>");
		out.print("<label class='control-label' for='type'>");
		out.print("Set Type flag:");
		out.println("</label>");
		out.println("<div class='controls'>");
		out.println("<input id='type' type='text' name='" + PARAM_HISTORY_TYPE + "' value='' placeholder=''>");
		out.println("</div>");
		out.println("</div>");
			
		out.println("<div class='control-group'>");
		out.println("<div class='controls'>");
		out.println("<button class='btn btn-primary' id='start-obj'>Poll Recording</button>");
		out.println("<button class='btn btn-primary' id='start-watch'>Watch Recording</button>");
		out.println("<button class='btn btn-primary' id='start-history'>History Recording</button>");
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
