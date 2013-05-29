/*
 * This code licensed to public domain
 */
package obix.ui.views;  

import java.awt.BorderLayout;

import javax.swing.*;

import obix.ui.*;

/**
 * ErrorView is what we show when there is a problem showing a uri.
 *
 * @author    Brian Frank
 * @creation  13 Sept 05
 * @version   $Revision$ $Date$
 */
public class ErrorView
  extends View
{                   
    
////////////////////////////////////////////////////////////////
// Construction 
////////////////////////////////////////////////////////////////

  public ErrorView(Shell shell, Throwable problem)
  {             
    super(shell, "Error", null);       
    
    JLabel title = new JLabel("ERROR  " + problem.toString(), icon, JLabel.LEFT);
    JScrollPane details = new JScrollPane(new JTextArea(Utils.toString(problem)));
    
    add(new Pane(title, 10),   BorderLayout.NORTH);          
    add(new Pane(details, 0, 10, 10, 10), BorderLayout.CENTER);          
  }

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  static final ImageIcon icon = Utils.icon("x32/error.png");
 
  
}
