/*
 * This code licensed to public domain
 */
package obix.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import obix.*;

/**
 * SessionWatch manages a client side watch a session.
 * See ObixSession.makeWatch().
 *
 * @author    Brian Frank
 * @creation  12 Sept 05
 * @version   $Revision$ $Date$
 */
public class SessionWatch
{

////////////////////////////////////////////////////////////////
// Factory
////////////////////////////////////////////////////////////////

  /**
   * Package private factory, use ObixSession.SessionWatch().
   */
  static SessionWatch make(ObixSession session, String name, long pollPeriod)
    throws Exception
  {
    // get watch service from lobby
    if (session.watchService == null)
      throw new Exception("Lobby missing watchService with valid href");

    // get make operation
    Op makeOp = (Op)session.watchService.get("make");
    if (makeOp == null || makeOp.getNormalizedHref() == null)
      throw new Exception("watchService missing op with valid href");

    // invoke make and get back watch obj
    Obj watchObj = session.invoke(makeOp, new Obj());

    // now we can create and start our client side watch
    SessionWatch watch = new SessionWatch(session, name, watchObj, pollPeriod);
    watch.start();
    return watch;
  }

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////

  /**
   * Private constructor
   */
  private SessionWatch(ObixSession session, String name, Obj watchObj, long pollPeriod)
    throws Exception
  {
    this.session         = session;
    this.name            = name;
    this.watchObj        = watchObj;
    this.addHref         = watchObj.getChildHref("add");
    this.removeHref      = watchObj.getChildHref("remove");
    this.pollChangesHref = watchObj.getChildHref("pollChanges");
    this.pollRefreshHref = watchObj.getChildHref("pollRefresh");
    this.deleteHref      = watchObj.getChildHref("delete");

    // read server specified lease time (we assume at this
    // point the server will never change it from under us)
    Reltime lease = (Reltime)watchObj.get("lease");
    if (lease == null)
      throw new Exception("Watch missing lease object " + watchObj);
    this.lease = lease.get();
    this.leaseHref = lease.getNormalizedHref();

    // set pollPeriod (and automaticaly request
    // longer lease time if needed)
    setPollPeriod(pollPeriod);
  }

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

  /**
   * Get associated session for this watch.
   */
  public ObixSession getSession()
  {
    return session;
  }

  /**
   * Get the obj representing the watch on the server.
   */
  public Obj getWatchObj()
  {
    return watchObj;
  }

  /**
   * Get name assigned by programmer
   */
  public String getName()
  {
    return name;
  }

  /**
   * Get friendly string with name.
   */
  public String toString()
  {
   return "Watch:" + name + " :: " + (watchObj!=null?watchObj.getHref():null);
  }

////////////////////////////////////////////////////////////////
// Item Management
////////////////////////////////////////////////////////////////

  /**
   * Get the number of objects in this subscription.
   */
  public int size()
  {
    return items.size();
  }

  /**
   * Get the current obj value for specified index (0 to size-1).
   */
  public Obj get(int index)
  {
    return ((Item)items.get(index)).obj;
  }

  /**
   * Get the current obj value for href.
   */
  public Obj get(Uri href)
  {
    return ((Item)hrefToItem.get(href.toString())).obj;
  }

  /**
   * Get the time (millis since epoch) that the obj at the
   * specified index was updated by a poll operation.
   */
  public long getLastUpdate(int index)
  {
    return ((Item)items.get(index)).lastUpdate;
  }

  /**
   * Get the list of objects currently subscribed.
   */
  public Obj[] list()
  {
    Obj[] list = new Obj[items.size()];
    for (int i=0; i<list.length; ++i) list[i] = get(i);
    return list;
  }

  public Iterator hrefs()
  {
    return hrefToItem.keySet().iterator();
  }
  
////////////////////////////////////////////////////////////////
// Item Management
////////////////////////////////////////////////////////////////

  /**
   * Convenience to add one one href to the watch.
   */
  public Obj add(Uri href)
    throws Exception
  {
    return add(new Uri[] { href })[0];
  }

  /**
   * Add the specified uris to this watch and
   * return the current values.
   */
  public Obj[] add(Uri[] hrefs)
    throws Exception
  {
    // create input object
    Obj in = new Obj();
    in.setIs(new Contract("obix:WatchIn"));
    in.add(new List("hrefs", new Contract("obix:Uri")).addAll(hrefs));

    // invoke add operation
    Obj out = session.invoke(addHref, in);
    if (debug)
    {
      System.out.println("-- ADD: " + name);
      out.dump();
    }
    Obj[] added = ((List)out.get("values")).list();
    long now = System.currentTimeMillis();

    // map added into our lookup tables
    for (int i=0; i<added.length; ++i)
    {
      Obj obj = added[i];
      String href = obj.getHref().toString();
      Item item = (Item)hrefToItem.get(href);
      if (item == null)
      {
        hrefToItem.put(href, item = new Item());
        items.add(item);
      }
      item.obj = obj;
      item.lastUpdate = now;
    }

    return added;
  }

  /**
   * Remove the uris at the specified indexes.
   */
  public void remove(Uri[] hrefs)
    throws Exception
  {
    Item[] items = new Item[hrefs.length];
    for (int i=0; i<items.length; ++i)
      items[i] = (Item)hrefToItem.get(hrefs[i].toString());
    remove(items, hrefs);
  }

  /**
   * Remove the uris at the specified indexes.
   */
  public void remove(int[] indexes)
    throws Exception
  {
    Item[] items = new Item[indexes.length];
    Uri[] hrefs = new Uri[items.length];
    for (int i=0; i<items.length; ++i)
    {
      items[i] = (Item)this.items.get(indexes[i]);
      hrefs[i] = items[i].obj.getHref();
    }
    remove(items, hrefs);
  }

  /**
   * Remove implementation.
   */
  private void remove(Item[] items, Uri[] hrefs)
    throws Exception
  {
    // create input object
    Obj in = new Obj();
    in.setIs(new Contract("obix:WatchIn"));
    in.add(new List("hrefs", new Contract("obix:Uri")).addAll(hrefs));

    if (debug)
    {
      System.out.println("-- Remove: " + name);
      in.dump();
    }

    // invoke add operation
    session.invoke(removeHref, in);

    // remove from our lookup tables
    for (int i=0; i<items.length; ++i)
    {
      Item item = items[i];
      this.items.remove(item);
      hrefToItem.remove(item.obj.getHref().toString());
    }
  }

////////////////////////////////////////////////////////////////
// Poll
////////////////////////////////////////////////////////////////

  /**
   * Get number of milliseconds for the lease time.  This
   * time is specified the server.
   */
  public long getLease()
  {
    return lease;
  }

  /**
   * Set number of milliseconds for the lease time.  This method
   * will synchronously attempt to change the lease time (blocks
   * until network request finishes).  Return the actual lease
   * time given to us by the server.
   */
  public long setLease(long desiredLease)
    throws Exception
  {
    if (leaseHref == null) throw new Exception("Lease time missing href");
    Reltime x = new Reltime(desiredLease);
    x.setHref(leaseHref);
    x = (Reltime)session.write(x);
    return this.lease = x.get();
  }

  /**
   * Get number of milliseconds between each poll. This value
   * is initialized specified in ObixSession.makeWatch(), but
   * can be changed via setPollPeriod.
   */
  public long getPollPeriod()
  {
    return pollPeriod;
  }

  /**
   * Set number of milliseconds between each poll. The initial
   * pollPeriod is specified during ObixSession.makeWatch().
   * This method will check that the lease is safely larger
   * than the pollPeriod - if not this method will synchronously
   * attempt to change the lease time (blocks until network
   * request finishes).  It is possible that the server will
   * not honor the required lease time, in which case we will
   * automatically shorten our pollPeriod accordingly.  This
   * method doesn't take effect until the next poll cycle - although
   * you can force an immediate poll using the pollChanges()
   * method.  Return actual pollPeriod in use.
   */
  public long setPollPeriod(long pollPeriod)
    throws Exception
  {
    // give ourselves a safety net of 5sec
    long desiredLease = pollPeriod + 5000;

    // if we don't have enough wiggle room with current
    // lease, then attempt to increate the lease time
    if (desiredLease > lease) setLease(desiredLease);

    // if server didn't want to shorten lease time,
    // just decrease pollPeriod
    if (desiredLease > lease)
    {
      pollPeriod = lease - 5000;
      if (pollPeriod < 100)
        throw new Exception("Lease time is too short: " + lease);
    }

    return this.pollPeriod = pollPeriod;
  }

  /**
   * Get millis of last poll attempt.
   */
  public long lastPollAttempt()
  {
    return lastPollAttempt;
  }

  /**
   * Get millis of last poll success.
   */
  public long lastPollSuccess()
  {
    return lastPollSuccess;
  }

  /**
   * Synchronously poll the server for just changes which
   * have occurred since the last poll.
   */
  public void pollChanges()
    throws Exception
  {
    poll(pollChangesHref, false);
  }

  /**
   * Synchronously poll the server for a completely fresh
   * update.
   */
  public void pollRefresh()
    throws Exception
  {
    poll(pollRefreshHref, true);
  }

  /**
   * Common implementation for pollChanges and pollRefresh.
   */
  private void poll(Uri opHref, boolean refresh)
    throws Exception
  {
    lastPollAttempt = System.currentTimeMillis();

    Obj out = session.invoke(opHref, null);
    long now = System.currentTimeMillis();

    if (debug)
    {
      System.out.println("-- POLL: " + name + " [refresh=" + refresh + "]");
      out.dump();
    }

    // map values to our lookup tables
    Obj[] values = ((List)out.get("values")).list();
    for (int i=0; i<values.length; ++i)
    {
      Obj obj = values[i];
      String href = obj.getHref().toString();
      Item item = (Item)hrefToItem.get(href);
      if (item == null)
      {
        System.out.println("WARNING: polled href not in my list: " + href);
        continue;
      }
      item.obj = obj;
      item.lastUpdate = now;

      // callback listeners
      fireChanged(obj);
    }

    lastPollSuccess = now;
  }

////////////////////////////////////////////////////////////////
// Listener
////////////////////////////////////////////////////////////////

  public void addListener(WatchListener listener)
  {
    synchronized(listeners)
    {
      listeners.add(listener);
    }
  }

  public void removeListener(WatchListener listener)
  {
    synchronized(listeners)
    {
      listeners.remove(listener);
    }
  }

  public WatchListener[] getListeners()
  {
    synchronized(listeners)
    {
      return (WatchListener[])listeners.toArray(new WatchListener[listeners.size()]);
    }
  }

  public void fireChanged(Obj obj)
  {
    WatchListener[] listeners = getListeners();
    for (int i=0; i<listeners.length; ++i)
    {
      try
      {
        listeners[i].changed(obj);
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
    }
  }

  public void fireClosed()
  {
    WatchListener[] listeners = getListeners();
    for (int i=0; i<listeners.length; ++i)
    {
      try
      {
        listeners[i].closed(this);
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
    }
  }

////////////////////////////////////////////////////////////////
// Lifecycle
////////////////////////////////////////////////////////////////

  /**
   * Close and remove this subscription.
   */
  public void delete()
  {
    stop();
    try
    {
      session.invoke(deleteHref, null);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    session.watches.remove(name);
    session = null;
  }

  /**
   * Start the watch poll thread.
   */
  void start()
  {
    poller = new Thread(toString() + "-Poller")
    {
      public void run() { pollLoop(); }
    };
    alive = true;
    poller.start();
  }

  /**
   * Stop the watch poll thread (public API is dispose)
   */
  void stop()
  {
    alive = false;
    if (poller != null) poller.interrupt();
    poller = null;
  }

  /**
   * Loop run by the poller thread.
   */
  void pollLoop()
  {
    while(alive)
    {
      long nextPollAttempt = lastPollAttempt + pollPeriod;
      long now = System.currentTimeMillis();
      try
      {
        long sleepTime = nextPollAttempt - now;
        if (sleepTime > 0)
          Thread.sleep(sleepTime);

        if (alive) pollChanges();
      }
      catch(Exception e)
      {
        e.printStackTrace();
        if (alive)
        {
          if (session != null)
          {
            session.watches.remove(name);
            session = null;
          }
        }
        alive = false;
      }
    }
    fireClosed();
  }

////////////////////////////////////////////////////////////////
// Item
////////////////////////////////////////////////////////////////

  static class Item
  {
    Obj obj;
    long lastUpdate;
  }

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  public boolean debug = false;

  ObixSession session;
  String name;
  Obj watchObj;
  long lease;
  Uri leaseHref;
  Uri addHref;
  Uri removeHref;
  Uri pollChangesHref;
  Uri pollRefreshHref;
  Uri deleteHref;
  ArrayList items = new ArrayList();
  HashMap hrefToItem = new HashMap();
  long pollPeriod;
  long lastPollAttempt;
  long lastPollSuccess;
  Thread poller;
  boolean alive;
  ArrayList listeners = new ArrayList();

}
