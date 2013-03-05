/*
 * This code licensed to public domain
 */
package obix.ui.views;  

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import obix.ui.Shell;
import obix.ui.UiSession;
import obix.ui.View;

/**
 * SessionsView lists the "open" sessions at "spy:sessions"
 *
 * @author    Brian Frank
 * @creation  13 Sept 05
 * @version   $Revision$ $Date$
 */
public class SessionsView
  extends View
{                   
    
////////////////////////////////////////////////////////////////
// Construction 
////////////////////////////////////////////////////////////////

  public SessionsView(Shell shell)
  {             
    super(shell, "Sessions", null);                     
    
    UiSession[] sessions = UiSession.list();
    rows = new Row[sessions.length];
    for (int i=0; i<rows.length; ++i)
      rows[i] = new Row(sessions[i]); 
    
    table = new JTable(new Model());
    table.addMouseListener(new Controller());
    
    add(new JScrollPane(table), BorderLayout.CENTER);          
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
        case 0:  return "Authority";
        case 1:  return "Lobby";
        case 2:  return "Username";
        default: return "?";
      }
    }
    public int getColumnCount() { return 3; }
    public int getRowCount() { return rows.length; }
    public Object getValueAt(int row, int col) { return rows[row].get(col); } 
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
        if (row != -1) 
          shell.hyperlink(""+rows[row].session.getLobbyUri());
      }
    }
  }

////////////////////////////////////////////////////////////////
// Row
////////////////////////////////////////////////////////////////

  static class Row
  {              
    Row(UiSession session)
    {
      this.session = session;
    }
    
    public Object get(int col)
    {
      switch(col)
      {
        case 0:  return session.getAuthority();
        case 1:  return session.getLobbyUri();
        case 2:  return session.getUsername();
        default: return "?";
      }
    }
    
    UiSession session;
  }       
 
////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////
  
  JTable table;
  Row[] rows;
}
