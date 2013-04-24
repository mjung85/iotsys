/*
 * This code licensed to public domain
 */
package obix.ui;  

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.Field;

import javax.swing.*;

/**
 * Form is a simple swing utility to display a set of labels
 * and textfields to edit a Java class's public fields using
 * reflection.
 *
 * @author    Brian Frank
 * @creation  13 Sept 05
 * @version   $Revision$ $Date$
 */
public class Form
  extends JPanel
{                  

////////////////////////////////////////////////////////////////
// Prompt
////////////////////////////////////////////////////////////////
  
  /**
   * Prompt the user with a dialog to edit the specified
   * target fields.  Return null if dialog canceled.
   */
  public static Object prompt(JComponent parent, String title, Object target)
  {             
    Form form = new Form(target);                                    
    form.load();
    JOptionPane pane = new JOptionPane(form, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
    JDialog dialog = pane.createDialog(parent, title);
    dialog.pack();
    dialog.setVisible(true);  
    if (pane.getValue() == null || pane.getValue().equals(new Integer(JOptionPane.CANCEL_OPTION))) return null;
    form.save();
    return target;
  }

////////////////////////////////////////////////////////////////
// Construction
////////////////////////////////////////////////////////////////

  public Form(Object target)
  {                          
    try
    {
      this.target = target;
      
      Field[] fields = target.getClass().getFields();     
      editors = new Editor[fields.length];   
      GridBagLayout gb = new GridBagLayout();
      GridBagConstraints gc = new GridBagConstraints();
      setLayout(gb); 
      for(int i=0; i<fields.length; ++i)
      {
        editors[i] = makeEditor(fields[i]);
        
        gc = new GridBagConstraints();
        gc.gridx = 0;  gc.gridy = i;
        gc.insets = new Insets(2, 2, 2, 2);    
        gc.anchor = GridBagConstraints.WEST;
        gc.weightx = 0.0;
        gb.setConstraints(editors[i].label, gc);
        add(editors[i].label);      
        
        gc.gridx = 1;  gc.gridy = i;
        gc.insets = new Insets(2, 2, 2, 2);
        gc.anchor = GridBagConstraints.WEST;
        gc.weightx = 1.0;
        gb.setConstraints(editors[i].editor, gc);
        add(editors[i].editor);
      }
    }
    catch(Exception e)
    {
      throw new RuntimeException(e);
    }
  }

////////////////////////////////////////////////////////////////
// Load
////////////////////////////////////////////////////////////////
  
  /**
   * Load UI from target
   */
  public void load()
  {       
    for(int i=0; i<editors.length; ++i)
    {                    
      Editor editor = editors[i];
      try
      {               
        editor.load(editor.field.get(target));
      }
      catch(Exception e)
      {
        throw new RuntimeException("Cannot save form field: " + editor.name, e);
      }
    }
  }

////////////////////////////////////////////////////////////////
// Save
////////////////////////////////////////////////////////////////
  
  /**
   * Save UI to target
   */
  public void save()
  {
    for(int i=0; i<editors.length; ++i)
    {                    
      Editor editor = editors[i];
      try
      {          
        editor.field.set(target, editor.save());
      }
      catch(Exception e)
      {
        throw new RuntimeException("Cannot save form field: " + editor.name, e);
      }
    }
  }

////////////////////////////////////////////////////////////////
// Editor
////////////////////////////////////////////////////////////////

  static Editor makeEditor(Field field)
  {                          
    Class type  = field.getType();
    if (type == String.class)    return new StringEditor(field);
    if (type == int.class)       return new IntEditor(field);
    if (type == UiSession.class) return new SessionEditor(field);
    throw new IllegalStateException("Field not unsupported: " + type.getName());
  }

  static abstract class Editor
  {                  
    Editor(Field field)
    {     
      this.field = field;     
      this.name  = Utils.toFriendly(field.getName());
      this.label = new JLabel(name, JLabel.LEFT);
    }     
    
    abstract void load(Object val);
    
    abstract Object save();
    
    Field field;  
    String name;
    JLabel label;
    JComponent editor;
  }

////////////////////////////////////////////////////////////////
// StringEditor
////////////////////////////////////////////////////////////////

  static class StringEditor extends Editor
  {
    StringEditor(Field field) 
    { 
      super(field);     
      if (field.getName().equals("password"))
        editor = textField = new JPasswordField("", 30);
      else
        editor = textField = new JTextField("", 30);
    }                
    
    void load(Object v)
    {
      textField.setText(v.toString());
    }                                 
    
    Object save()
    {
      return textField.getText();
    }
    
    JTextField textField;
  }      

////////////////////////////////////////////////////////////////
// IntEditor
////////////////////////////////////////////////////////////////

  static class IntEditor extends Editor
  {
    IntEditor(Field field) 
    { 
      super(field);     
      editor = textField = new JTextField("0", 15);
    }                
    
    void load(Object v)
    {
      textField.setText(v.toString());
    }                                 
    
    Object save()
    {                   
      return new Integer(textField.getText());
    }
    
    JTextField textField;
  }      

////////////////////////////////////////////////////////////////
// SessionEditor
////////////////////////////////////////////////////////////////

  static class SessionEditor extends Editor
  {
    SessionEditor(Field field) 
    { 
      super(field);     
      editor = combo = new JComboBox(UiSession.list());
      combo.setSelectedIndex(0);
    }                
    
    void load(Object v)
    {                             
      if (v != null)
        combo.setSelectedItem(v);
    }                                 
    
    Object save()
    {
      return combo.getSelectedItem();
    }
    
    JComboBox combo;
  }      

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  Object target;  
  Editor[] editors;
  
}
