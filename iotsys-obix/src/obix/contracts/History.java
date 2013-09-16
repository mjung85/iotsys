package obix.contracts;

import obix.*;

/**
 * History
 * 
 * @author obix.tools.Obixc
 * @creation 24 May 06
 * @version $Revision$ $Date$
 */
public interface History extends IObj
{
	public static final String CONTRACT = "obix:History";

	public static final String countContract = "<int name='count' val='0' min='0'/>";

	public Int count();

	public static final String startContract = "<abstime name='start' val='1969-12-31T19:00:00.000-05:00' null='true'/>";

	public Abstime start();

	public static final String endContract = "<abstime name='end' val='1969-12-31T19:00:00.000-05:00' null='true'/>";

	public Abstime end();

	public static final String tzContract = "<str name='tz' null='true'/>";

	public Str tz();

	public static final String queryContract = "<op name='query' in='" + HistoryFilter.CONTRACT + "' out='" + HistoryQueryOut.CONTRACT + "'/>";

	public Op query();

	public static final String feedContract = "<feed name='feed' in='" + HistoryFilter.CONTRACT + "' of='" + HistoryRecord.CONTRACT + "'/>";

	public Feed feed();

	public static final String rollupContract = "<op name='rollup' in='" + HistoryRollupIn.CONTRACT + "' out='" + HistoryRollupOut.CONTRACT + "'/>";

	public Op rollup();

	public static final String appendContract = "<op name='append' in='" + HistoryAppendIn.CONTRACT + "' out='" + HistoryAppendOut.CONTRACT + "'/>";

	public Op append();

}
