/*
 * This code licensed to public domain
 */
package obix.ui.views;  

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import obix.io.ObixEncoder;
import obix.ui.*;

/**
 * XmlView shows the ObjEncoder dump of the Obj we read from 
 * the server.  It provides a sanity check via a complete 
 * round-robin decode/re-encode.
 *
 * @author    Brian Frank
 * @creation  13 Sept 05
 * @version   $Revision$ $Date$
 */
public class XmlView
  extends View   
{                   
    
////////////////////////////////////////////////////////////////
// Construction 
////////////////////////////////////////////////////////////////

  public XmlView(Shell shell, UiSession.Response resp)
  {             
    super(shell, "XML", resp);                

    String xmlStr;
    if (resp.obj != null)
      xmlStr = ObixEncoder.toString(resp.obj);
    else    
      xmlStr = Utils.toString(resp.objError);
      
    textArea = new JTextArea(xmlStr);
    add(new JScrollPane(textArea), BorderLayout.CENTER);
  }                  

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////
 
  JTextArea textArea;
  
}
