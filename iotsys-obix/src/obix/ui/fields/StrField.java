/*
 * This code licensed to public domain
 */
package obix.ui.fields;  

import java.awt.*;
import javax.swing.*;
import obix.*;
import obix.ui.*;  

/**
 * StrField
 *
 * @author    Brian Frank
 * @creation  26 Sept 05
 * @version   $Revision$ $Date$
 */
public class StrField
  extends ObjField
{                   

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////

  public StrField()
  {
    add(textField, BorderLayout.CENTER); 
    registerForChanged(textField);
  }

////////////////////////////////////////////////////////////////
// Methods
////////////////////////////////////////////////////////////////

  protected void doSetEditable(boolean editable)
  { 
    textField.setEditable(editable);
  }

  protected void doLoad(Obj val)
  {                       
    Str v = (Str)val;              
    textField.setText(v.get());
  }
  
  protected void doSave(Obj val)
  {         
    Str v = (Str)val;       
    v.set(textField.getText());
  }

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  JTextField textField = new JTextField("", 50);
 
}