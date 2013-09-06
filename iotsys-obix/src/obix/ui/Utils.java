/*
 * This code licensed to public domain
 */
package obix.ui;  

import java.io.*;
import java.awt.*;
import java.net.*;      
import javax.swing.*;
import javax.swing.plaf.*;

/**
 * Utils make using AWT and Swing slightly less painful.
 *
 * @author    Brian Frank
 * @creation  13 Sept 05
 * @version   $Revision$ $Date$
 */
public class Utils
{               
  
////////////////////////////////////////////////////////////////
// Images
////////////////////////////////////////////////////////////////
  
  /**
   * Load an image file bundled with the jar.
   */
  public static Image img(String filename)
  {                                          
    ImageIcon icon = icon(filename);
    if (icon == null) return null;
    return icon.getImage();
  }
  
  /**
   * Load an image file bundled with the jar.
   */
  public static ImageIcon icon(String filename)
  {                                          
    URL url = Utils.class.getResource("img/" + filename);
    if (url == null)
    {
      System.out.println("WARNING: image not found: " + filename);
      return null;
    }             
    
    return new ImageIcon(url);
  }
  
  /**
   * Sync an image since AWT will async load an image even
   * if given the entire image file as a byte array.
   */
  public static Image sync(Image image)
  {
    // synchronize loading of the image, and block
    // until the image is fully loaded.
    synchronized(tracker)
    {
      try
      {
        tracker.addImage(image, 0);
        tracker.waitForID(0, 0);
        tracker.removeImage(image, 0);
      }
      catch(InterruptedException e)
      {
        throw new IllegalStateException(e.toString());
      }
    }
    return image;
  }
  static MediaTracker tracker = new MediaTracker(new Component() {});

////////////////////////////////////////////////////////////////
// Defaults
////////////////////////////////////////////////////////////////

  public static void dumpDefaults()
  {
    UIDefaults defs = UIManager.getLookAndFeelDefaults();
    String[] keys = (String[])defs.keySet().toArray(new String[defs.size()]);
    for(int i=0; i<keys.length; ++i) 
      if (keys[i].indexOf("font") > 0)
        System.out.println(keys[i] + " = " + defs.get(keys[i]));
  }                  
  
  public static void initDefaults()
  {
    // force text fields and text areas to be fixed width
    UIDefaults defs = UIManager.getLookAndFeelDefaults();
    defs.put("TextField.font",     new FontUIResource("monospaced", Font.PLAIN, 12));     
    defs.put("TextArea.font",      new FontUIResource("monospaced", Font.PLAIN, 12));     
    defs.put("TextPane.font",      new FontUIResource("monospaced", Font.PLAIN, 12));     
    defs.put("ComboBox.font",      new FontUIResource("monospaced", Font.PLAIN, 12));     
    defs.put("PasswordField.font", new FontUIResource("monospaced", Font.PLAIN, 12));     
  }        
  
  public static void main(String[] args)
  {                      
    initDefaults();
    dumpDefaults();
  }

////////////////////////////////////////////////////////////////
// String Utils
////////////////////////////////////////////////////////////////

  /**
   * Translate a programatic name to a friendly
   * name.  This is done based on standard identifier
   * capitalization.  So the string "fooBar" would
   * be translated as "Foo Bar".
   */
  public static String toFriendly(String s)
  {
    StringBuffer buf = new StringBuffer();
    buf.append( Character.toUpperCase(s.charAt(0)) );

    int len = s.length();
    for(int i=1; i<len; ++i)
    {
      char c = s.charAt(i);
      if ((c & 0x20) == 0 && i > 0)
        buf.append(' ').append(c);
      else
        buf.append(c);
    }
    return buf.toString();
  }

  /**
   * Translate a friendly string back into its
   * programatic name.
   */
  public static String fromFriendly(String s)
  {
    StringBuffer buf = new StringBuffer( s.length() );
    buf.append( Character.toLowerCase(s.charAt(0)) );

    int len = s.length();
    int i = 1;
    for(; i<len; ++i)
    {
      char c = s.charAt(i);
      if (c == ' ') break;
      buf.append(c);
    }

    for(; i<len; ++i)
    {
      char c = s.charAt(i);
      if (c != ' ')
        buf.append(c);
    }

    return buf.toString();
  }

  /**
   * Dump the specified exception to a String.
   */
  public static String toString(Throwable ex)
  {
    StringWriter sout = new StringWriter();
    PrintWriter out = new PrintWriter(sout);
    ex.printStackTrace(out);                
    out.flush();
    return sout.toString();
  }                     
  
  /**
   * Get a string description of how long ago the 
   * specified millis occurred.
   */
  public static String millisAgo(long millis)
  {
    if (millis == 0) return "-";
    long ago = System.currentTimeMillis() - millis;
    if (ago > 2000) return ago/1000  + "sec";
    return ago + "ms"; 
  }

  /**
   * Get a string format for duration.
   */
  public static String duration(long millis)
  {                     
    if (millis < 2000) return millis + "ms";
    return millis/1000 + "sec";       
  }
  
  
}
