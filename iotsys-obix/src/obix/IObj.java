/*
 * This code licensed to public domain
 */
package obix;


/**
 * IObj is the base interface for contract interfaces.  IObj should
 * only be implemented by subclasses of Obj.
 *
 * @author    Brian Frank
 * @creation  27 Apr 05
 * @version   $Revision$ $Date$
 */
public interface IObj
{ 

////////////////////////////////////////////////////////////////
// Identity
////////////////////////////////////////////////////////////////

  public String getName();
  public Obj getParent();
  public Obj getRoot();
  public Uri getHref();
  public Uri getNormalizedHref();
  public void setHref(Uri href);
  public Contract getIs();
  public void setIs(Contract is);

////////////////////////////////////////////////////////////////
// Convenience
////////////////////////////////////////////////////////////////
  
  public boolean isVal();
  public boolean isBool();
  public boolean isInt();
  public boolean isReal();
  public boolean isEnum();
  public boolean isStr();
  public boolean isAbstime();
  public boolean isReltime();
  public boolean isUri();
  public boolean isList();
  public boolean isOp();
  public boolean isRef();
  public boolean isFeed();
  public boolean isErr();
  
  public boolean getBool();
  public long getInt();
  public double getReal();
  public String getStr();

  public void setBool(boolean val);
  public void setInt(long val);
  public void setReal(double val);
  public void setStr(String val);

////////////////////////////////////////////////////////////////
// Facets
////////////////////////////////////////////////////////////////

  public String toDisplayString();
  public String getDisplay();
  public void setDisplay(String display);
  public String toDisplayName();
  public String getDisplayName();
  public void setDisplayName(String displayName);
  public Uri getIcon();
  public void setIcon(Uri icon);
  public Status getStatus();
  public void setStatus(Status status);
  public boolean isNull();
  public void setNull(boolean isNull);
  public boolean isWritable();
  public void setWritable(boolean writable);
  public void setWritable(boolean writable, boolean recursive);

////////////////////////////////////////////////////////////////
// Children
////////////////////////////////////////////////////////////////
  
  public Obj get(String name);
  public int size();
  public Obj[] list();
  public Obj add(Obj kid);
  public Obj addAll(Obj[] kid);
  public void remove(Obj kid);
  public void replace(Obj oldObj, Obj newObj);
  public void removeThis();
  
}
