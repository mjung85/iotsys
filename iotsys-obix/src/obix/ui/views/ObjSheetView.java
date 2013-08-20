/*
 * This code licensed to public domain
 */
package obix.ui.views;  

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import obix.Obj;
import obix.ui.*;

/**
 * ObjSheetView is the default view for viewing/editing obix objects
 * with a simple property sheet.
 *
 * @author    Brian Frank
 * @creation  13 Sept 05
 * @version   $Revision$ $Date$
 */
public class ObjSheetView
  extends View   
  implements ObjSheet.SaveVisitor 
{                   
    
////////////////////////////////////////////////////////////////
// Construction 
////////////////////////////////////////////////////////////////

  public ObjSheetView(Shell shell, UiSession.Response resp)
    throws Exception
  {             
    super(shell, "Obj", resp);                

    sheet = new ObjSheet(shell, resp.session);
    sheet.load(resp.obj);
    sheet.addListener(new Editor.Listener()
    {
      public void changed(Editor editor) { ObjSheetView.this.changed(); }
    });
    
    add(new JScrollPane(sheet), BorderLayout.CENTER);
  }                  

////////////////////////////////////////////////////////////////
// Save
////////////////////////////////////////////////////////////////

  public void save()
    throws Exception
  {                 
    sheet.save(this);
  }
  
  public void save(Obj obj)
    throws Exception
  {         
    resp.session.write(obj);
  }

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////
 
  ObjSheet sheet;
}
