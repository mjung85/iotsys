/*
 * This code licensed to public domain
 */
package obix.ui;  

import java.util.*;
import obix.*;

/**
 * History stores the history of hyperlinks for back/forward
 *
 * @author    Brian Frank
 * @creation  14 Sept 05
 * @version   $Revision$ $Date$
 */
public class History
{

////////////////////////////////////////////////////////////////
// List
////////////////////////////////////////////////////////////////  

  /**
   * Append the specified viewInfo.
   */
  public Entry append(String uri)
  {
    Entry entry = new Entry(uri);
    
    // remove all the forward entries
    for(int i=current+1; i<count; ++i)
      list[i] = null;
    count = current+1;
    
    // append to the end, shift 
    // history down if we need to
    if (count >= list.length)
    {
      count = list.length;
      System.arraycopy(list, 1, list, 0, count-1);
      list[count-1] = entry;
    }
    else
    {
      list[count++] = entry;
    }
    current = count-1;
    
    recentAdd(entry);
        
    return entry;
  }
    
  /**
   * Get the previous and move back in the list.
   */
  public String back()
  {
    if (current < 1) return null;
    return list[--current].uri;
  }

  /**
   * Get the next and move ahead in the list.
   */
  public String forward()
  {
    if (current >= count-1) return null;
    return list[++current].uri;
  }
  
  /**
   * Is there a view to go back to?
   */
  public boolean isBackEnabled()
  {
    return current >= 1;
  }

  /**
   * Is there a view to go forward to?
   */
  public boolean isForwardEnabled()
  {
    return current < count-1;
  }          

  /**
   * Can we go "up" from current uri.
   */
  public boolean isUpEnabled()
  {                                
    try
    {  
      String cur = getCurrent().uri;
      return new Uri(cur).parent() != null;
    }
    catch(Exception e)
    {
      return false;
    }
  }          
  
//  private void dump(String msg)
//  {
//    System.out.println("History " + msg);
//    for(int i=0; i<count; ++i)
//    {
//      String prefix = current == i ? "->" : "  ";
//      System.out.println(prefix + i + ": " + list[i]);
//    }
//  }

  /**
   * Get the history from current backwards.
   */
  Entry[] getBackHistory()
  {
    Entry[] temp = new Entry[current];
    for (int i=0; i<current; i++)
      temp[current-i-1] = list[i];
    return temp;
  }

  /**
   * Get the history from current forwards.
   */
  Entry[] getForwardHistory()
  {
    Entry[] temp = new Entry[count-current-1];
    for (int i=0; i<count-current-1; i++)
      temp[i] = list[current+i+1];
    return temp;
  }

  /**
   * Get current
   */
  Entry getCurrent()
  {
    return list[current];
  }

////////////////////////////////////////////////////////////////
// Recent
////////////////////////////////////////////////////////////////  

  /**
   * Get the index into the most recent list or -1.
   */
  public int recentIndexOf(String uri)
  {
    for(int i=0; i<recent.size(); ++i)
      if (uri.equals(((Entry)recent.get(i)).uri))
        return i;
    return -1;
  }

  /**
   * Add to the most recent list.
   */
  public void recentAdd(Entry entry)
  {
    int index = recentIndexOf(entry.uri);
    if (index < 0)
    {
      if (recent.size() >= MAX_HISTORY)
        recent.remove(recent.size()-1);
    }
    else
    {
      recent.remove(index);
    }
    recent.add(0, entry);
  }

  /**
   * Touch an ord (which moves it to the top of the 
   * most recent list).
   */
  public void recentTouch(String ord)
  {
    int index = recentIndexOf(ord);
    if (index >= 0)
    {
      Entry entry = (Entry)recent.get(index);
      recent.remove(index);
      recent.add(0, entry);
    }
  }

  /**
   * Get the recent history.
   */
  Entry[] recentList()
  {
    return (Entry[])recent.toArray(new Entry[recent.size()]);
  }                

////////////////////////////////////////////////////////////////
// IO
////////////////////////////////////////////////////////////////
  
  /**
   * Load from file.
   */           
  /*
  public void load()
  {
    try
    {
      File file = new File(home, "recents.xml");
      if (!file.exists()) return;
      
      XElem root = XParser.make(file).parse();
      XElem[] xEntries = root.elems("entry");
      for(int i=0; i<xEntries.length; ++i)
      {
        XElem x = xEntries[i];
        
        String name = x.get("name");  
        String uri = x.get("uri");
        
        recent.add(new Entry(name, uri, icon));
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Save to file.
   */                                 
  /*
  public void save()
  {
    try
    {          
      File file = new File(home, "recent.xml");
      XWriter out = new XWriter(file);
      out.prolog();
      out.w("<recent>\n");
      for(int i=0; i<recent.size(); ++i)
      {
        Entry e = (Entry)recent.get(i);
        out.w("  <entry")
           .attr(" name", e.name)
           .attr(" uri",  e.uri);
        if (e.icon != null) out.attr(" icon", e.icon);
        out.w("/>\n");
      }
      out.w("</recent>\n");
      out.close();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  } 
  */

////////////////////////////////////////////////////////////////
// Entry
////////////////////////////////////////////////////////////////  
  
  static class Entry
  {
    public Entry(String uri)
    {
      this.uri = uri;
    }              
        
    public String uri;
  }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

  public static final int MAX_HISTORY = 50;

  private Entry[] list = new Entry[MAX_HISTORY];
  private int count;
  private int current = -1;
  private ArrayList recent = new ArrayList();
  
}