/*
 * This code licensed to public domain
 */
package obix.ui;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * The HighlightedImageFactory converts a color image to one that
 * appears highlighted by applying a red filter to the image.
 *
 * @author    Andy Frank
 * @creation  16 Jul 01
 * @version   $Revision$ $Date$
 */
public class HighlightedImageFactory
  extends RGBImageFilter
{

////////////////////////////////////////////////////////////////
// Factory
////////////////////////////////////////////////////////////////

  public static Icon convert(Icon icon)
  {     
    if (icon instanceof ImageIcon)
    {
      return new ImageIcon(convert(((ImageIcon)icon).getImage()));
    }
    else if (icon instanceof UiSession.RemoteIcon)
    {
      return new ImageIcon(convert(((UiSession.RemoteIcon)icon).image));
    }                     
    else
    {
      return icon;
    }
  }

  public static synchronized Image convert(Image colorImage)
  {
    FilteredImageSource src = new FilteredImageSource(colorImage.getSource(), filter);
    Image dirtyImage = component.createImage(src);
    tracker.addImage(dirtyImage, 0);
    try
    {
      tracker.waitForID(0, 0);
    }
    catch(InterruptedException e)
    {
      System.out.println("DirtyImageFactory.convert -> interrupted");
    }
    tracker.removeImage(dirtyImage, 0);
    return dirtyImage;
  }

  static final HighlightedImageFactory filter = new HighlightedImageFactory();
  static final Component component = new Component() {};
  static final MediaTracker tracker = new MediaTracker(component);

////////////////////////////////////////////////////////////////
// RGBImageFilter
////////////////////////////////////////////////////////////////

  private HighlightedImageFactory()
  {
    canFilterIndexColorModel = false;
  }

  public void setDimensions(int width, int height)
  {
    super.setDimensions(width, height);

    pixels = new int[width][height];
    this.width  = width;
    this.height = height;
  }

  public int filterRGB(int x, int y, int rgb)
  {
    int alpha = (rgb >> 24) & 0xff;

    int red   = (rgb >> 16) & 0xff;
    int green = (rgb >> 8) & 0xff;
    int blue  = rgb & 0xff;

    float brightness = ((red + green + blue) / 3f) / 255f;

    hsb[0] = 0.5f;
    hsb[1] = 1.0f - (1 / brightness); //1.0f;
    hsb[2] = brightness;

    int rgbPost = 0;

    if (alpha == 0x00)
      rgbPost = 0x00ffffff;
    else if (brightness > 0.1)
      rgbPost = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
    else
      rgbPost = 0xffff0000;

    pixels[x][y] = rgbPost;
    return rgbPost;
  }

  protected float[] hsb = new float[3];

  /* the image pixels which have already been processed */
  protected int[][] pixels;

  /* the width of the filtered image */
  protected int width;

  /* the height of the filtered image */
  protected int height;
}