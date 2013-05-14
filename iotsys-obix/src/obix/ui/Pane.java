/*
 * This code licensed to public domain
 */
package obix.ui;  

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.JPanel;

/**
 * Pane handles some basic stuff that Swing doesn't do 
 * or I just can't figure out how to do easily.
 *
 * @author    Brian Frank
 * @creation  13 Sept 05
 * @version   $Revision$ $Date$
 */
public class Pane
  extends JPanel
{                   
    
////////////////////////////////////////////////////////////////
// Construction 
////////////////////////////////////////////////////////////////

  public Pane()
  {             
    this(0, 0, 0, 0);
  }

  public Pane(int inset)
  {
    this(inset, inset, inset, inset);
  }

  public Pane(int top, int left, int bottom, int right)
  {             
    this(new Insets(top, left, bottom, right));
  }

  public Pane(Insets insets)
  {             
    super(new BorderLayout());
    this.insets = insets;
  }

  public Pane(Component content, int top, int left, int bottom, int right)
  {             
    this(top, left, bottom, right);
    add(content, BorderLayout.CENTER);
  }

  public Pane(Component content, int inset)
  {             
    this(content, inset, inset, inset, inset);
  }

////////////////////////////////////////////////////////////////
// Methods
////////////////////////////////////////////////////////////////

  public Insets getInsets()
  {
    return insets;
  }              
  
  public void setInsets(int inset)
  {
    setInsets(inset, inset, inset, inset);
  }

  public void setInsets(int top, int left, int bottom, int right)
  {
    insets.top    = top;
    insets.left   = left;
    insets.bottom = bottom;
    insets.right  = right;
  }

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////
 
  public Insets insets;

}
