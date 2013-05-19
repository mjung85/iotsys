/*
 * This code licensed to public domain
 */
package obix.ui;  

import java.awt.event.*;

import javax.swing.JComboBox;

/**
 * Locator is the field showing current URI.
 *
 * @author    Brian Frank
 * @creation  13 Sept 05
 * @version   $Revision$ $Date$
 */
public class Locator
  extends JComboBox 
{               

////////////////////////////////////////////////////////////////
// Construction
////////////////////////////////////////////////////////////////

  public Locator(Shell shell)
  {                      
    this.shell = shell;    
    setEditable(true);   
    getEditor().getEditorComponent().addKeyListener(new KeyAdapter()
      { public void keyPressed(KeyEvent event) { editorKeyPressed(event); } });
    addItemListener(new ItemListener()
      { public void itemStateChanged(ItemEvent event) { itemChanged(event); } });
  }                         

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

  void update(String uri)
  {                       
    locked = true;           
    
    getEditor().setItem(uri);
    
    // make this recent to trim duplicates
    if (getItemCount() > 20) removeItemAt(getItemCount()-1);
    insertItemAt(uri, 0);  
    setSelectedIndex(0);
    
    locked = false;
  }       
 
////////////////////////////////////////////////////////////////
// Events
////////////////////////////////////////////////////////////////

  void editorKeyPressed(KeyEvent event)
  {                         
    if (event.getKeyCode() == KeyEvent.VK_ENTER)
    {
      handleEnter();
      event.consume();
    }
  }                 
  
  void handleEnter()
  {       
    String uri = ""+getEditor().getItem();
    shell.hyperlink(new HyperlinkInfo(uri));
  }                    
  
  void itemChanged(ItemEvent event)
  {                   
    if (locked) return;    
    String uri = ""+getSelectedItem(); 
    if (!uri.equals(shell.uri()))
      shell.hyperlink(uri);
  } 

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  public final Shell shell;
  
  boolean locked;
  
}
