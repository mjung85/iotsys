/*
 * This code licensed to public domain
 */
package obix.ui.views;  

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import obix.ui.Shell;
import obix.ui.UiSession;
import obix.ui.View;

/**
 * SourceView shows the raw source text received from the server.
 *
 * @author    Brian Frank
 * @creation  13 Sept 05
 * @version   $Revision$ $Date$
 */
public class SourceView
  extends View   
{                   
    
////////////////////////////////////////////////////////////////
// Construction 
////////////////////////////////////////////////////////////////

  public SourceView(Shell shell, UiSession.Response resp)
  {             
    super(shell, "Source", resp);                
    textArea = new JTextArea(resp.source);
    add(new JScrollPane(textArea), BorderLayout.CENTER);
  }                  

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////
 
  JTextArea textArea;
  
}
