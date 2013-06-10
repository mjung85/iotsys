/*
 * This code licensed to public domain
 */
package obix.ui.fields;  

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;

import obix.Bool;
import obix.Obj;
import obix.ui.ObjField;

/**
 * StatusField              
 *
 *  NOTE:  not currently used
 *
 * @author    Brian Frank
 * @creation  19 Apr 06
 * @version   $Revision$ $Date$
 */
public class StatusField
  extends ObjField
{                   

////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////

  public StatusField()
  {                                    
    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
    for (int i=0; i<checks.length; ++i)
    {
      add(checks[i]); 
      registerForChanged(checks[i]);
    }
  }

////////////////////////////////////////////////////////////////
// Methods
////////////////////////////////////////////////////////////////

  protected void doSetEditable(boolean editable)
  { 
    for (int i=0; i<checks.length; ++i)
      checks[i].setEnabled(editable);
  }

  protected void doLoad(Obj val)
  {                       
    Obj[] kids = val.list();
    for (int i=0; i<kids.length; ++i)
    {                                 
      if (kids[i] instanceof Bool)
      {
        Bool flag = (Bool)kids[i];
        JCheckBox check = lookup(flag.getName());
        if (check != null) check.setSelected(flag.get());
      }
    }
  }
  
  protected void doSave(Obj val)
  {         
    Obj[] kids = val.list();
    for (int i=0; i<kids.length; ++i)
    {                                 
      if (kids[i] instanceof Bool)
      {
        Bool flag = (Bool)kids[i];
        JCheckBox check = lookup(flag.getName());
        if (check != null) flag.set(check.isSelected());
      }
    }
  }                                  
  
  JCheckBox lookup(String name)
  {
    for (int i=0; i<checks.length; ++i)
      if (checks[i].getText().equals(name))
        return checks[i];
    return null;
  }

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  JCheckBox overridden   = new JCheckBox("overridden");
  JCheckBox disabled     = new JCheckBox("disabled");
  JCheckBox fault        = new JCheckBox("fault");
  JCheckBox down         = new JCheckBox("down");
  JCheckBox inAlarm      = new JCheckBox("inAlarm");
  JCheckBox unackedAlarm = new JCheckBox("unackedAlarm");
  JCheckBox historyStart = new JCheckBox("historyStart");
  JCheckBox historyEnd   = new JCheckBox("historyEnd");
  JCheckBox[] checks = new JCheckBox[] { overridden, disabled, 
    fault, down, inAlarm, unackedAlarm, historyStart, historyEnd };
 
}