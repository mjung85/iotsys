package obix.contracts;

import obix.*;

/**
 * WatchInItem
 * 
 * @author obix.tools.Obixc
 * @creation 24 May 06
 * @version $Revision$ $Date$
 */
public interface WatchInItem extends IObj
{
	public static final String CONTRACT = "obix:WatchInItem";

	public Obj in();

}
