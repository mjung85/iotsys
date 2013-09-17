package obix.contracts;

import obix.*;

/**
 * Lobby
 * 
 * @author obix.tools.Obixc
 * @creation 24 May 06
 * @version $Revision$ $Date$
 */
public interface Lobby extends IObj
{
	public static final String CONTRACT = "obix:Lobby";

	public static final String aboutContract = "<ref name='about' is='" + About.CONTRACT + "'/>";

	public Ref about();

	public static final String batchContract = "<op name='batch' in='obix:BatchIn' out='" + BatchOut.CONTRACT + "'/>";

	public Op batch();

	public static final String watchServiceContract = "<ref name='watchService' is='" + WatchService.CONTRACT + "'/>";

	public Ref watchService();

}
