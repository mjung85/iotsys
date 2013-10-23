/*
 * This code licensed to public domain
 */
package obix.ui;  

import java.awt.*;
import java.awt.event.*; 
import java.util.*;     
import java.util.prefs.*;
import javax.swing.*;
import obix.*;

/**
 * Shell is the main window the obix Swing application.
 *
 * @author    Brian Frank
 * @creation  13 Sept 05
 * @version   $Revision$ $Date$
 */
public class Shell
  extends JFrame
{               

////////////////////////////////////////////////////////////////
// Construction
////////////////////////////////////////////////////////////////

  public Shell()
  {
    super("Obix Spy");
    setIconImage(Utils.img("x16/obix.png"));   
                         
    enableEvents(AWTEvent.WINDOW_EVENT_MASK); 
    
    JPanel bar = new JPanel(new BorderLayout());
    bar.add(commands.makeToolBar(), BorderLayout.WEST);
    bar.add(locator, BorderLayout.CENTER);                        
    
    splitter.setDividerLocation(180);    
    splitter.setLeftComponent(new JScrollPane(tree));
    splitter.setRightComponent(new JLabel("content"));
    
    setJMenuBar(commands.makeMenuBar());
    getContentPane().add(bar, BorderLayout.NORTH);
    getContentPane().add(splitter, BorderLayout.CENTER);
    getContentPane().add(new Pane(status, 2), BorderLayout.SOUTH);
    
    new javax.swing.Timer(500, new ActionListener() { public void actionPerformed(ActionEvent e) { animate(); } } ).start();
  }              

////////////////////////////////////////////////////////////////
// Navigation
////////////////////////////////////////////////////////////////

  public String uri()
  {
    return uri;
  }              

  public View activeView()
  {
    return views.activeView();
  }
  
  public void hyperlink(String uri)
  {            
    hyperlink(new HyperlinkInfo(uri));
  }

  public void hyperlink(Uri uri)
  {            
    hyperlink(new HyperlinkInfo(uri.toString()));
  }
  
  public void hyperlink(HyperlinkInfo hinfo)
  {                                    
    try
    {
      hinfo.hyperlink(this);
      this.uri = hinfo.uriStr;        
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }             

  public void refresh()
  {
    hyperlink(uri());             
  }
  
  public void status(String msg)
  {
    if (msg == null || msg.length() == 0) msg = " ";
    status.setText(msg);
  }                    
  
  public void animate()
  {           
    if (views != null) views.activeView().animate();
  }
  
////////////////////////////////////////////////////////////////
// View
////////////////////////////////////////////////////////////////

  void setViewPane(ViewPane newViews)
  {     
    ViewPane oldViews = this.views;
                                 
    // remove current view
    if (oldViews != null) splitter.remove(oldViews);
    
    // give oldViews a chance to transfer state to newViews
    if (oldViews != null) newViews.syncFrom(oldViews);
    
    // add new view
    this.views = newViews;
    int oldPos = splitter.getDividerLocation();
    splitter.setRightComponent(newViews);
    splitter.setDividerLocation(oldPos); // swing is lame
    invalidate();
    validate();
  }

////////////////////////////////////////////////////////////////
// Preferences
////////////////////////////////////////////////////////////////

  public void savePrefs()
  {                     
    try
    {
      Rectangle b = getBounds();    
      Preferences prefs = Preferences.userNodeForPackage(getClass());
      prefs.putInt("shell.x", b.x);
      prefs.putInt("shell.y", b.y);
      prefs.putInt("shell.w", b.width);
      prefs.putInt("shell.h", b.height);
    }
    catch(Throwable e)
    {
      e.printStackTrace();
    }
  }

  public void loadPrefs()
  {
    try
    {
      Preferences prefs = Preferences.userNodeForPackage(getClass());
      int x = prefs.getInt("shell.x", 50);
      int y = prefs.getInt("shell.y", 50);    
      int w = prefs.getInt("shell.w", 500);    
      int h = prefs.getInt("shell.h", 400);    
      setBounds(x, y, w, h);
    }
    catch(Throwable e)
    {
      e.printStackTrace();
    }
  }

////////////////////////////////////////////////////////////////
// Sessions
////////////////////////////////////////////////////////////////

  void sessionCreated(UiSession session)
  {
    tree.sessionCreated(session);    
  }

////////////////////////////////////////////////////////////////
// Eventing
////////////////////////////////////////////////////////////////

  public void processWindowEvent(WindowEvent event)
  {
    if (event.getID() == WindowEvent.WINDOW_CLOSING)
      commands.exit.invoke();
  }                         
  
////////////////////////////////////////////////////////////////
// Main
////////////////////////////////////////////////////////////////

  public static void main(String[] args)
  {                   
    String uri = args.length > 0 ? args[0] : "spy:splash";           
    Utils.initDefaults();
    Shell shell = theShell = new Shell();
    shell.loadPrefs();      
    shell.hyperlink(uri);
    shell.setVisible(true);      
  }  

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  public final Commands commands = new Commands(this);
  public final Locator locator   = new Locator(this);   
  public final History history   = new History();
  
  public static Shell theShell;

  String uri;                    
  ViewPane views;   
  HashMap state = new HashMap();  // keyed by view.class.name  
  JLabel status = new JLabel(" ");    
  JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
  Tree tree = new Tree(this);
}
