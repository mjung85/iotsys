package at.ac.tuwien.auto.iotsys.appscale.demos.history;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.iotsys.IotsysService;
import com.google.appengine.api.iotsys.IotsysServiceFactory;
import com.google.appengine.api.iotsys.exception.CommunicationException;
import com.google.appengine.api.iotsys.exception.HistoryExistsException;
import com.google.appengine.api.iotsys.exception.NoSuchHistoryException;
import com.google.appengine.api.iotsys.object.IotAbsoluteTime;
import com.google.appengine.api.iotsys.object.IotDatapoint;
import com.google.appengine.api.iotsys.object.IotObject;
import com.google.appengine.api.iotsys.object.history.IotHistoryRecord;

@SuppressWarnings("serial")
public class RecordHistoryServlet extends BaseServlet {

	private static final Logger logger = Logger.getLogger(RecordHistoryServlet.class.getName());
	
	@Override
	public void writeBody(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		PrintWriter out = resp.getWriter();
		
		writeInputForm(out, "/record");	
		
		if(getHost() == null || getHost().length() == 0) {
			return;
		}
		if(getPort() == 0) {
			return;
		}
		if(getUri() == null || getUri().length() == 0) {
			return; 
		}
		
		out.println("<div id='body-wrapper' class='container'>");
		
		IotsysService is = IotsysServiceFactory.getIotsysService(getHost(), getPort());
		try {
			IotObject obj = new IotObject();
			obj.setHost(getHost());
			obj.setPort(getPort());
			obj.setHref(getUri());
						
			logger.info("object has format: " + obj.getFormat());
			logger.info("object has protocol: " + obj.getFormat());
			
			out.println("<div class='row'>");	
			out.println("<div class='span8 center'>");		
			if(getStop()) {
				is.stopHistoryRecording(obj);
				out.println("<span>History Recording stopped for " + obj.getHref() + "</span>");
			}		
			if(getDelete()) {
				is.deleteHistoryRecords(obj);
				out.println("<span>History Records deleted for " + obj.getHref() + "</span>");
			}
			if(!getStop() && !getDelete() && !is.hasActiveHistoryRecording(obj)) {
				if("watch".equals(getType())) {
					is.recordWatch(obj);
				} else if("history".equals(getType())) {
					is.recordHistory(obj);
				} else {
					is.recordObject(obj, 30000);
				}
				out.println("<span>History Recording started for " + obj.getHref() + "</span>");
			}
			out.println("</div>");
			out.println("</div>");
			
			writeHistoryPlot(out);
			
			if(is.hasHistoryData(obj)) {
				List<IotHistoryRecord> records;
				records = is.getHistoryData(obj);
		 
				writeHistoryData(out, obj, records); 
			} else {
				writeHistoryData(out, obj, new ArrayList<IotHistoryRecord>()); 
			}
		} catch (CommunicationException e) { 
			writeError(out, e.getMessage());
		} catch (NoSuchHistoryException e) {
			writeError(out, e.getMessage());
		} catch (HistoryExistsException e) {
			writeError(out, e.getMessage());
		}
		
		out.println("</div>"); //container fluid
		
	}
	
	private void writeHistoryPlot(PrintWriter out) {
		out.println("<div class='row'>");
		out.println("<div class='span8 center'>");
		out.println("<div id='history-plot'>");
		out.println("</div>");
		out.println("</div>");
		out.println("</div>");
	}
	
	private void writeHistoryData(PrintWriter out, IotObject historyObject, List<IotHistoryRecord> records) {
		writeHistoryObject(out, historyObject);
		out.println("<div class='row'>");
		out.println("<div class='span8 center'>");
		if(records.isEmpty()) {
			out.println("<span>There are no Records yet</span>");
		} else {
			out.println("<table class='table table-striped span6'>");
			out.println("<tr>");
			out.println("<th>");
			out.println("Timestamp");
			out.println("</th>");
			out.println("<th>");
			out.println("Value");
			out.println("</th>");
			out.println("</tr>");
			for(IotHistoryRecord rec : records) {
				out.println("<tr class='history-record'>");
				writeHistoryRecord(out, rec);
				out.println("</tr>");
			}
			out.println("</table>");
		}
		out.println("</div>");
		out.println("</div>");
	}
	
	private void writeHistoryObject(PrintWriter out, IotObject historyObject) {
		out.println("<div class='row'>");
		out.println("<div class='span8 center'>");
		out.println("<div>");
		out.println("<span>History Records for <b id='href'>" + historyObject.getHref() + "</b></span>");
		out.println("<button class='btn btn-warning' id='rec-stop'>Stop Recording</button>");
		out.println("<button class='btn btn-danger' id='rec-del'>Delete Records</button>");
		out.println("</div>");
		out.println("</div>");
		out.println("</div>");
	}
	
	private void writeHistoryRecord(PrintWriter out, IotHistoryRecord historyRecord) {
		IotAbsoluteTime time = historyRecord.getTimestamp();
		out.println("<td class='timestamp'>");
		String timestamp = time.getTimestamp();
		out.println(timestamp.substring(0, timestamp.lastIndexOf('-')));
		out.println("</td>");
		IotObject data = historyRecord.getValue();
		if(data instanceof IotDatapoint<?>) {
			out.println("<td class='numeric'>");
			out.println(((IotDatapoint<?>) data).getValue());
			out.println("</td>");
		} else {
			out.println("<td>");
			out.println(data.toString());
			out.println("</td>");
		}
	}
	
}
