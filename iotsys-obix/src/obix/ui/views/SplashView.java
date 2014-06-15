/*
 * This code licensed to public domain
 */
package obix.ui.views;  

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.*;

import obix.ui.*;

/**
 * SplashView is displayed on "spy:splash"
 *
 * @author    Brian Frank
 * @creation  13 Sept 05
 * @version   $Revision$ $Date$
 */
public class SplashView
  extends View
{                   
    
////////////////////////////////////////////////////////////////
// Construction 
////////////////////////////////////////////////////////////////

  public SplashView(Shell shell)
  {             
    super(shell, "Splash", null);                     
    
    String javaVer  = 
      System.getProperty("java.vm.name")
      + " " + System.getProperty("java.vm.version")
      + " (" + System.getProperty("java.home") + ")";
        
    JLabel img   = new JLabel(logo, JLabel.CENTER);
    JLabel line1 = new JLabel("Java Obix Toolkit v1.0.0", JLabel.CENTER);
    JLabel line2 = new JLabel("This software is Public Domain - do whatever the heck you want with it!", JLabel.CENTER);
    JLabel line3 = new JLabel("http://sourceforge.net/projects/obix", JLabel.CENTER);
    JLabel line4 = new JLabel(javaVer, JLabel.CENTER);
    
    line1.setFont(new Font("Dialog", Font.BOLD, 16));
    line2.setFont(new Font("Dialog", Font.PLAIN, 12));
    line3.setFont(new Font("Dialog", Font.PLAIN, 12));
    line4.setFont(new Font("Dialog", Font.PLAIN, 12));

    Box box = new Box(BoxLayout.Y_AXIS);
    box.add(new Pane(img, 5));
    box.add(new Pane(line1, 2));
    box.add(new Pane(line2, 2));
    box.add(new Pane(line4, 2));
    add(new Pane(box, 30), BorderLayout.NORTH);          
  }                                   

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  static final ImageIcon logo = Utils.icon("logo.png");
 
  
}
