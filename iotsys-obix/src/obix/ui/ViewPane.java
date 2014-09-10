/*
 * This code licensed to public domain
 */
package obix.ui;  

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import obix.Uri;

/**
 * ViewPane is the container for all the view tabs.
 *
 * @author    Brian Frank
 * @creation  13 Sept 05
 * @version   $Revision$ $Date$
 */
public class ViewPane
  extends Pane   
  implements ChangeListener
{                   
    
////////////////////////////////////////////////////////////////
// Construction 
////////////////////////////////////////////////////////////////

  public ViewPane(Shell shell, Uri activeUri, View[] views)
  {             
    this.shell = shell;
    this.views = views;                     
    this.activeUri = activeUri;
    this.tabs = new JTabbedPane(JTabbedPane.BOTTOM);
    
    tabs.addChangeListener(this);

    for (int i=0; i<views.length; ++i)
      tabs.addTab(views[i].getName(), views[i]);
        
    add(tabs, BorderLayout.CENTER);
  }                  

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

  public View activeView()
  {
    return activeView;
  }

////////////////////////////////////////////////////////////////
// State
////////////////////////////////////////////////////////////////

  public void syncFrom(ViewPane old)
  {                     
    // don't sync unless we have the same UI
    if (activeUri == null || !activeUri.equals(old.activeUri)) return;
    
    // try to pre-select the same view as was
    // open before the hyperlink
    String activeName = old.activeView.getName();
    for (int i=0; i<views.length; ++i)
      if (views[i].getName().equals(activeName))
        { tabs.setSelectedIndex(i); break; }
  }


////////////////////////////////////////////////////////////////
// Eventing
////////////////////////////////////////////////////////////////
   
  /**
   * Callback when selected tab changes.
   */
  public void stateChanged(ChangeEvent evt) 
  {    
    activeView = views[tabs.getSelectedIndex()]; 
  }
  
////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////
 
  Shell shell;
  JTabbedPane tabs;  
  Uri activeUri;
  View[] views;   
  View activeView;
}
