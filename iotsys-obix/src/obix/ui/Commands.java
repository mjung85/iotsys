/*
 * This code licensed to public domain
 */
package obix.ui;  

import java.awt.event.ActionEvent;

import javax.swing.*;

import obix.Obj;
import obix.Op;
import obix.Uri;

/**
 * Main class for obix Swing application.
 *
 * @author    Brian Frank
 * @creation  13 Sept 05
 * @version   $Revision$ $Date$
 */
public class Commands
{               

////////////////////////////////////////////////////////////////
// Construction
////////////////////////////////////////////////////////////////

  public Commands(Shell shell)
  {                      
    this.shell = shell;
  }

////////////////////////////////////////////////////////////////
// Menus
////////////////////////////////////////////////////////////////

  public JMenuBar makeMenuBar()
  {                 
    JMenuBar bar = new JMenuBar();
    bar.add(makeFileMenu()); 
    bar.add(makeToolsMenu()); 
    bar.add(makeHelpMenu());
    return bar;
  }                              
  
  private JMenu makeFileMenu()
  {       
    JMenu menu = new JMenu("File");
    menu.add(makeMenuItem(back));
    menu.add(makeMenuItem(forward));
    menu.add(makeMenuItem(up));
    menu.add(makeMenuItem(refresh));
    menu.add(new JSeparator());
    menu.add(makeMenuItem(save));
    menu.add(new JSeparator());
    menu.add(makeMenuItem(exit));
    return menu;
  }     

  private JMenu makeToolsMenu()
  {       
    JMenu menu = new JMenu("Tools");
    menu.add(makeMenuItem(localHost));
    menu.add(makeMenuItem(sessions));
    menu.add(new JSeparator());
    menu.add(makeMenuItem(verbose)); 
    menu.add(makeMenuItem(useWritable));
    return menu;
  }     

  private JMenu makeHelpMenu()
  {       
    JMenu menu = new JMenu("Help");
    menu.add(makeMenuItem(about));
    return menu;
  }     
  
  public static JMenuItem makeMenuItem(Command cmd)
  {                      
    JMenuItem item;
    if (cmd instanceof ToggleCommand)       
    {
      item = new JCheckBoxMenuItem(cmd);
      item.setSelected(((ToggleCommand)cmd).isSelected());           
    }
    else
    {
      item = new JMenuItem(cmd);
    }
    cmd.bind(item);
    return item;
  }

////////////////////////////////////////////////////////////////
// ToolBar
////////////////////////////////////////////////////////////////

  public JToolBar makeToolBar()
  {                 
    JToolBar bar = new JToolBar();
    bar.setFloatable(false);
    bar.add(makeToolBarButton(back)); 
    bar.add(makeToolBarButton(forward)); 
    bar.add(makeToolBarButton(up)); 
    bar.add(makeToolBarButton(refresh)); 
    bar.add(new JSeparator());
    bar.add(makeToolBarButton(save)); 
    return bar;
  }                        
  
  public static AbstractButton makeToolBarButton(Command cmd)
  {
    AbstractButton b;
    if (cmd instanceof ToggleCommand)
    {
      b = new JToggleButton(cmd);
      b.setSelected(((ToggleCommand)cmd).isSelected());           
    }
    else
    {
      b = new JButton(cmd);
    }
    b.setFocusable(false);
    b.setText(null);
    //b.setMargin(new Insets(2, 2, 2, 2));
    b.setBorderPainted(false);
    cmd.bind(b);
    return b;
  }              

////////////////////////////////////////////////////////////////
// Popup
////////////////////////////////////////////////////////////////

  public JPopupMenu makePopup(Obj obj)
  {
    JPopupMenu menu = new JPopupMenu();  
    Op[] ops = (Op[])obj.list(Op.class);
    if (ops.length > 0)
    {
      JMenu sub = new JMenu("Operations");    
      sub.setIcon(UiSession.iconOp);
      for (int i=0; i<ops.length; ++i)
        sub.add(new Invoke(ops[i]));
      menu.add(sub);
    }
    if (obj.getHref() != null) menu.add(new Goto(obj.getNormalizedHref()));  
    if (obj instanceof Op)     menu.add(new Invoke((Op)obj));
    return menu;
  }

////////////////////////////////////////////////////////////////
// Update
////////////////////////////////////////////////////////////////

  public void update()
  { 
    History history = shell.history;
    back.setEnabled(history.isBackEnabled());
    forward.setEnabled(history.isForwardEnabled());   
    up.setEnabled(history.isUpEnabled());  
  }              

////////////////////////////////////////////////////////////////
// Command
////////////////////////////////////////////////////////////////

  public abstract class MyCommand 
    extends Command
  {               
    public MyCommand(String name, String icon, String accelerator)
    {
      super(null, name, icon, accelerator);    
    }

    public MyCommand(String name)
    {
      this(name, null, null);
    }           
    
    public Shell shell()
    {
      return shell;
    }
  }

////////////////////////////////////////////////////////////////
// ToggleCommand
////////////////////////////////////////////////////////////////
  
  // is it possible that this isn't provided by Swing automatically?
  public abstract class ToggleCommand 
    extends MyCommand
  {                
    public ToggleCommand(String name, String icon, String accelerator)
    {
      super(name, icon, accelerator);    
    }

    public ToggleCommand(String name)
    {
      super(name);
    }            
    
    public boolean isSelected() 
    {
      return selected;
    }               
    
    public void setSelected(boolean sel)
    {                
      if (this.selected == sel) return;    
      this.selected = sel;            
      JComponent[] bindings = bindings();
      for (int i=0; i<bindings.length; ++i)
        setSelected(bindings[i], sel);
    }
    
    public void actionPerformed(ActionEvent event)
    {                
      setSelected( getSelected(event.getSource()) );
      super.actionPerformed(event);
    }               
    
    boolean getSelected(Object c)
    {
      if (c instanceof JToggleButton) return ((JToggleButton)c).isSelected();
      if (c instanceof JCheckBoxMenuItem) return ((JCheckBoxMenuItem)c).getState();
      throw new IllegalStateException(c.getClass().getName());
    }                     
    
    void setSelected(Object c, boolean sel)
    {                               
      if (c instanceof JToggleButton) { ((JToggleButton)c).setSelected(sel); return; }
      if (c instanceof JCheckBoxMenuItem) { ((JCheckBoxMenuItem)c).setState(sel); return; }
      // skip
    }
    
    boolean selected;
  }   
  
////////////////////////////////////////////////////////////////
// Back
////////////////////////////////////////////////////////////////

  public class Back extends MyCommand
  {                       
    Back() { super("Back", "x16/arrowLeft.png", "alt LEFT"); }
    public void doInvoke()
    {                      
      String uri = shell.history.back();
      if (uri == null) return;
      shell.hyperlink(new HyperlinkInfo(uri, false));         
    }
  }

////////////////////////////////////////////////////////////////
// Forward
////////////////////////////////////////////////////////////////

  public class Forward extends MyCommand
  {                       
    Forward() { super("Forward", "x16/arrowRight.png",  "alt RIGHT"); }
    public void doInvoke()
    {                               
      String uri = shell.history.forward();
      if (uri == null) return;
      shell.hyperlink(new HyperlinkInfo(uri, false));         
    }
  }

////////////////////////////////////////////////////////////////
// Up
////////////////////////////////////////////////////////////////

  public class Up extends MyCommand
  {                       
    Up() { super("Up", "x16/upLevel.png", "alt UP"); }
    public void doInvoke()
    {                                
      String uriStr = shell.uri().toString();
      if (uriStr.startsWith("spy:"))
      {
        int slash = uriStr.lastIndexOf('/');
        if (slash > 0)
        {
          shell.hyperlink(uriStr.substring(0, slash));
          return;
        }
      }
      
      Uri parent = new Uri(shell.uri()).parent();
      if (parent != null)
        shell.hyperlink(parent.toString());
    }
  }

////////////////////////////////////////////////////////////////
// Refresh
////////////////////////////////////////////////////////////////

  public class Refresh extends MyCommand
  {                       
    Refresh() { super("Refresh", "x16/refresh.png", "control R"); }
    public void doInvoke()
    {                  
      shell.refresh();
    }
  }

////////////////////////////////////////////////////////////////
// Save
////////////////////////////////////////////////////////////////

  public class Save extends MyCommand
  {                       
    Save() { super("Save", "x16/save.png", "control S"); }
    public void doInvoke() throws Exception
    {                                   
      shell.activeView().save();
      setEnabled(false);
    }
  }

////////////////////////////////////////////////////////////////
// Exit
////////////////////////////////////////////////////////////////

  public class Exit extends MyCommand
  {                       
    Exit() { super("Exit", "x16/close.png", "control X"); }
    public void doInvoke()
    {                 
      shell.savePrefs();
      System.exit(0);
    }
  }

////////////////////////////////////////////////////////////////
// Verbose
////////////////////////////////////////////////////////////////

  public class Verbose extends ToggleCommand
  {                       
    Verbose() { super("Verbose", "x16/v.png", null); setSelected(true); }
    public void doInvoke()
    {                
      refresh.invoke();
    }
  }

////////////////////////////////////////////////////////////////
// Use Writable
////////////////////////////////////////////////////////////////

  public class UseWritable extends ToggleCommand
  {                       
    UseWritable() { super("Use Writable", null, null); setSelected(true); }
    public void doInvoke()
    {                
      refresh.invoke();
    }
  }

////////////////////////////////////////////////////////////////
// LocalHost
////////////////////////////////////////////////////////////////

  public class LocalHost extends MyCommand
  {                       
    LocalHost() { super("LocalHost", null, null); }
    public void doInvoke()
    {  
      shell.hyperlink("http://localhost/obix");                                         
    }
  }

////////////////////////////////////////////////////////////////
// Sessions
////////////////////////////////////////////////////////////////

  public class Sessions extends MyCommand
  {                       
    Sessions() { super("Sessions", null, null); }
    public void doInvoke()
    {  
      shell.hyperlink("spy:sessions");                                         
    }
  }

////////////////////////////////////////////////////////////////
// About
////////////////////////////////////////////////////////////////

  public class About extends MyCommand
  {                       
    About() { super("About", "x16/questionMark.png", null); }
    public void doInvoke()
    {  
      shell.hyperlink("spy:splash");                                         
    }
  }

////////////////////////////////////////////////////////////////
// Popup:Goto
////////////////////////////////////////////////////////////////

  public class Goto extends MyCommand
  {                       
    Goto(Uri uri) { super("Goto", "x16/arrowRight.png", null); this.uri = uri; }
    public void doInvoke()
    {  
      shell.hyperlink(uri);                                         
    }                  
    Uri uri;
  }

////////////////////////////////////////////////////////////////
// Popup:Invoke
////////////////////////////////////////////////////////////////

  public class Invoke extends MyCommand
  {                       
    Invoke(Op op) { super(op.toDisplayName(), "x16/exclaim.png", null); this.op = op; }
    public void doInvoke() throws Exception
    {    
      UiSession session = UiSession.make(shell, op.getNormalizedHref());
      session.invoke(shell, op);
    }                  
    Op op;
  }

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////
  
  public final Shell shell;
  
  // File
  public final Command back    = new Back();
  public final Command forward = new Forward();
  public final Command up      = new Up();
  public final Command refresh = new Refresh();
  public final Command save    = new Save();
  public final Command exit    = new Exit();

  // Tools
  public final Command localHost = new LocalHost();
  public final Command sessions  = new Sessions();
  public final ToggleCommand verbose = new Verbose();
  public final ToggleCommand useWritable = new UseWritable();
  
  // Help
  public final Command about   = new About();

}
