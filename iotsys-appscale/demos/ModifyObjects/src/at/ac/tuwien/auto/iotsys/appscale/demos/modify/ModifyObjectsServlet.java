package at.ac.tuwien.auto.iotsys.appscale.demos.modify;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.iotsys.IotsysService;
import com.google.appengine.api.iotsys.IotsysServiceFactory;
import com.google.appengine.api.iotsys.exception.CommunicationException;
import com.google.appengine.api.iotsys.object.IotAbsoluteTime;
import com.google.appengine.api.iotsys.object.IotBoolean;
import com.google.appengine.api.iotsys.object.IotCalendarTypeDatapoint;
import com.google.appengine.api.iotsys.object.IotDatapoint;
import com.google.appengine.api.iotsys.object.IotDate;
import com.google.appengine.api.iotsys.object.IotError;
import com.google.appengine.api.iotsys.object.IotInteger;
import com.google.appengine.api.iotsys.object.IotNumericDatapoint;
import com.google.appengine.api.iotsys.object.IotObject;
import com.google.appengine.api.iotsys.object.IotOperation;
import com.google.appengine.api.iotsys.object.IotReal;
import com.google.appengine.api.iotsys.object.IotRelativeTime;
import com.google.appengine.api.iotsys.object.IotString;
import com.google.appengine.api.iotsys.object.IotTime;

@SuppressWarnings("serial")
public class ModifyObjectsServlet extends BaseServlet {

	@Override
	public void writeBody(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
			
		writeInputForm(resp.getWriter(), "/modify");
		
		if(getHost() == null || getHost().length() == 0) {
			return;
		}
		if(getPort() == 0) {
			return;
		}
		
		PrintWriter out = resp.getWriter();  
		
		String uri = "/";
		if(getUri() != null) {
			uri = getUri();
		}
		
		IotsysService is = IotsysServiceFactory.getIotsysService(getHost(), getPort());
		try {
			if(getModifyUri() != null && getModifyUri().length() > 0) {
				IotObject modify = is.retrieveObject(getModifyUri());
				if(modify instanceof IotInteger) {
					((IotInteger) modify).setValue(Long.valueOf(getValue()));	
				} else if(modify instanceof IotReal) {
					((IotReal) modify).setValue(Double.valueOf(getValue()));
				} else if(modify instanceof IotRelativeTime) {
					((IotRelativeTime) modify).setValue(Long.valueOf(getValue()));
				} else if(modify instanceof IotBoolean) {
					((IotBoolean) modify).setValue(Boolean.valueOf(getValue()));
				} else if(modify instanceof IotOperation) {
					IotObject opIn = null;
					if(getValue() != null && getValue().length() > 0) {
						opIn = is.retrieveObject(getValue());
					}
					IotObject opOut = ((IotOperation) modify).invoke(opIn);
					writeObject(out, opOut);
					return;
				}
				modify.write();
			}
			
			IotObject requested = is.retrieveObject(uri);
			if(requested instanceof IotError) {
				writeError(out, requested.getName());
				return;
			}
				
			writeObject(out, requested);
		} catch (CommunicationException e) {
			writeError(out, e.getMessage());
		}
	}
	
	private void writeObject(PrintWriter out, IotObject object) {
		out.println("<div class='container'>");
		out.println("<div class='row'>");
		out.println("<div class='span12'>");
		out.println("<ul id='objectlist' class='list-group'>");
		writeChildTree(out, object, 0);
		out.println("</ul>");
		out.println("</div>");
		out.println("</div>");
		out.println("</div>");
	}
	
	private void writeChildTree(PrintWriter out, IotObject object, int level) {
		
		out.println("<li class='list-group-item child" + level + "'>");
		out.println("<div class='input-group'>");
		writeObjectByClass(out, object);
		out.println("</div>");
		out.println("</li>");
		
		for(IotObject child : object.getAllChildren()) {
			writeChildTree(out, child, level + 1);
		}
	}
	
	private void writeObjectByClass(PrintWriter out, IotObject object) {
		out.println("<span class='input-group-btn'>");
		if(object.getHref() != null && object.getHref().length() > 1) {
			out.println("<button class='btn btn-primary href'>" + object.getHref() + "</button>");
		} else if(object.getName() != null && object.getName().length() > 0) {
			out.println("<button class='btn btn-primary'>" + object.getName() + "</button>");
		} else if(object.getContractString() != null && object.getContractString().length() > 0) {
			out.println("<button class='btn btn-primary'>" + object.getContractString() + "</button>");
		} else {
			out.println("<button class='btn btn-primary'>" + object.getClass().getSimpleName() + "</button>");
		}
		out.println("</span>");
		if(object instanceof IotDatapoint) {
			writeDatapoint(out, (IotDatapoint<?>)object);
		} else if(object instanceof IotOperation){
			writeOperation(out, (IotOperation)object); 
		}	
		out.println();
	}
	
	private void writeOperation(PrintWriter out, IotOperation op) {
		out.println("<input class='operation-in form-control' type='text' name='operation' value='' placeholder='URI to input object'>");
		out.println("<span class='input-group-btn'>");
		out.println("<button class='btn btn-default operation-invoke'>Invoke (" + op.getClass().getSimpleName() + ")</button>");
		out.println("</span>");
	}
	
	private void writeDatapoint(PrintWriter out, IotDatapoint<?> object) {
		if(object instanceof IotNumericDatapoint) {
			writeNumeric(out, (IotNumericDatapoint<?>) object);
		} else if(object instanceof IotBoolean) {
			writeBoolean(out, (IotBoolean) object);
		} else if(object instanceof IotCalendarTypeDatapoint) {
			writeDateTime(out, (IotCalendarTypeDatapoint) object);
		} else if(object instanceof IotString) {
			writeString(out, (IotString) object); 
		}
	}
	
	private void writeDateTime(PrintWriter out, IotCalendarTypeDatapoint dp) {
		if(dp instanceof IotAbsoluteTime) {
			writeAbsoluteTime(out, (IotAbsoluteTime) dp);
		} else if(dp instanceof IotRelativeTime) {
			writeRelativeTime(out, (IotRelativeTime) dp);
		} else if(dp instanceof IotDate) {
			writeDate(out, (IotDate) dp);
		} else if(dp instanceof IotTime) {
			writeTime(out, (IotTime) dp);
		}
	}
	
	private void writeNumeric(PrintWriter out, IotNumericDatapoint<?> dp) {
		if(dp.isWritable()) {
			out.println("<input class='numpoint form-control' type='text' name='numpoint' value='" + dp.getValue().toString() + "'>");
			out.println("<span class='input-group-btn'>");
			out.println("<button class='btn btn-default numpointsubmit'>Set new Value (" + dp.getClass().getSimpleName() + ")</button>");
			out.println("</span>");
		} else {
			out.println("<input class='numpoint form-control' type='text' name='numpoint' disabled='disabled' value='" + dp.getValue().toString() + "'>");
		}
	}
	
	private void writeBoolean(PrintWriter out, IotBoolean dp) {
		if(dp.isWritable()) {
			out.println("<input class='boolpoint form-control' type='text' name='numpoint' disabled='disabled' value='" + dp.getValue().toString() + "'>");
			out.println("<span class='input-group-btn'>");
			out.println("<button class='btn btn-default bool-on'>On</button>");
			out.println("<button class='btn btn-default bool-off'>Off</button>");
			out.println("</span>");
		} else {
			out.println("<input class='boolpoint form-control' type='text' name='numpoint' disabled='disabled' value='" + dp.getValue().toString() + "'>");
		}
	}
	
	private void writeString(PrintWriter out, IotString dp) {
		if(dp.isWritable()) {
			out.println("<input class='numpoint form-control' type='text' name='numpoint' value='" + dp.getValue().toString() + "'>");
			out.println("<span class='input-group-btn'>");
			out.println("<button class='btn btn-default numpointsubmit'>Set new Value (" + dp.getClass().getSimpleName() + ")</button>");
			out.println("</span>");
		} else {
			out.println("<input class='numpoint form-control' type='text' name='numpoint' disabled='disabled' value='" + dp.getValue().toString() + "'>");
		}
	}
	
	private void writeAbsoluteTime(PrintWriter out, IotAbsoluteTime dp) {
		Calendar cal = dp.getValue();
		//if(!dp.isWritable()) {
			out.println("<input class='form-control' type='text' disabled='disabled' value='Day: " + cal.get(Calendar.DAY_OF_MONTH) + "'>");
			out.println("<input class='form-control' type='text' disabled='disabled' value='Month: " + (cal.get(Calendar.MONTH) + 1) + "'>");
			out.println("<input class='form-control' type='text' disabled='disabled' value='Year: " + cal.get(Calendar.YEAR) + "'>");
			out.println("<input class='form-control' type='text' disabled='disabled' value='Hour: " + cal.get(Calendar.HOUR_OF_DAY) + "'>");
			out.println("<input class='form-control' type='text' disabled='disabled' value='Minute: " + cal.get(Calendar.MINUTE) + "'>");
			out.println("<input class='form-control' type='text' disabled='disabled' value='Second: " + cal.get(Calendar.SECOND) + "'>");
			out.println("<input class='form-control' type='text' disabled='disabled' value='Timezone: " + dp.getTimeZone().getDisplayName() + "'>");
		//}
	}
	
	private void writeRelativeTime(PrintWriter out, IotRelativeTime dp) { 
		if(dp.isWritable()) {
			out.println("<input class='numpoint form-control' type='text' name='numpoint' value='" + dp.getValueInMillis() + "'>");
			out.println("<span class='input-group-btn'>");
			out.println("<button class='btn btn-default numpointsubmit'>Set new Value (" + dp.getClass().getSimpleName() + ")</button>");
			out.println("</span>");
			
		} else {
			out.println("<input class='numpoint form-control' type='text' name='numpoint' disabled='disabled' value='" + dp.getValueInMillis() + "'>");
		}
	}
	
	private void writeDate(PrintWriter out, IotDate dp) {
		Calendar cal = dp.getValue();
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		//if(!dp.isWritable()) {
			out.println("<input class='form-control' type='text' disabled='disabled' value='" + day + "-" + month + "-" + year + "'>");
		//}
	}
	
	private void writeTime(PrintWriter out, IotTime dp) {
		Calendar cal = dp.getValue();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);
		int ms = cal.get(Calendar.MILLISECOND);
		//if(!dp.isWritable()) {
			out.println("<input class='form-control' type='text' disabled='disabled' value='" + hour + ":" + min + ":" + sec + "." + ms + "'>");
		//}
	}
	
}
