package obix.contracts;

import obix.*;

/**
 * AlarmSubject
 * 
 * @author obix.tools.Obixc
 * @creation 24 May 06
 * @version $Revision$ $Date$
 */
public interface AlarmSubject extends IObj
{
	public static final String CONTRACT = "obix:AlarmSubject";

	public static final String countContract = "<int name='count' val='0' min='0'/>";

	public Int count();

	public static final String queryContract = "<op name='query' in='" + AlarmFilter.CONTRACT + "' out='" + AlarmQueryOut.CONTRACT + "'/>";

	public Op query();

	public static final String feedContract = "<feed name='feed' in='" + AlarmFilter.CONTRACT + "' of='" + Alarm.CONTRACT + "'/>";

	public Feed feed();

}
