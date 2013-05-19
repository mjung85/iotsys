/*
 * This code licensed to public domain
 */
package obix.io;

/**
 * BinObix defines the binary byte codes for the oBIX-binary encoding.
 *
 * @author    Brian Frank
 * @creation  1 Sep 09
 * @version   $Revision$ $Date$
 */
public interface BinObix
{             

  // more bit
  public static final int MORE =  0x80;
                      
  // object codes                       
  public static final int OBJ           =  1 << 2;  // no val
  public static final int BOOL          =  2 << 2;  // bool val
  public static final int INT           =  3 << 2;  // int val
  public static final int REAL          =  4 << 2;  // real val
  public static final int STR           =  5 << 2;  // str val
  public static final int ENUM          =  6 << 2;  // str val
  public static final int URI           =  7 << 2;  // str val
  public static final int ABSTIME       =  8 << 2;  // abstime val
  public static final int RELTIME       =  9 << 2;  // reltime val
  public static final int DATE          = 10 << 2;  // date val 
  public static final int TIME          = 11 << 2;  // time val
  public static final int LIST          = 12 << 2;  // no val
  public static final int OP            = 13 << 2;  // no val
  public static final int FEED          = 14 << 2;  // no val
  public static final int REF           = 15 << 2;  // no val
  public static final int ERR           = 16 << 2;  // no val
  public static final int CHILDREN_END  = 17 << 2;  // no val

  // facet/sub-structure codes                       
  public static final int HAS_CHILDREN  =  1 << 2;  // no val
  public static final int NAME          =  2 << 2;  // str val
  public static final int HREF          =  3 << 2;  // str val
  public static final int IS            =  4 << 2;  // str val
  public static final int OF            =  5 << 2;  // str val
  public static final int IN            =  6 << 2;  // str val
  public static final int OUT           =  7 << 2;  // str val
  public static final int NULL          =  8 << 2;  // bool val
  public static final int ICON          =  9 << 2;  // str val
  public static final int DISPLAY_NAME  = 10 << 2;  // str val
  public static final int DISPLAY       = 11 << 2;  // str val
  public static final int WRITABLE      = 12 << 2;  // bool val
  public static final int MIN           = 13 << 2;  // object specific val
  public static final int MAX           = 14 << 2;  // object specific val
  public static final int UNIT          = 15 << 2;  // str val
  public static final int PRECISION     = 16 << 2;  // int val
  public static final int RANGE         = 17 << 2;  // str val
  public static final int TZ            = 18 << 2;  // str val
  public static final int STATUS_0      = 19 << 2;  // status 0 block of values 
  public static final int STATUS_1      = 20 << 2;  // status 1 block of values 
  
  // value codes                       
  public static final int BOOL_FALSE    = 0;  // bool false value
  public static final int BOOL_TRUE     = 1;  // bool true value  
  public static final int INT_U1        = 0;  // unsigned 8-bit int
  public static final int INT_U2        = 1;  // unsigned 16-bit int
  public static final int INT_S4        = 2;  // signed 32-bit int
  public static final int INT_S8        = 3;  // signed 64-bit int
  public static final int REAL_F4       = 0;  // IEEE 32-bit float
  public static final int REAL_F8       = 1;  // IEEE 64-bit float
  public static final int STR_UTF8      = 0;  // null-terminated UTF-8 string
  public static final int STR_PREV      = 1;  // u2 index of previously encoded string
  public static final int ABSTIME_SEC   = 0;  // signed 32-bit int secs since epoch
  public static final int ABSTIME_NS    = 1;  // signed 64-bit int nanoseconds since epoch
  public static final int RELTIME_SEC   = 0;  // signed 32-bit int secs
  public static final int RELTIME_NS    = 1;  // signed 64-bit int nanoseconds
  public static final int TIME_SEC      = 0;  // signed 32-bit int secs since midnight
  public static final int TIME_NS       = 1;  // signed 64-bit int nanoseconds since midnight
  public static final int DATE_YYMD     = 0;  // u2 year, u1 month 1-12, u1 day 1-31
  
  public static final int STATUS_0_DISABLED      = 0;  
  public static final int STATUS_0_FAULT         = 1;  
  public static final int STATUS_0_DOWN          = 2;  
  public static final int STATUS_0_UNACKED_ALARM = 3;  
  public static final int STATUS_1_ALARM         = 0;  
  public static final int STATUS_1_UNACKED       = 1;  
  public static final int STATUS_1_OVERRIDDEN    = 2;  
  
}
