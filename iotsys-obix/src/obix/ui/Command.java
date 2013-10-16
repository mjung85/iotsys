/*
 * This code licensed to public domain
 */
package obix.ui;  

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Base class for commands which perform a user action.
 *
 * @author    Brian Frank
 * @creation  13 Sept 05
 * @version   $Revision$ $Date$
 */
public abstract class Command 
  extends AbstractAction
{              
 
////////////////////////////////////////////////////////////////
// Constructors
////////////////////////////////////////////////////////////////

  public Command(Shell shell, String name, String icon, String accelerator)
  {
    super(name);    
    this.shell = shell;     
    this.name  = name;     
    
    if (icon != null)
      putValue(SMALL_ICON, Utils.icon(icon));
                         
    if (accelerator != null)
      putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator));
  }

  public Command(Shell shell, String name)
  {
    this(shell, name, null, null);
  }

////////////////////////////////////////////////////////////////
// Methods
////////////////////////////////////////////////////////////////

  public Shell shell()
  {
    return shell;
  }
      
  public void invoke()
  {
    try
    {            
      shell().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      try
      {                                               
        doInvoke();
      }
      finally
      {
        shell().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
    }
    catch(Throwable e)
    {
      e.printStackTrace();  
      String msg = "Error invoking \"" + name + "\" command (see console for stack trace)\n" + e;
      JOptionPane.showMessageDialog(shell(), msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  public abstract void doInvoke()
    throws Exception;   

  public void actionPerformed(ActionEvent event)
  {
    invoke();
  }                 
  
  public void bind(JComponent c)
  {
    bindings.add(c);
  }                 
  
  public void unbind(JComponent c)
  {
    bindings.remove(c);
  }                    
  
  public JComponent[] bindings()
  {
    return (JComponent[])bindings.toArray(new JComponent[bindings.size()]);
  }
          
////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  private String name;   
  private Shell shell;
  private ArrayList bindings = new ArrayList();
}
