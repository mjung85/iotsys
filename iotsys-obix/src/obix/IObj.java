/*
 * This code licensed to public domain
 */
package obix;

/**
 * IObj is the base interface for contract interfaces. IObj should only be
 * implemented by subclasses of Obj.
 * 
 * @author Brian Frank
 * @creation 27 Apr 05
 * @version $Revision$ $Date$
 */
public interface IObj
{
	// //////////////////////////////////////////////////////////////
	// Attributes
	// //////////////////////////////////////////////////////////////

	public String getDisplayName();

	public void setDisplayName(String displayName);

	public String toDisplayName();

	public String getDisplay();

	public void setDisplay(String display);

	public String toDisplay();

	public Uri getHref();

	public void setHref(Uri href);

	public Uri getIcon();

	public void setIcon(Uri icon);

	public Contract getIs();

	public void setIs(Contract is);

	public String getName();

	public void setName(String name);

	public void setName(String name, boolean force);

	public boolean isNull();

	public void setNull(boolean isNull);

	public Status getStatus();

	public void setStatus(Status status);

	public boolean isWritable();

	public void setWritable(boolean writable);

	public void setWritable(boolean writable, boolean recursive);
	
	public boolean isReadable();

	public void setReadable(boolean readable);

	// //////////////////////////////////////////////////////////////
	// Extended Attributes
	// //////////////////////////////////////////////////////////////

	public boolean isHidden();

	public void setHidden(boolean hidden);

	public boolean isOverridden();

	public void setOverridden(boolean overridden);

	public boolean isDisabled();

	public void setDisabled(boolean isDisabled);

	public boolean isFaulty();

	public void setFaulty(boolean isFaulty);

	public boolean isDown();

	public void setDown(boolean isDown);

	// //////////////////////////////////////////////////////////////
	// Identity / Children
	// //////////////////////////////////////////////////////////////

	public boolean is(String is);

	public int getBinCode();

	public IObj getParent();

	public IObj getRoot();

	public IObj get(String name);

	public int size();

	public IObj[] list();

	// //////////////////////////////////////////////////////////////
	// Convenience
	// //////////////////////////////////////////////////////////////

	public boolean isVal();

	public boolean isBool();

	public boolean isInt();

	public boolean isReal();

	public boolean isStr();

	public boolean isEnum();

	public boolean isUri();

	public boolean isAbstime();

	public boolean isReltime();

	public boolean isDate();

	public boolean isTime();

	public boolean isList();

	public boolean isOp();

	public boolean isFeed();

	public boolean isRef();

	public boolean isErr();

	// //////////////////////////////////////////////////////////////
	// Values
	// //////////////////////////////////////////////////////////////

	public boolean getBool();

	public void setBool(boolean val);

	public long getInt();

	public void setInt(long val);

	public double getReal();

	public void setReal(double val);

	public String getStr();

	public void setStr(String val);

	public void set(IObj obj);

}
