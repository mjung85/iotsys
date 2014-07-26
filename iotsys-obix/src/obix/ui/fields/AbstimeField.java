/*
 * This code licensed to public domain
 */
package obix.ui.fields;  

import java.awt.*;
import java.util.*;
import javax.swing.*;
import obix.*;
import obix.ui.*;  

/**
 * AbstimeField
 *
 * @author    Brian Frank
 * @creation  26 Sept 05
 * @version   $Revision$ $Date$
 */
public class AbstimeField
  extends ObjField
{                   

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////

  public AbstimeField()
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
    Abstime v = (Abstime)val;                                 
    if (val.isNull() || v.getMillis() == 0)    
      textField.setText("null");
    else
      textField.setText(v.encodeVal());
  }
  
  protected void doSave(Obj val) 
    throws Exception
  {         
    Abstime v = (Abstime)val; 
    String text = textField.getText();
    if (text.equals("null"))
      v.set(0, TimeZone.getDefault());
    else
      v.decodeVal(text);
  }

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  JTextField textField = new JTextField("", 30);
 
}