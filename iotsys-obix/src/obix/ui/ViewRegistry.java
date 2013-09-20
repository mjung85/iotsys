/*
 * This code licensed to public domain
 */
package obix.ui;  

import java.util.*;
import obix.*;
import obix.net.*;
import obix.ui.views.*;

/**
 * ViewRegistry is used to manage custom view tabs based on contracts.
 *
 * @author    Brian Frank
 * @creation  4 Apr 06
 * @version   $Revision$ $Date$
 */
public class ViewRegistry
{  

////////////////////////////////////////////////////////////////
// Factory
////////////////////////////////////////////////////////////////
  
  /**
   * Get the views for a built-in URI or return null.
   */
  public static View[] toBuiltin(Shell shell, String uriStr)
  {
    if (!uriStr.startsWith("spy:")) return null;
    if (uriStr.equals("spy:sessions")) return new View[] { new SessionsView(shell) };
    return new View[] { new SplashView(shell) };
  }
  
////////////////////////////////////////////////////////////////
// Object
////////////////////////////////////////////////////////////////
  
  /**
   * Get all the views associated with the specified response.
   */
  public static View[] viewsFor(Shell shell, UiSession.Response resp)
    throws Exception
  {
    ArrayList acc = new ArrayList();
    
    // always have source and XML tabs
    acc.add(0, new SourceView(shell, resp));
    acc.add(0, new XmlView(shell, resp));
    
    // always have ObjSheet unless obj wasn't decoded
    if (resp.obj != null)
      acc.add(0, new ObjSheetView(shell, resp));
    
    // add tabs for each contract (start least specific
    // contract and work up to primary contract inserting tabs)
    if (resp.obj != null && resp.obj.getIs() != null)
    {
      Uri[] contracts = resp.obj.getIs().list();
      for (int i=contracts.length-1; i>=0; --i)
      {
        View[] v = viewsForContract(shell, resp, contracts[i]);
        for (int j=0; v != null && j<v.length; ++j)
          acc.add(0, v[j]);
      }       
    }
    
    return (View[])acc.toArray(new View[acc.size()]);
  }
  
  /**
   * Get a custom view for the specified contract
   */
  public static View[] viewsForContract(Shell shell, UiSession.Response resp, Uri contract)
    throws Exception
  {
    String uri = contract.toString();
    if (uri.equals("obix:WatchService")) return viewsForWatchService(shell, resp);
    if (uri.equals("obix:AlarmSubject")) return viewsForAlarmSubject(shell, resp);
    return null;
  }
  
  /**
   * Get the views for the WatchService.  There is always a 
   * WatchServiceView used to make and manage watches.  Plus
   * there is a WatchView for each active watch used to manage
   * the objects in that watch.
   */
  static View[] viewsForWatchService(Shell shell, UiSession.Response resp)
    throws Exception
  {
    ArrayList acc = new ArrayList();
    acc.add(new WatchServiceView(shell, resp));
    SessionWatch[] watches = resp.session.getWatches();
    for (int i=0; i<watches.length; ++i)
      acc.add(new WatchView(shell, resp, watches[i]));
    return (View[])acc.toArray(new View[acc.size()]);
  }
  
  /**
   * Get the views for the AlarmSubject.
   */
  static View[] viewsForAlarmSubject(Shell shell, UiSession.Response resp)
    throws Exception
  {
    return new View[] { new AlarmConsoleView(shell, resp) };
  }
  
}
