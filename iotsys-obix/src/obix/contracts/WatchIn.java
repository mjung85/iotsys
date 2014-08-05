package obix.contracts;

import obix.*;

/**
 * WatchIn
 * 
 * @author obix.tools.Obixc
 * @creation 24 May 06
 * @version $Revision$ $Date$
 */
public interface WatchIn extends IObj
{
	public static final String CONTRACT = "obix:WatchIn";

	public static final String hrefsContract = "<list name='hrefs' of='" + WatchInItem.CONTRACT + "'/>";

	public List hrefs();

}
