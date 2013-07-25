/*
 * Copyright 2007 Tridium, Inc. All Rights Reserved.
 */
package obix.net;

import java.util.*;

/**
 * The HttpDateFormat class handles generating formatted date header
 * field values from a long or date.  It also handles parsing a
 * string date into a long value representing the date and time.
 * Parsed strings must be in a standard HTTP date and time format
 * using GMT. Date strings are generated in the RFC 1123 format.
 *
 * @author    John Sublett
 * @author    Craig Gemmill
 * @creation  28 Mar 2000
 * @version   $Revision: 1$ $Date: 3/28/2005 10:49:34 AM$
 */
public class HttpDateFormat
{
  /**
   * Format the specified Date into a RFC 1123
   * date string using the specified character
   * to separate the elements of the date.
   */
  public static String format(long dateTime, char dateSep)
  {
    return formatDate(new Date(dateTime), dateSep);
  }

  /**
   * Format the specified date and time into a
   * RFC 1123 date string.
   */
  public static String format(long dateTime)
  {
    return formatDate(new Date(dateTime), ' ');
  }

  /**
   * Format the specified Date into a RFC 1123
   * date string.
   */
  public static String formatDate(Date date)
  {
    return formatDate(date, ' ');
  }

  public static String formatDate(Date date, char dateSep)
  {
    DateTimeInfo dt = new DateTimeInfo();
    synchronized(cal)
    {
      cal.setTime(date);
      dt.dayOfWeek  = cal.get(Calendar.DAY_OF_WEEK);
      dt.dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
      dt.month      = cal.get(Calendar.MONTH);
      dt.year       = cal.get(Calendar.YEAR);
      dt.hour       = cal.get(Calendar.HOUR_OF_DAY);
      dt.minute     = cal.get(Calendar.MINUTE);
      dt.second     = cal.get(Calendar.SECOND);
    }

    return formatDateTimeInfo(dt, dateSep);
  }

  /**
   * Format the specified DateTimeInfo into a string
   * with the RFC 1123 date time format.
   */
  private static String formatDateTimeInfo(DateTimeInfo dt, char dateSep)
  {
    StringBuffer sbuf = new StringBuffer(30);
    sbuf.append(getDayOfWeekString(dt.dayOfWeek));
    sbuf.append(", ");
    if (dt.dayOfMonth < 10)
      sbuf.append('0');
    sbuf.append(dt.dayOfMonth);
    sbuf.append(dateSep);
    sbuf.append(getMonthString(dt.month));
    sbuf.append(dateSep);
    sbuf.append(dt.year).append(' ');

    if (dt.hour < 10)
      sbuf.append('0');
    sbuf.append(dt.hour).append(':');
    if (dt.minute < 10)
      sbuf.append('0');
    sbuf.append(dt.minute).append(':');
    if (dt.second < 10)
      sbuf.append('0');
    sbuf.append(dt.second);

    sbuf.append(" GMT");
    return sbuf.toString();
  }

  /**
   * Get a string for the specified day of the week.
   */
  public static String getDayOfWeekString(int dayOfWeek)
  {
    switch(dayOfWeek)
    {
      case Calendar.SUNDAY   : return "Sun";
      case Calendar.MONDAY   : return "Mon";
      case Calendar.TUESDAY  : return "Tue";
      case Calendar.WEDNESDAY: return "Wed";
      case Calendar.THURSDAY : return "Thu";
      case Calendar.FRIDAY   : return "Fri";
      case Calendar.SATURDAY : return "Sat";
      default:
        throw new IllegalArgumentException("Invalid weekday index: " + dayOfWeek);
    }
  }

  /**
   * Get a string for the specified month.
   */
  public static String getMonthString(int month)
  {
    switch(month)
    {
      case Calendar.JANUARY  : return "Jan";
      case Calendar.FEBRUARY : return "Feb";
      case Calendar.MARCH    : return "Mar";
      case Calendar.APRIL    : return "Apr";
      case Calendar.MAY      : return "May";
      case Calendar.JUNE     : return "Jun";
      case Calendar.JULY     : return "Jul";
      case Calendar.AUGUST   : return "Aug";
      case Calendar.SEPTEMBER: return "Sep";
      case Calendar.OCTOBER  : return "Oct";
      case Calendar.NOVEMBER : return "Nov";
      case Calendar.DECEMBER : return "Dec";
      default:
        throw new IllegalArgumentException("Invalid month index: " + month);
    }
  }

  /**
   * Parse the specified date string and compute
   * the date and time as a long.
   */
  public static long parse(String dateString)
    throws IllegalArgumentException
  {
    try
    {
      DateTimeInfo d = new DateTimeInfo();
      StringTokenizer st = new StringTokenizer(dateString, " ,");
      String token = st.nextToken();

      d.dayOfWeek = getWeekday(token);
      if (d.dayOfWeek != -1)
        token = st.nextToken(" ,-");

      d.dayOfMonth = getDayOfMonth(token);
      boolean asctime = false;
      if (d.dayOfMonth != -1)
        parseDate1OrDate2(d, st);
      else
      {
        asctime = true;
        d.month = getMonth(token);
        if (d.month == -1)
          throw new IllegalArgumentException("Invalid date string: " + dateString);

        parseAsctimeDate(d, st);
      }

      parseTime(d, st);

      if (asctime)
        d.year = Integer.parseInt(st.nextToken());

      long dt;
      synchronized(cal)
      {
        cal.set(d.year, d.month, d.dayOfMonth, d.hour, d.minute, d.second);
        dt = cal.getTime().getTime();
      }

      return dt;
    }
    catch(Exception e)
    {
      throw new IllegalArgumentException("Invalid date string: " + dateString);
    }
  }

  /**
   * Get the weekday index for the specified weekday
   * string.  Valid indexes are Calendar.SUNDAY -
   * Calendar.SATURDAY. -1 is returned if the string
   * is not recognized as a weekday.
   */
  public static int getWeekday(String wdStr)
  {
    int ch = wdStr.charAt(0);
    int ch2;

    switch(ch)
    {
      case 'm':
      case 'M': return Calendar.MONDAY;
      case 't':
      case 'T': ch2 = wdStr.charAt(1);
                if ((ch2 == 'u') || (ch2 == 'U'))
                  return Calendar.TUESDAY;
                else
                  return Calendar.THURSDAY;
      case 'w':
      case 'W': return Calendar.WEDNESDAY;
      case 'f':
      case 'F': return Calendar.FRIDAY;
      case 's':
      case 'S': ch2 = wdStr.charAt(1);
                if ((ch2 == 'u') || (ch2 == 'U'))
                  return Calendar.SUNDAY;
                else
                  return Calendar.SATURDAY;

      default: return -1;
    }
  }

  /**
   * Get an integer value from the specified day of
   * month string.  If the string does not represent
   * a day of the month (i.e. it's not a number) -1
   * is returned.
   */
  public static int getDayOfMonth(String dayStr)
  {
    if (Character.isDigit(dayStr.charAt(0)))
    {
      try
      {
        return Integer.parseInt(dayStr);
      }
      catch(NumberFormatException e)
      {
        return -1;
      }
    }

    return -1;
  }

  /**
   * Get the month index for the specified month
   * string. Valid indexes are Calendar.JANUARY -
   * Calendar.DECEMBER. If the specified string
   * is not recognized as a month, -1 is returned.
   */
  public static int getMonth(String monthStr)
  {
    int ch1, ch2, ch3;

    ch1 = monthStr.charAt(0);
    switch(ch1)
    {
      case 'j':
      case 'J': ch2 = monthStr.charAt(1);
                if ((ch2 == 'a') || (ch2 == 'A'))
                  return Calendar.JANUARY;
                else
                {
                  ch3 = monthStr.charAt(2);
                  if ((ch3 == 'n') || (ch3 == 'N'))
                    return Calendar.JUNE;
                  else
                    return Calendar.JULY;
                }
      case 'f':
      case 'F': return Calendar.FEBRUARY;
      case 'm':
      case 'M': ch2 = monthStr.charAt(2);
                if ((ch2 == 'r') || (ch2 == 'R'))
                  return Calendar.MARCH;
                else
                  return Calendar.MAY;
      case 'a':
      case 'A': ch2 = monthStr.charAt(1);
                if ((ch2 == 'p') || (ch2 == 'P'))
                  return Calendar.APRIL;
                else
                  return Calendar.AUGUST;
      case 's':
      case 'S': return Calendar.SEPTEMBER;
      case 'o':
      case 'O': return Calendar.OCTOBER;
      case 'n':
      case 'N': return Calendar.NOVEMBER;
      case 'd':
      case 'D': return Calendar.DECEMBER;

      default: return -1;
    }
  }

  /**
   * Parse a date in the RFC 1123 or RFC 850 format.
   */
  private static void parseDate1OrDate2(DateTimeInfo d, StringTokenizer st)
  {
    String token = st.nextToken(" -");
    d.month = getMonth(token);
    if (d.month == -1)
      throw new IllegalArgumentException("Invalid month string: " + token);

    token = st.nextToken();
    d.year = Integer.parseInt(token);
    if (d.year < 100)
    {
      if (d.year < 60)
        d.year += 2000;
      else
        d.year += 1900;
    }
  }

  /**
   * Parse a date in the asctime date format.
   */
  private static void parseAsctimeDate(DateTimeInfo d, StringTokenizer st)
  {
    d.dayOfMonth = Integer.parseInt(st.nextToken());
  }

  /**
   * Parse the time.
   */
  private static void parseTime(DateTimeInfo d, StringTokenizer st)
  {
    d.hour = Integer.parseInt(st.nextToken(" :"));
    d.minute = Integer.parseInt(st.nextToken());
    d.second = Integer.parseInt(st.nextToken());
  }

  /**
   * Encapsulates the elements of a date. Used
   * during parsing.
   */
  private static class DateTimeInfo
  {
    public int dayOfWeek  = -1;
    public int dayOfMonth = -1;
    public int month      = -1;
    public int year       = -1;
    public int hour       = -1;
    public int minute     = -1;
    public int second     = -1;

    /**
     * Print the attributes of this date.
     */
    public void printAttributes()
    {
      System.out.println("DateTimeInfo:");
      System.out.println("  dayOfWeek : " + dayOfWeek);
      System.out.println("  dayOfMonth: " + dayOfMonth);
      System.out.println("  month     : " + month);
      System.out.println("  year      : " + year);
      System.out.println("  hour      : " + hour);
      System.out.println("  minute    : " + minute);
      System.out.println("  second    : " + second);
    }
  }

  private static Calendar cal =
    Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.US);

  /*
  public static void main(String[] args)
    throws Exception
  {
    System.out.println(format(System.currentTimeMillis()));
  }
  */
}