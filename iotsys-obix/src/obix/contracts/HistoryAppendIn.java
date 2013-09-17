package obix.contracts;

import obix.IObj;
import obix.List;

/**
 * HistoryAppendInIn
 * 
 * @author Robert Horvath
 * @creation 10 July 13
 * @version $Revision$ $Date$
 */
public interface HistoryAppendIn extends IObj
{
	public static final String CONTRACT = "obix:HistoryAppendIn";

	public static final String timestampContract = "<list name='data' of='" + HistoryRecord.CONTRACT + "'/>";

	public List data();

}
