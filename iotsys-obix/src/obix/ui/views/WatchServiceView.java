/*
 * This code licensed to public domain
 */
package obix.ui.views;  
                    
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import obix.net.SessionWatch;
import obix.ui.*;

/**
 * WatchServiceView
 *
 * @author    Brian Frank
 * @creation  16 Nov 05
 * @version   $Revision$ $Date$
 */
public class WatchServiceView
  extends View
{                   
    
////////////////////////////////////////////////////////////////
// Construction 
////////////////////////////////////////////////////////////////

  public WatchServiceView(Shell shell, UiSession.Response resp)
  {             
    super(shell, "Watches", resp);                     
    
    SessionWatch[] watches = resp.session.getWatches();
    rows = new Row[watches.length];
    for (int i=0; i<rows.length; ++i)    
      rows[i] = new Row(watches[i]); 
    
    table = new JTable(model = new Model());
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.addMouseListener(new Controller());
    
    JPanel actions = new JPanel(new FlowLayout());
    actions.add(new JButton(new Command(shell, "Make")         { public void doInvoke() throws Exception { doMake();        } }));
    actions.add(new JButton(new Command(shell, "Poll Changes") { public void doInvoke() throws Exception { doPollChanges(); } }));
    actions.add(new JButton(new Command(shell, "Poll Refresh") { public void doInvoke() throws Exception { doPollRefresh(); } }));
    actions.add(new JButton(new Command(shell, "Dispose")      { public void doInvoke() throws Exception { doDispose();     } }));
        
    add(new JScrollPane(table), BorderLayout.CENTER);          
    add(actions, BorderLayout.SOUTH);          
  }                                   

////////////////////////////////////////////////////////////////
// Events
////////////////////////////////////////////////////////////////

  public void animate()
  {                          
    model.fireTableRowsUpdated(0, rows.length);
  }

  public void doMake() 
    throws Exception
  {   
    MakePrompt p = new MakePrompt();
    p.name = "Watch-" + (char)('A' + (counter++));
    p.pollPeriod = lastPollPeriod;
    p = (MakePrompt)Form.prompt(this, "Make Watch", p);
    if (p == null) return;
    lastPollPeriod = p.pollPeriod;
    
    session.makeWatch(p.name, lastPollPeriod);
    shell.refresh();
  }

  public void doPollChanges()
    throws Exception
  {                              
    int row = table.getSelectedRow();
    if (row < 0) return;
    
    SessionWatch watch = rows[row].watch;
    watch.pollChanges();
  }

  public void doPollRefresh()
    throws Exception
  {                              
    int row = table.getSelectedRow();
    if (row < 0) return;
    
    SessionWatch watch = rows[row].watch;
    watch.pollRefresh();
  }

  public void doDispose()
  {
    int row = table.getSelectedRow();
    if (row < 0) return;
    
    SessionWatch watch = rows[row].watch;
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
//      if (event.getClickCount() == 2)
//      {
//        int row = table.rowAtPoint(event.getPoint());
//        //if (row != -1) 
//        //  hyperlink to view?
//      }
    }
  }

////////////////////////////////////////////////////////////////
// Model
////////////////////////////////////////////////////////////////

  class Model extends AbstractTableModel
  {                 
    public String getColumnName(int col)
    {                                   
      switch(col)
      {
        case 0:  return "Session";
        case 1:  return "Name";
        case 2:  return "Size";     
        case 3:  return "Lease";
        case 4:  return "Poll Period";
        case 5:  return "Last Attempt";
        case 6:  return "Last Success";
        default: return "?";
      }
    }
    public int getColumnCount() { return 7; }
    public int getRowCount() { return rows.length; }
    public Object getValueAt(int row, int col) { return rows[row].get(col); } 
  }                              

////////////////////////////////////////////////////////////////
// Row
////////////////////////////////////////////////////////////////

  static class Row
  {              
    Row(SessionWatch watch)
    {
      this.session = (UiSession)watch.getSession();
      this.watch = watch;
    }
    
    public Object get(int col)
    {
      switch(col)
      {
        case 0:  return session.getAuthority();
        case 1:  return watch.getName();
        case 2:  return ""+watch.size();
        case 3:  return Utils.duration(watch.getLease());
        case 4:  return Utils.duration(watch.getPollPeriod());
        case 5:  return Utils.millisAgo(watch.lastPollAttempt());
        case 6:  return Utils.millisAgo(watch.lastPollSuccess());
        default: return "?";
      }
    }
    
    UiSession session;
    SessionWatch watch;
  }                                            
  
////////////////////////////////////////////////////////////////
// MakePrompt
////////////////////////////////////////////////////////////////

  public static class MakePrompt
  {                 
    public String name;
    public int pollPeriod;
  }
 
////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  static int counter = 0;
  static int lastPollPeriod = 500;
  
  JTable table;
  Model model;
  Row[] rows;
}
