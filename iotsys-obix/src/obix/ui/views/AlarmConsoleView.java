/*
 * This code licensed to public domain
 */
package obix.ui.views;  
                    
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import obix.*;
import obix.io.*;
import obix.net.*;
import obix.ui.*;

/**
 * AlarmConsoleView
 *
 * @author    Brian Frank
 * @creation  25 May 06
 * @version   $Revision$ $Date$
 */
public class AlarmConsoleView
  extends View
{                   
    
////////////////////////////////////////////////////////////////
// Construction 
////////////////////////////////////////////////////////////////

  public AlarmConsoleView(Shell shell, UiSession.Response resp)
    throws Exception
  {             
    super(shell, "AlarmConsole", resp);                     

    // this table looks like dog-shit, but I don't
    // have time to figure out why Swing doesn't just
    // the right thing out of the box
    table = new JTable(model = new Model());
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    table.addMouseListener(new Controller());
    
    add(new JScrollPane(table), BorderLayout.CENTER);          
    
    // subscribe to the alarm feed    
    Feed feed = (Feed)resp.obj.get("feed");
    feed = (Feed)watch().add(feed.getNormalizedHref());
    
    // populate model with alarms
    Obj[] alarms = feed.list();
    for (int i=0; i<alarms.length; ++i)
      rows.add(new Row(alarms[i]));
  }                                   

////////////////////////////////////////////////////////////////
// Events
////////////////////////////////////////////////////////////////

  public void changed(Obj obj)
  { 
    /*                        
    System.out.println("AlarmConsoleView.changed");   
    obj.dump();      
    */
    
    // assume the only event is from the event feed 
    Feed feed = (Feed)obj;
    Obj[] alarms = feed.list();
    for (int i=0; i<alarms.length; ++i)
      mergeIntoModel(alarms[i]);
    int[] sel = table.getSelectedRows();
    model.fireTableDataChanged();       
    if (sel != null && sel.length > 0)
      table.getSelectionModel().setSelectionInterval(sel[0], sel[sel.length-1]);
  }                     
  
  public void mergeIntoModel(Obj alarm)
  {
    // this is an absolutely awful, non-scalable
    // algorthm, but it's very easy to write and
    // read - basically do a linear search thru
    // the rows and if we have an href match replace
    // that row, otherwise add it as a new row
    Uri href = alarm.getHref();
    for (int i=0; i<rows.size(); ++i)
    {   
      Row row = (Row)rows.get(i);
      Obj existing = row.alarm;
      if (existing.getHref().equals(href))
      {
        row.alarm = alarm;
        return;
      }
    } 
    rows.add(new Row(alarm));
  }

////////////////////////////////////////////////////////////////
// Controller
////////////////////////////////////////////////////////////////

  class Controller extends MouseAdapter
  {
    public void mousePressed(MouseEvent event)
    {              
      if (event.isPopupTrigger())
      {
        popup(event);
        return;
      }
      
      int row = table.rowAtPoint(event.getPoint());
      if (row != -1) 
      {
        Obj alarm = ((Row)rows.get(row)).alarm;
        if (event.getClickCount() == 2)
          doubleClicked(alarm);
      }
    }
    
    public void mouseReleased(MouseEvent event) 
    {
      if (event.isPopupTrigger())
      {
        popup(event);
        return;
      }
    }
  }
  
  void doubleClicked(Obj alarm)
  {                            
    // display XML in dialog box
    String xmlStr = ObixEncoder.toString(alarm);
    JTextArea textArea = new JTextArea(xmlStr, 20, 100);
    JOptionPane.showMessageDialog(this, new JScrollPane(textArea));
  }

  void popup(MouseEvent event)
  {              
    JPopupMenu menu = new JPopupMenu();
    menu.add(new Ack());
    menu.show(event.getComponent(), event.getX(), event.getY());
  }                 
  
////////////////////////////////////////////////////////////////
// Popup:Ack
////////////////////////////////////////////////////////////////

  public class Ack extends Command
  {                       
    Ack() 
    { 
      super(shell, "Ack", null, null); 
    }
    
    public void doInvoke()
      throws Exception
    {                 
      ackSelected();
    }      
                
  }
  void ackSelected()
    throws Exception
  {
    Obj[] alarms = getSelectedAlarms();
    BatchIn batch = session.makeBatch();
    for (int i=0; i<alarms.length; ++i)
    {
      Op ack = (Op)alarms[i].get("ack");
      if (ack != null)
        batch.invoke(ack, null);
    }                
    
    if (batch.size() > 0)
    {
      batch.commit();      
    }
  }

////////////////////////////////////////////////////////////////
// Model
////////////////////////////////////////////////////////////////

  Obj[] getSelectedAlarms()
  {
    int[] rows = table.getSelectedRows();
    Obj[] alarms = new Obj[rows.length];
    for (int i=0; i<rows.length; ++i)
      alarms[i] = ((Row)this.rows.get(rows[i])).alarm;
    return alarms;
  }

  class Model extends AbstractTableModel
  {                 
    public String getColumnName(int col)
    {                                   
      switch(col)
      {
        case 0:  return "Timestamp";
        case 1:  return "Source";
        case 2:  return "Normal Timestamp";     
        case 3:  return "Ack Timestamp";
        case 4:  return "Ack User";
        case 5:  return "Alarm Value";
        case 6:  return "Contracts";
        default: return "?";
      }
    }
    public int getColumnCount() { return 7; }
    public int getRowCount() { return rows.size(); }
    public Object getValueAt(int row, int col) { return ((Row)rows.get(row)).format(col); } 
  }                              

////////////////////////////////////////////////////////////////
// Row
////////////////////////////////////////////////////////////////

  static class Row
  {              
    Row(Obj alarm)
    {
      this.alarm = alarm;
    }

   
     public Object format(int col)
    {
      Object obj = get(col);
      if (obj == null) return "";
      if (obj instanceof Abstime) return ((Abstime)obj).format();
      return obj.toString();
    }
    
    public Object get(int col)
    {
      switch(col)
      {
        case 0:  return alarm.get("timestamp");
        case 1:  return alarm.get("source");
        case 2:  return alarm.get("normalTimestamp");
        case 3:  return alarm.get("ackTimestamp");
        case 4:  return alarm.get("ackUser");
        case 5:  return alarm.get("alarmValue");
        case 6:  return alarm.getIs();
        default: return "?";
      }
    }
    
    Obj alarm;
  }   

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  JTable table;
  Model model;
  ArrayList rows = new ArrayList();
}
