/*
 * This code licensed to public domain
 */
package obix.ui.fields;  

import java.awt.*;
import javax.swing.*;
import obix.*;
import obix.ui.*;  

/**
 * RealField
 *
 * @author    Brian Frank
 * @creation  26 Sept 05
 * @version   $Revision$ $Date$
 */
public class RealField
  extends ObjField
{                   

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////

  public RealField()
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
    Real v = (Real)val;   
    textField.setText(String.valueOf(v.get()));
  }
  
  protected void doSave(Obj val)
  {              
    Real v = (Real)val;   
    v.set(Double.parseDouble(textField.getText()));
  }

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  JTextField textField = new JTextField("", 25);
 
}