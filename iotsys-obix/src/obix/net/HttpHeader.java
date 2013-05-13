/*
 * Copyright 2007 Tridium, Inc. All Rights Reserved.
 */
package obix.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;


/**
 * An HttpHeader is a table of HTTP header fields.
 *
 * @author    John Sublett
 * @author    Craig Gemmill
 * @creation  23 Mar 2000
 * @version   $Revision$Revision$ $Date: 7/9/2003 6:09:42 PM$
 * @since     Niagara 3.0
 */
public class HttpHeader
{
  /**
   * Constructor.
   */
  public HttpHeader()
  {
    table = new HashMap(17);
  }

  public Enumeration getFieldNames()
  {
    Vector names = new Vector(table.size() + 3);
    Iterator i = table.keySet().iterator();
    while (i.hasNext())
    {
      String name = (String)i.next();
      names.add(name);
    }
    
    addFieldNames(names);
    return names.elements();
  }
  
  protected void addFieldNames(Vector names)
  {
  }

  /**
   * Get the value for the specified field.  If the name has multiple values,
   * the first value is returned.  If the name has no value, null is returned.
   */
  public String get(String name)
  {
    return (String)table.get(name.toLowerCase());
  }

  /**
   * Get the integer value for the specified field.  If the field does not
   * exist or is not an integer, def is returned.
   */
  public int getInt(String name, int def)
  {
    try
    {
      String istr = get(name);
      if (istr == null)
        return def;
      else
        return Integer.parseInt(istr);
    }
    catch(NumberFormatException e)
    {
      return def;
    }
  }

  /**
   * Get the date value for the specified field.  If the field does not
   * exist, -1 is returned.  If the value cannot be converted to a date
   * an IllegalArgumentException is thrown.
   */
  public long getDate(String name)
  {
    String val = get(name);
    if (val == null)
      return -1;
    else
      return HttpDateFormat.parse(val);
  }


  /**
   * Get the boolean value for the specified field.  If the field does not
   * exist or is not a boolean, def is returned.
   */
  public boolean getBoolean(String name, boolean def)
  {
    String val = get(name);
    if (val == null)
      return def;
    else
    {
      if (val.equalsIgnoreCase("true"))
        return true;
      else if (val.equalsIgnoreCase("false"))
        return false;
      else
        return def;
    }
  }

  /**
   * Does the header contain the specified field with
   * the specified value?  Both the name and value
   * comparisons are case insensitive.
   */
  public boolean contains(String name, String val)
  {
    String value = get(name);
    if (value == null)
      return false;
    else
      return value.equalsIgnoreCase(val);
  }

  /**
   * Sets the value for the specified header field.
   */
  public void set(String name, String value)
  {
    table.put(name.toLowerCase(), value);
  }

  /**
   * Set the value of the specified field as an int.
   */
  public void setInt(String name, int value)
  {
    set(name, Integer.toString(value));
  }

  /**
   * Set the value of the specified field as a date.
   */
  public void setDate(String name, long value)
  {
    set(name, HttpDateFormat.format(value));
  }

  /**
   * Set the value of the specified field as a boolean.
   */
  public void setBoolean(String name, boolean value)
  {
    set(name, value ? "true" : "false");
  }

  /**
   * Read the header from the specified input stream.
   */
  public void read(InputStream in)
    throws IOException
  {
    StringBuffer sbuf = new StringBuffer(64);
    String name;
    String value;
    int    ch;

    while (true)
    {
      name = null;
      value = null;

      ch = in.read();
      if ((ch == -1) || (ch == Http.CR))
      {
        ch = in.read();
        return;
      }

      while ((ch != -1) && (ch != ':'))
      {
        sbuf.append((char)ch);
        ch = in.read();
      }
      name = sbuf.toString();
      sbuf.setLength(0);

      ch = in.read();
      while ((ch != -1) && (ch == ' '))
        ch = in.read();

      while ((ch != -1) && (ch != Http.CR))
      {
        sbuf.append((char)ch);
        ch = in.read();
      }
      value = sbuf.toString();
      sbuf.setLength(0);
      ch = in.read();

      addField(name, value);
    }
  }
  
  protected void addField(String name, String value)
  {
    table.put(name.toLowerCase(), value);
  }
  
  /**
   * Write the header fields to the specified output.
   */
  public void write(PrintWriter out)
    throws IOException
  {
    write(out, null);
  }
  
  /**
   * Write the header fields to the specified output and to the specified
   * trace buffer.
   *
   * @param out The output target for the header fields.
   * @param trace The trace buffer for the header fields.  This may be
   *   null if no tracing is required.
   */
  public void write(PrintWriter out, StringBuffer trace)
    throws IOException
  {
    // write the normal fields
    Iterator fields = table.keySet().iterator();
    while (fields.hasNext())
    {
      String name = (String)fields.next();
      String value = (String)table.get(name);
      out.print(name);
      out.print(": ");
      out.print(value);
      out.print(Http.CRLF);
      
      if (trace != null)
      {
        trace.append("  ");
        trace.append(name);
        trace.append(": ");
        trace.append(value);
        trace.append(Http.CRLF);
      }
    }
    
    doWriteFields(out, trace);
  }

  /**
   * Let subclasses write any fields that have special handling
   * (like cookies).
   */
  protected void doWriteFields(PrintWriter out, StringBuffer trace)
    throws IOException
  {
  }

  /**
   * Parse a field value.  The result is the input string or an array
   * of strings resulting from a comma-separated input value.
   */
  public static String[] parseValue(String s)
  {
    int index = s.indexOf(',');
    if (index == -1)
      return new String[] {s};
    else
    {
      StringTokenizer st = new StringTokenizer(s, ", ");
      Vector vals = new Vector(2);
      while(st.hasMoreTokens())
        vals.addElement(st.nextToken());

      String[] res = new String[vals.size()];
      vals.copyInto(res);
      return res;
    }
  }

  /**
   * Dump the header as a string.
   */
  public String toString()
  {
    StringBuffer s = new StringBuffer(256);
    Iterator keys = table.keySet().iterator();
    while (keys.hasNext())
    {
      String key = (String)keys.next();
      s.append("  ").append(key).append(": ");
      s.append(table.get(key));
      s.append('\n');
    }
    
    try { doAppendFields(s); } catch(Exception e) {}

    return s.toString();
  }
  
  /**
   * Let subclasses append any fields that require special
   * handling (like cookies).
   */
  protected void doAppendFields(StringBuffer s)
    throws IOException
  {
  }

////////////////////////////////////////////////////////////////
// Attributes
////////////////////////////////////////////////////////////////

  protected HashMap table;
}

