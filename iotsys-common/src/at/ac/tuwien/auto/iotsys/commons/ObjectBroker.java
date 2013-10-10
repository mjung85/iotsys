package at.ac.tuwien.auto.iotsys.commons;

import java.util.HashMap;

import obix.Obj;
import obix.Uri;

public interface ObjectBroker
{
	public abstract HashMap<String, String> get_ipv6MappingTable();

	public abstract Obj pullObj(Uri href);

	public abstract Obj pushObj(Uri href, Obj input, boolean isOp) throws Exception;

	public abstract void addObj(Obj o, String ipv6Address);

	public abstract String getIPv6LinkedHref(String ipv6Address);

	public abstract boolean containsIPv6(String ipv6Address);

	public void addObj(Obj o);

	public void addObj(Obj o, boolean listInLobby);

	public abstract void removeObj(String href);

	public abstract Obj invokeOp(Uri uri, Obj input);

	public abstract String getCoRELinks();

	public void addHistoryToDatapoints(Obj obj);

	public void enableGroupComm(Obj obj);

	public void addHistoryToDatapoints(Obj obj, int countMax);

	public void enableObjectRefresh(Obj obj);

	public void enableObjectRefresh(Obj obj, long interval);

	public void disableObjectRefresh(Obj obj);

	public void shutdown();

	public MdnsResolver getMDnsResolver();

	public void setMdnsResolver(MdnsResolver resolver);
}
