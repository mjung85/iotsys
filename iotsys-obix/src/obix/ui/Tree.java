/*
 * This code licensed to public domain
 */
package obix.ui;  

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.*;

import obix.*;

/**
 * Tree provides client side in-memory model of all the open
 * sessions and their obj trees (lazily loaded).
 *
 * @author    Brian Frank
 * @creation  3 Apr 06
 * @version   $Revision$ $Date$
 */
public class Tree
  extends JTree
{  
                
////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////

  public Tree(Shell shell)
  {         
    this.shell = shell;                    
    this.root  = makeRoot();
    this.model = new DefaultTreeModel(root);
    
    setModel(model);
    setCellRenderer(new Renderer());

    enableEvents(AWTEvent.WINDOW_EVENT_MASK); 
  }
  
  public void processMouseEvent(MouseEvent event)
  {                               
    if (event.isPopupTrigger())
    {    
      TreePath selPath = getPathForLocation(event.getX(), event.getY());
      if (selPath != null)
      {
        Node node = (Node)selPath.getLastPathComponent();
        if (node != null)
        {
          event.consume();
          popup(event, node);
          return;
        }
      }
    }
    else if (event.getID()== MouseEvent.MOUSE_PRESSED)
    {
      TreePath selPath = getPathForLocation(event.getX(), event.getY());
      if (selPath != null)
      {
        Node node = (Node)selPath.getLastPathComponent();
        if(node != null) 
        {                       
          setSelectionPath(selPath);
          event.consume();
          if (event.getClickCount() == 1) 
            singleClick(node);              
          else if (event.getClickCount() == 2) 
            doubleClick(node);
        }
        return;
      }
    }
    super.processMouseEvent(event);
  }
  
////////////////////////////////////////////////////////////////
// Sessions
////////////////////////////////////////////////////////////////

  void sessionCreated(UiSession session)
  {                         
    root.kids.add(new Node(root, session));
    model.reload();
  }

////////////////////////////////////////////////////////////////
// Eventing
////////////////////////////////////////////////////////////////

  public void singleClick(Node node)
  {                        
  }

  public void doubleClick(Node node)
  {                       
    if (node.href == null) return;
    shell.hyperlink(node.href);
  }

  public void popup(MouseEvent e, Node node)
  {                                                         
    if (node.obj == null) return;
    JPopupMenu menu = shell.commands.makePopup(node.obj);
    if (menu == null) menu = new JPopupMenu();
    menu.add(new Refresh(node));
    menu.show(e.getComponent(), e.getX(), e.getY());
  }       

////////////////////////////////////////////////////////////////
// Renderer
////////////////////////////////////////////////////////////////

  class Renderer
    extends DefaultTreeCellRenderer
  {
  
    public Component getTreeCellRendererComponent(JTree  tree,
      Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
      if (value instanceof Node)
      {
        Icon icon = ((Node)value).icon;
        if (icon != null)
        {
          setOpenIcon(icon);
          setClosedIcon(icon);
          setLeafIcon(icon);
        }
      }
      
      return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }
    
  }

////////////////////////////////////////////////////////////////
// Node
////////////////////////////////////////////////////////////////

  static Node makeRoot()
  {
    Node root = new Node();
    root.kids = new Vector();
    return root;        
  }

  static class Node implements TreeNode
  { 
    // master root               
    Node() 
    {     
      this.name    = "Sessions";
      this.href    = new Uri("spy:sessions");
      this.session = null;  
      this.leaf    = false;
      this.icon    = Utils.icon("x16/cloud.png");
    }
    
    // session root
    Node(Node parent, UiSession session) 
    {                 
      this.parent = parent;     
      this.session = session;
      this.name = session.getLobbyUri().toString();
      this.href = session.getLobbyUri();
      this.leaf    = false;
      this.icon    = Utils.icon("x16/host.png");
    }
    
    // obj node
    Node(Node parent, Obj obj) 
    {
      this.parent = parent;         
      this.session = parent.session;        
      this.obj     = obj;
      
      name = obj.toDisplayName();
      if (name == null) name = obj.getElement();
      
      href = obj.getNormalizedHref();
      
      // if not a ref or list and no inline children,
      // then we can assume this node doesn't have kids
      leaf = (obj.size() == 0 && !(obj instanceof Ref || obj instanceof List));
      
      // get icon (asynchronously)
      icon = session.loadIcon(obj);
    }
    
    public Enumeration children() { return kids().elements(); }
    public boolean getAllowsChildren() { return true; }
    public TreeNode getChildAt(int index) { return (TreeNode)kids().get(index); }
    public int getChildCount() { return kids().size(); }
    public int getIndex(TreeNode node) { return kids().indexOf(node); }
    public TreeNode getParent() { return parent; }
    public boolean isLeaf() { return leaf; }
    public String toString() { return name; }    
    
    // lame!
    public TreePath getPath()
    {              
      ArrayList acc = new ArrayList();
      for (Node n = this; n != null; n = n.parent)
        acc.add(0, n);
      return new TreePath(acc.toArray());
    }
    
    public void refresh(Tree tree)
    {               
      kids = null;        
      tree.collapsePath(getPath());
      tree.model.reload(this);
      tree.expandPath(getPath());
    }
    
    Vector kids()
    {                  
      if (kids == null)
      {            
        System.out.println("Tree.expand: " + name + " [" + href + "]");      
        kids = new Vector();
        try
        {
          this.obj = session.read(href);
          Obj[] list = obj.list();
          for (int i=0; i<list.length; ++i)
          {
            // there different ways to handle the granularity
            // shown in the tree - here we use ref or lists
            // with an explicit hyperlink
            Obj kid = list[i];            
            if (kid.getHref() != null && (kid.isRef() || kid.isList()))
              kids.add(new Node(this, kid));
          }
        }
        catch(Exception e)
        {                              
          e.printStackTrace();
        }
      }
      return kids;
    }              
    
    Node parent;
    UiSession session;
    Obj obj;
    String name;   
    Uri href;
    Vector kids;  
    boolean leaf;    
    Icon icon;
  }

////////////////////////////////////////////////////////////////
// Popup:Refresh
////////////////////////////////////////////////////////////////

  public class Refresh extends Command
  {                       
    Refresh(Node node) 
    { 
      super(shell, "Refresh", "x16/refresh.png", null); 
      this.node = node; 
    }
    
    public void doInvoke()
    {  
      node.refresh(Tree.this);                                         
    }      
                
    Node node;
  }

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  Shell shell;
  Node root;
  DefaultTreeModel model;
  
}
