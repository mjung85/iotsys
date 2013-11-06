package obix.contracts;

import obix.*;

/**
 * AlarmFilter
 * 
 * @author obix.tools.Obixc
 * @creation 24 May 06
 * @version $Revision$ $Date$
 */
public interface AlarmFilter extends IObj
{
	public static final String CONTRACT = "obix:AlarmFilter";

	public static final String limitContract = "<int name='limit' val='0' null='true'/>";

	public Int limit();

	public static final String startContract = "<abstime name='start' val='1969-12-31T19:00:00.000-05:00' null='true'/>";

	public Abstime start();

	public static final String endContract = "<abstime name='end' val='1969-12-31T19:00:00.000-05:00' null='true'/>";

	public Abstime end();
	
	public static final String sourceContract = "<uri name='source' null='true'/>";

	public Uri source();
	
	public static final String unackedContract = "<bool name='unacked' null='true'/>";

	public Bool unacked();
	
	public static final String activeContract = "<bool name='active' null='true'/>";

	public Bool active();

}
