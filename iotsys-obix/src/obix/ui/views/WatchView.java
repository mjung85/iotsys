/*
 * This code licensed to public domain
 */
package obix.ui.views;  

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import obix.*;
import obix.net.*;
import obix.ui.*;

/**
 * WatchView
 *
 * @author    Brian Frank
 * @creation  16 Nov 05
 * @version   $Revision$ $Date$
 */
public class WatchView
  extends View
{                   
    
////////////////////////////////////////////////////////////////
// Construction 
////////////////////////////////////////////////////////////////

  public WatchView(Shell shell, UiSession.Response resp, SessionWatch watch)
  {             
    super(shell, watch.getName(), resp); 
    this.watch = watch;                    
    
    table = new JTable(model = new Model());
    table.addMouseListener(new Controller());
    
    JPanel actions = new JPanel(new FlowLayout());
    actions.add(new JButton(new Command(shell, "Add")          { public void doInvoke() throws Exception { doAdd();         } }));
    actions.add(new JButton(new Command(shell, "Remove")       { public void doInvoke() throws Exception { doRemove();      } }));
    actions.add(new JButton(new Command(shell, "Poll Changes") { public void doInvoke() throws Exception { doPollChanges(); } }));
    actions.add(new JButton(new Command(shell, "Poll Refresh") { public void doInvoke() throws Exception { doPollRefresh(); } }));
    actions.add(new JButton(new Command(shell, "Dispose")      { public void doInvoke() throws Exception { doDispose();     } } ));
        
    add(new JScrollPane(table), BorderLayout.CENTER);          
    add(actions, BorderLayout.SOUTH);          
  }                                   

////////////////////////////////////////////////////////////////
// Events
////////////////////////////////////////////////////////////////

  public void animate()
  {
    table.repaint();
  }

  public void doAdd()
    throws Exception
  {                              
    AddPrompt p = new AddPrompt();
    p.href = "";
    p = (AddPrompt)Form.prompt(this, "Add Href to Watch", p);
    if (p == null) return;
    
    Uri uri = new Uri(p.href);
    watch.add(new Uri[] { uri }); 
    
    model.fireTableDataChanged();
  }

  public void doRemove()
    throws Exception
  {                              
    int[] rows = table.getSelectedRows();
    if (rows.length ==  0) return;
    
    watch.remove(rows); 
    
    model.fireTableDataChanged();
  }

  public void doPollChanges()
    throws Exception
  {                    
    watch.pollChanges();
    animate();          
  }

  public void doPollRefresh()
    throws Exception
  {                              
    watch.pollRefresh();
    animate();          
  }

  public void doDispose()
  {
    watch.delete();
    shell.refresh();
  }

////////////////////////////////////////////////////////////////
// Controller
////////////////////////////////////////////////////////////////

  class Controller extends MouseAdapter
  {
    public void mousePressed(MouseEvent event)
    {              
      if (event.getClickCount() == 2)
      {
        int row = table.rowAtPoint(event.getPoint());
        if (row != -1) { /* TODO */ }
      }
    }
  }

////////////////////////////////////////////////////////////////
// Model
////////////////////////////////////////////////////////////////

  class Model extends AbstractTableModel
  {                 
    public int getColumnCount() { return 4; }
    
    public int getRowCount() { return watch.size(); }
    
    public String getColumnName(int col)
    {                                   
      switch(col)
      {
        case 0:  return "Href";
        case 1:  return "Contract";
        case 2:  return "Display";
        case 3:  return "Last Update";
        default: return "?";
      }
    }
    
    public Object getValueAt(int row, int col) 
    {               
      switch(col)
      {
        case 0:  return watch.get(row).getHref();
        case 1:  return doContractsStr(watch.get(row));
        case 2:  return watch.get(row).toDisplayString();
        case 3:  return Utils.millisAgo(watch.getLastUpdate(row));
        default: return "?";
      }
    }
    
    private String doContractsStr(Obj obj)
    {            
      String s = "obix:" + obj.getElement();
      if (obj.getIs() != null)
        s += " " + obj.getIs();
      return s;
    } 
  }                              

////////////////////////////////////////////////////////////////
// AddPrompt
////////////////////////////////////////////////////////////////

  public static class AddPrompt
  {                 
    public String href;
  }

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  SessionWatch watch;
  JTable table;
  Model model;
}
