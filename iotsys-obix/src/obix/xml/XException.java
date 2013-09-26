/*
 * This code licensed to public domain
 */
package obix.xml;

/**
 * XException is used to indicate a problem parsing XML.
 *
 * @author    Brian Frank
 * @creation  6 Apr 02
 * @version   $Revision: 2$ $Date: 29-Mar-05 2:16:47 PM$
 */
public class XException
  extends RuntimeException
{   

////////////////////////////////////////////////////////////////
// Constructors
////////////////////////////////////////////////////////////////

  /**
   * Construct using specified line and column.
   */
  public XException(String msg, int line, int column, Throwable cause)
  {
    super(format(msg, line, column));
    this.cause = cause;
  }

  /**
   * Construct using specified line and column.
   */
  public XException(String msg, int line, int column)
  {
    this(msg, line, column, null);
  }

  /**
   * Construct using specified line number.
   */
  public XException(String msg, int line, Throwable cause)
  {
    this(msg, line, 0, cause);
  }

  /**
   * Construct using specified line number.
   */
  public XException(String msg, int line)
  {
    this(msg, line, 0, null);
  }

  /**
   * Construct using current line and column of parser.
   */
  public XException(String msg, XParser parser, Throwable cause)
  {
    this(msg, parser.line(), parser.column(), cause);   
  }

  /**
   * Construct using current line and column of parser.
   */
  public XException(String msg, XParser parser)
  {
    this(msg, parser.line(), parser.column(), null);   
  }

  /**
   * Construct using element's line number.
   */
  public XException(String msg, XElem elem, Throwable cause)
  {
    this(msg, elem.line(), 0, cause);   
    this.elem = elem; 
  }

  /**
   * Construct using element's line number.
   */
  public XException(String msg, XElem elem)
  {
    this(msg, elem.line(), 0, null);  
    this.elem = elem; 
  }

  /**
   * Construct with no line number.
   */
  public XException(String msg, Throwable cause)
  {
    this(msg, 0, 0, cause);   
  }

  /**
   * Construct with no line number.
   */
  public XException(String msg)
  {
    this(msg, 0, 0, null);   
  }

  /**
   * Constructor with cause only.
   */
  public XException(Throwable cause)
  {
    this("", 0, 0, cause);   
  }

  /**
   * Default constructor.
   */
  public XException()
  {
    this("", 0, 0, null);   
  }

////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////

  /**
   * Get the line number of 0 if unknown.
   */
  public int line()
  {
    return line;
  }

  /**
   * Get the column number of 0 if unknown.
   */
  public int column()
  {
    return col;
  }
  
  /**
   * Return the XElem passed to the constructor or null.
   */
  public XElem getElem()
  {                                                     
    return elem;
  }

  /**
   * Get the nested exception for or return null if no 
   * cause exception is provided.
   */
  public Throwable getCause()
  {
    return cause;
  }             
  
  /**
   * Get the standard message format given a text message
   * and a line/column location.  
   *
   * @param msg   base message
   * @param line  0 if unknown
   * @param col   0 if unknown
   */
  public static String format(String msg, int line, int col)
  {
    if (line == 0 && col == 0) return msg;
    if (col == 0) return msg + " [line " + line + ']';
    return msg + " [" + line + ':' + col + ']';
  }

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////  
  
  private int line;
  private int col;
  private Throwable cause; 
  private XElem elem;
  
}
