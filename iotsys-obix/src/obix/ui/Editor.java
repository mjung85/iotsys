/*
 * This code licensed to public domain
 */
package obix.ui;  

import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import obix.Obj;

/**
 * Editor is the base class for things which load and save
 * an Obj and provide a changed callback.
 *
 * @author    Brian Frank
 * @creation  26 Sept 05
 * @version   $Revision$ $Date$
 */
public abstract class Editor
  extends JPanel
{                 
  
////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////

  public Editor()
  {
    super(new BorderLayout());
  }

////////////////////////////////////////////////////////////////
// Methods
////////////////////////////////////////////////////////////////

  public final boolean isEditable()
  {
    return editable;
  }

  public final void setEditable(boolean editable)
  {
    if (this.editable == editable) return;
    this.editable = editable;
    doSetEditable(editable);
  }

  public final void load(Obj obj)
  {            
    suppressChanged = true;
    try
    {
      doLoad(obj);
    }
    finally
    {
      suppressChanged = false;
    }
  }
  
  public final void save(Obj obj)
    throws Exception
  {             
    doSave(obj);
  }

////////////////////////////////////////////////////////////////
// Overrides
////////////////////////////////////////////////////////////////

  protected abstract void doSetEditable(boolean editable);

  protected abstract void doLoad(Obj obj);
  
  protected abstract void doSave(Obj obj)
    throws Exception;

////////////////////////////////////////////////////////////////
// Listener
////////////////////////////////////////////////////////////////

  public void addListener(Listener listener)
  {
    listeners.add(listener);
  }                         
  
  public void removeListener(Listener listener)
  {
    listeners.remove(listener);
  }                            
  
  public void fireChanged()
  {   
    if (suppressChanged) return;
    Listener[] listeners = (Listener[])this.listeners.toArray(new Listener[this.listeners.size()]);
    for (int i=0; i<listeners.length; ++i)
      listeners[i].changed(this);    
  }

  public static interface Listener
  {
    public void changed(Editor editor);
  }               

////////////////////////////////////////////////////////////////
// Change Adaptors
////////////////////////////////////////////////////////////////

  public void registerForChanged(AbstractButton button)
  {                                                 
    button.addActionListener(new ActionListener()
    {  
      public void actionPerformed(ActionEvent e) { fireChanged(); }
    });
  }

  public void registerForChanged(JTextComponent text)
  {                                                 
    text.getDocument().addDocumentListener(new DocumentListener()
    {  
      public void changedUpdate(DocumentEvent e) { fireChanged(); }
      public void insertUpdate(DocumentEvent e)  { fireChanged(); }
      public void removeUpdate(DocumentEvent e)  { fireChanged(); }
    });
  }

  public void registerForChanged(JComboBox combo)
  {                                             
    registerForChanged((JTextComponent)combo.getEditor().getEditorComponent());
    combo.addItemListener(new ItemListener()
    {  
      public void itemStateChanged(ItemEvent e) { fireChanged(); }
    });
  }    

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////
  
  ArrayList listeners = new ArrayList();
  boolean editable = true;
  boolean suppressChanged = false;
  
}
