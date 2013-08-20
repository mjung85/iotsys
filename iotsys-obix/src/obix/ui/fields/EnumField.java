/*
 * This code licensed to public domain
 */
package obix.ui.fields;  

import java.awt.*;
import javax.swing.*;
import obix.*;
import obix.Enum;
import obix.ui.*;  

/**
 * EnumField
 *
 * @author    Brian Frank
 * @creation  26 Sept 05
 * @version   $Revision$ $Date$
 */
public class EnumField
  extends ObjField
{                   

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////

  public EnumField()
  {          
    add(combo, BorderLayout.CENTER);
    combo.setEditable(true);   
    registerForChanged(combo);
 }

////////////////////////////////////////////////////////////////
// Methods
////////////////////////////////////////////////////////////////

  protected void doSetEditable(boolean editable)
  { 
    combo.setEnabled(editable);
  }

  protected void doLoad(Obj val)
  {            
    Enum v = (Enum)val;           
    String tag = v.get();              
    combo.getEditor().setItem(tag);
  }
  
  protected void doSave(Obj val)
  {                 
    Enum v = (Enum)val;           
    String tag = (String)combo.getEditor().getItem();
    v.set(tag);
  }

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  JComboBox combo = new JComboBox();   
 
}