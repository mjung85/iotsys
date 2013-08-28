/*
 * This code licensed to public domain
 */
package obix.ui.fields;  

import java.awt.*;
import javax.swing.*;
import obix.*;
import obix.ui.*;  

/**
 * SummaryField shows the display string of complex Obj as
 * an atomic field.
 *
 * @author    Brian Frank
 * @creation  19 Apr 06
 * @version   $Revision$ $Date$
 */
public class SummaryField
  extends ObjField
{                   

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////

  public SummaryField()
  {                     
    label.setFont(new JTextField().getFont());           
    setLayout(new BorderLayout());
    add(label, BorderLayout.CENTER); 
  }

////////////////////////////////////////////////////////////////
// Methods
////////////////////////////////////////////////////////////////

  protected void doSetEditable(boolean editable)
  { 
    // always uneditable
  }

  protected void doLoad(Obj val)
  {                       
    label.setText(val.toDisplayString());
  }
  
  protected void doSave(Obj val)
  {         
  }                                  
  
////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  JLabel label = new JLabel("");
 
}