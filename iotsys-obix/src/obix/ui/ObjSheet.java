/*
 * This code licensed to public domain
 */
package obix.ui;  

import java.awt.*;
import java.awt.event.MouseEvent;

import javax.swing.*;

import obix.Obj;

/**
 * ObjSheet provides a property sheet like interface to view
 * and edit an obix Obj tree.
 *
 * @author    Brian Frank
 * @creation  13 Sept 05
 * @version   $Revision$ $Date$
 */
public class ObjSheet
  extends Editor
{                  

////////////////////////////////////////////////////////////////
// Prompt
////////////////////////////////////////////////////////////////
    
  /**
   * Prompt the user with a dialog to edit the specified
   * target Obj.  Return null if dialog canceled.
   */
  public static Obj prompt(Shell shell, String title, UiSession session, Obj target)
    throws Exception
  {
    ObjSheet sheet = new ObjSheet(shell, session);                                    
    sheet.load(target);
    JOptionPane pane = new JOptionPane(sheet, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
    JDialog dialog = pane.createDialog(shell, title);
    dialog.pack();
    dialog.setVisible(true);  
    if (pane.getValue() == null || pane.getValue().equals(new Integer(JOptionPane.CANCEL_OPTION))) return null;
    sheet.save(target);
    return target;
  }
  
////////////////////////////////////////////////////////////////
// Construction
////////////////////////////////////////////////////////////////

  public ObjSheet(Shell shell, UiSession session)
  {     
    setLayout(new Layout()); 
    this.shell = shell;                  
    this.session = session;    
  }

////////////////////////////////////////////////////////////////
// Editable
////////////////////////////////////////////////////////////////

  /**
   * Chain editable
   */
  protected void doSetEditable(boolean editable) 
  {              
    node.setEditable(editable);             
  }

////////////////////////////////////////////////////////////////
// Load
////////////////////////////////////////////////////////////////
  
  /**
   * Load UI from target
   */
  protected void doLoad(Obj target)
  {         
    removeAll();
    node = new Node(target);
  }

////////////////////////////////////////////////////////////////
// Save
////////////////////////////////////////////////////////////////
  
  /**
   * Save UI to target.
   */
  protected void doSave(Obj target) 
    throws Exception
  {         
    if (target != node.target) 
      throw new IllegalStateException("Must save back to same instance passed to load()");
    node.save(null); 
  }          
  
  /**
   * This version of save is used to walk the object tree
   * and call SaveVisitor.save(Obj) for each Val node
   * which has been changed.
   */
  public void save(SaveVisitor visitor)
    throws Exception
  {           
    node.save(visitor);
  }
         
////////////////////////////////////////////////////////////////
// Node
////////////////////////////////////////////////////////////////

  class Node implements Editor.Listener
  {                        
    Node(Obj target) 
    {       
      this.target = target;
      
      name = target.toDisplayName();
      if (session != null)
        icon = session.loadIcon(target);                                    
      else
        icon = defaultIcon;                                    
      label = new ObjLabel(name, icon, target);
      add(label);
      
      field = ObjField.make(target);
      field.addListener(this);
      field.load(target);  
      if (!target.isWritable() && shell.commands.useWritable.isSelected())         
        field.setEditable(false);
      add(field);
      
      if (isAtomic(target))
      {
        kids = new Node[0];
      }
      else      
      {
        Obj[] list = target.list();
        kids = new Node[list.length];
        for (int i=0; i<kids.length; ++i)
          kids[i] = new Node(list[i]);
      }
    }                  
    
    public boolean isAtomic(Obj target)
    {
      // hide status sub-structure to keep display cleaner
      if (target.is("obix:Status")) return true;
      return false;
    }
    
    public void changed(Editor editor)
    {               
      if (changed) return;
      changed = true;      
      label.setIcon(HighlightedImageFactory.convert(icon));
      label.repaint();  
      ObjSheet.this.fireChanged();
    }

    void setEditable(boolean editable)
    {
      if (field != null) field.setEditable(editable);
      for (int i=0; i<kids.length; ++i)
        kids[i].setEditable(editable);
    }
        
    void save(SaveVisitor visitor)
      throws Exception
    {       
      // save field if changed
      if (field != null && changed)
      {
        try
        {                      
          // save value back to target
          field.save(target);         
          
          // visitor callback
          if (visitor != null)
            visitor.save(target);
          
          // reset to unchanged
          label.setIcon(icon);    
          changed = false;
        }
        catch(Exception e)
        {
          throw new Exception("Cannot save field: " + name, e);
        }
      }
      
      // recurse
      for (int i=0; i<kids.length; ++i)
        kids[i].save(visitor);
    }                       
    
    Obj target;        // object this item models
    String name;       // target name    
    Icon icon;         // target icon (highlight on change)
    ObjLabel label;    // name label
    ObjField field;    // if val
    boolean changed;   // has the editor been changed
    Node[] kids;       // children nodes 
  }

////////////////////////////////////////////////////////////////
// ObjLabel
////////////////////////////////////////////////////////////////

  class ObjLabel extends JLabel
  {             
    ObjLabel(String text, Icon icon, Obj target)
    {
      super(text, icon, LEFT);
      this.target = target;
      this.fg = getForeground();
      enableEvents(AWTEvent.MOUSE_EVENT_MASK);   
      
    }                            
    
    public void processMouseEvent(MouseEvent e)
    {                            
      super.processMouseEvent(e); 
      
      // if not in shell or not hyperlinkable, then bail
      if (shell == null || target.getHref() == null) return; 
      
      // check for right click 
      if (e.isPopupTrigger())
      {          
        JPopupMenu menu = shell.commands.makePopup(target);
        if (menu != null)
          menu.show(e.getComponent(), e.getX(), e.getY());
        return;
      }
      
      switch(e.getID())
      {
        case MouseEvent.MOUSE_ENTERED: 
          mouseOver = true;    
          shell.status("href=" + target.getHref());                 
          setForeground(Color.blue);
          setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
          break;
        case MouseEvent.MOUSE_EXITED: 
          mouseOver = false;  
          shell.status(null);       
          setForeground(fg);
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          break;
        case MouseEvent.MOUSE_RELEASED: 
          if (mouseOver && e.getButton() == MouseEvent.BUTTON1)
            shell.hyperlink(target.getNormalizedHref());
          break;
      }
    }      
    
    Obj target;            
    Color fg;
    boolean mouseOver;       
  }

////////////////////////////////////////////////////////////////
// Layout
////////////////////////////////////////////////////////////////

  class Layout implements LayoutManager
  {          
    public void addLayoutComponent(String name, Component comp)
    {
    }

    public void removeLayoutComponent(Component comp)
    {
    }

    public Dimension minimumLayoutSize(Container parent)
    {
      return preferredLayoutSize(parent);
    }
    
    public Dimension preferredLayoutSize(Container parent)
    {        
      layoutContainer(parent);
      return new Dimension(prefw, prefh);
    }
    
    public void layoutContainer(Container parent)
    {                     
      prefw = prefh = 0;
      if (node == null) return;
      
      findMax(node, 0);
      
      prefw = lmaxw + lgapf + fmaxw;
      prefh = layout(node, 0, 0, lmaxw+lgapf);  
    } 
    
    void findMax(Node node, int x)
    {                                                            
      // find label max width taking indent into account
      lmaxw = Math.max(x+node.label.getPreferredSize().width, lmaxw);
      
      // find field max width
      if (node.field != null)
        fmaxw = Math.max(node.field.getPreferredSize().width, fmaxw);
      
      // recurse  
      for (int i=0; i<node.kids.length; ++i)
        findMax(node.kids[i], x+indent);
    }   
    
    int layout(Node node, int x, int y, int fx)
    {                            
      Dimension lpref = node.label.getPreferredSize();
      int h = lpref.height;
      
      if (node.field != null)
      {
        Dimension fpref = node.field.getPreferredSize();
        node.field.setBounds(fx, y, fpref.width, fpref.height);  
        h = Math.max(h, fpref.height);
      }           

      node.label.setBounds(x, y+(h-lpref.height)/2, lpref.width, lpref.height);
      
      y = y + h;
      for (int i=0; i<node.kids.length; ++i)
        y = layout(node.kids[i], x+indent, y+ygap, fx);
      
      return y;
    }      
    
    int lmaxw, fmaxw;   // label max width, field max width
    int prefw, prefh;   // total preferred width and height
  }          
  
  static final int indent = 10;  // node indent
  static final int lgapf  = 20;  // label field gap
  static final int ygap   = 4;   // h gap between node rows

////////////////////////////////////////////////////////////////
// SaveVisitor
////////////////////////////////////////////////////////////////

  public static interface SaveVisitor
  {
    public void save(Obj obj)
      throws Exception;
  }
  
////////////////////////////////////////////////////////////////
// Test
////////////////////////////////////////////////////////////////
  
  /*
  public static void main(String[] args)
    throws Exception
  {
    Obj obj = new Obj("foo");
    obj.add(new Str("bar", "hello"));
    obj.add(new Str("cool", "world"));
    Obj x = new Obj("x");
    obj.add(x);
      x.add(new Bool("bf",  false));
      x.add(new Bool("bt",  true));
      x.add(new Int("i",  102));
      x.add(new Real("r", 123.456));
      x.add(new Enum("e", "foo"));
      x.add(new Uri("uri", "http://www.tridium.com/"));
    Obj result = prompt((JComponent)null, "Test", null, obj);  
    if (result == null)
    {
      System.out.println("CANCEL");
    }
    else
    {                            
      ObixEncoder.dump(result);
    }
    System.exit(0);
  }
  */

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  static final ImageIcon defaultIcon = Utils.icon("x16/object.png");

  Node node;        
  Shell shell;
  UiSession session;
}
