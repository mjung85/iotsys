/*
 * This code licensed to public domain
 */
package obix.ui;  

import obix.Uri;
import obix.ui.views.ErrorView;

/**
 * HyperlinkInfo specified information to perform a hyperlink
 * and manages the lifecycle of a hyperlink.
 *
 * @author    Brian Frank
 * @creation  13 Sept 05
 * @version   $Revision$ $Date$
 */
public class HyperlinkInfo
{                         

////////////////////////////////////////////////////////////////
// Construction
////////////////////////////////////////////////////////////////

  public HyperlinkInfo(String uriStr, boolean addToHistory)
  {                                                    
    this.uriStr = uriStr;
    this.addToHistory = addToHistory;
  }

  public HyperlinkInfo(String uriStr)
  {         
    this(uriStr, true);                                                    
  }                              

////////////////////////////////////////////////////////////////
// Process
////////////////////////////////////////////////////////////////

  public void hyperlink(Shell shell)
    throws Exception
  {                                          
    this.shell = shell;    
    closeCurrentViews();
    try
    {          
      views = ViewRegistry.toBuiltin(shell, uriStr);
      if (views == null)
      {                   
        toUri();
        toSession();    
        toResponse();    
        toViews();
      }
    }
    catch(Throwable e)
    {
      e.printStackTrace();    
      views = new View[] { new ErrorView(shell, e) };
    }
    if (addToHistory) shell.history.append(uriStr);
    updateUi();
  }         

////////////////////////////////////////////////////////////////
// Close Current Views
////////////////////////////////////////////////////////////////

  void closeCurrentViews()
  {                      
    if (shell.views == null) return;
    if (shell.views.views == null) return;
      
    View[] views = shell.views.views;
    for (int i=0; i<views.length; ++i)
    {                                                   
      View view = views[i];
      try
      {                    
        view.closing();
      }
      catch(Exception e)
      {
        System.out.println("ERROR: Closing " + view.name);
      }
    }
  }

////////////////////////////////////////////////////////////////
// To Uri
////////////////////////////////////////////////////////////////

  void toUri()
  {                      
    // build uri based on what was typed in 
    uri = new Uri(uriStr);
    uri.checkAbsolute();                    
        
    // show calculated uri actually being used
    uriStr = uri.toString();
  }

////////////////////////////////////////////////////////////////
// To Session
////////////////////////////////////////////////////////////////

  void toSession()  
    throws Exception
  {             
    session = UiSession.make(shell, uri);
    if (session == null)
      throw new RuntimeException("User canceled open session dialog");
  }

////////////////////////////////////////////////////////////////
// To Response
////////////////////////////////////////////////////////////////

  void toResponse()  
    throws Exception
  {                                                
//    boolean verbose = shell.commands.verbose.isSelected();
    resp = session.request(uri);
  }

////////////////////////////////////////////////////////////////
// To View
////////////////////////////////////////////////////////////////

  void toViews()  
    throws Exception
  {                                                
    views = ViewRegistry.viewsFor(shell, resp);         
  }

////////////////////////////////////////////////////////////////
// Update UI
////////////////////////////////////////////////////////////////

  void updateUi()
  {                
    shell.commands.save.setEnabled(false);
    shell.commands.update();
    shell.locator.update(uriStr); 
    shell.setViewPane(new ViewPane(shell, uri, views));    
    shell.status(uriStr);
  }

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  String uriStr;              // ctor
  boolean addToHistory;       // ctor
  Shell shell;                // hyperlink()
  Uri uri;                    // toUri() 
  UiSession session;          // toSession()
  UiSession.Response resp;    // toResponse()
  View[] views;               // toViews() (or on catch)

}
