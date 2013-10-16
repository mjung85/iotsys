/*
 * This code licensed to public domain
 */
package obix.ui;  

import obix.Obj;
import obix.net.SessionWatch;
import obix.net.WatchListener;

/**
 * View is the base class for the viewers of the current uri
 * which fill the main content area of a shell
 *
 * @author    Brian Frank
 * @creation  13 Sept 05
 * @version   $Revision$ $Date$
 */
public abstract class View
  extends Pane   
  implements WatchListener
{                   
    
////////////////////////////////////////////////////////////////
// Construction 
////////////////////////////////////////////////////////////////

  public View(Shell shell, String name, UiSession.Response resp)
  {           
    this.shell = shell;           
    this.name  = name;
    this.resp  = resp;      
    this.session = resp != null ? resp.session : null;
  }              

////////////////////////////////////////////////////////////////
// Methods
////////////////////////////////////////////////////////////////
  
  /**
   * Return view name.
   */
  public String getName() 
  { 
    return name; 
  }  
  
  /**
   * Called to save view when save command invoked.
   */
  public void save()
    throws Exception
  {      
    System.out.println("View.save() not implemented");
  }
  
  /**
   * Called by view when save should be enabled.
   */
  public void changed()
  {
    shell.commands.save.setEnabled(true);
  }
  
  /**
   * Called periodically to refresh data.
   */
  public void animate()
  {                          
  }

  /**
   * This callback is invoked when the view is being
   * closed to hyperlink to another suite of views.  This
   * method automatically calls deleteWatch().
   */                                              
  public void closing()
  {                    
    deleteWatch();
  }

////////////////////////////////////////////////////////////////
// Watch
////////////////////////////////////////////////////////////////
  
  /**
   * Get the watch to use for this view.  If no watch
   * has been initialized, then create a new one.  Any
   * change events for the watch are automatically routed
   * to the View.changed() callback for subclasses to
   * process.
   */
  public SessionWatch watch()                       
    throws Exception
  {                   
    if (watch == null)
    {
      watch = session.makeWatch(name, 250, this);
    }
    return watch;
  }             

  /**
   * If a watch has been created for this View, then clean 
   * it up by calling Watch.delete().  This method is 
   * automatically called by the closing() callback when 
   * the View is being  closed to hyperlink to another page.
   */
  public void deleteWatch()
  {          
    if (watch != null)
    {
System.out.println("View.deleteWatch");    
      watch.delete();
      watch = null;
    }
  }
  
  /**
   * WatchListener callback.
   */
  public void changed(Obj obj)
  {
    System.out.println("View.changed: " + obj);
  }

  /**
   * WatchListener callback.
   */
  public void closed(SessionWatch watch) {}
  
////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////
  
  public final String name;
  public final Shell shell;
  public final UiSession session;
  public final UiSession.Response resp;
  private SessionWatch watch;
  
}
