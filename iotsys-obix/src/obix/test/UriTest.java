/*
 * This code licensed to public domain
 */
package obix.test;

import obix.Uri;

/**
 * UriTest ensures Uri follows URI RFC 2396 and Obix rules
 *
 * @author    Brian Frank
 * @creation  27 Apr 05
 * @version   $Revision$ $Date$
 */
public class UriTest
  extends Test
{ 

////////////////////////////////////////////////////////////////
// Test
////////////////////////////////////////////////////////////////

  public void run()
    throws Exception
  {           
    verifyAbs("http://foo/",     "http",  "foo",     -1, "/");
    verifyAbs("http://foo:81/",  "http",  "foo",     81, "/");
    verifyAbs("https://foo/",    "https", "foo",     -1, "/");
    verifyAbs("http://foo.com/", "http",  "foo.com", -1, "/");
    verifyAbs("http://foo/x",    "http",  "foo",     -1, "/x");

    verifyAbs("http://foo/x#f",   "http", "foo", -1, "/x", null, "f");
    verifyAbs("http://foo/x?q",   "http", "foo", -1, "/x", "q",   null);
    verifyAbs("http://foo/x?q#f", "http", "foo", -1, "/x", "q",  "f");
    verifyAbs("http://foo/x#f?q", "http", "foo", -1, "/x", null, "f?q"); // weird, but I believe correct

    verifyRel("/",      "/",   null,  null);
    verifyRel("/x",     "/x",  null,  null);
    verifyRel("",       "",    null,  null);
    verifyRel("x",      "x",   null,  null);
    verifyRel("/x?q",   "/x",  "q",   null);
    verifyRel("x?q",    "x",   "q",   null);
    verifyRel("?q",     "",    "q",   null);
    verifyRel("#f",     "",    null,  "f");
    verifyRel("/x?q#f", "/x",  "q",   "f");
    verifyRel("x?q#f",  "x",   "q",   "f");
    verifyRel("?q#f",   "",    "q",   "f");

    verifyRel(".",       ".",       null,  null);
    verifyRel("..",      "..",      null,  null);
    verifyRel("../x",    "../x",    null,  null);
    verifyRel("../..",   "../..",   null,  null);
    verifyRel("../../x", "../../x", null,  null);      
    
    verifyNorm("http://foo/",     "/",       "http://foo/");
    verifyNorm("http://foo/",     "/x",      "http://foo/x");
    verifyNorm("http://foo/x",    "/y",      "http://foo/y");
    verifyNorm("http://foo/x",    "y",       "http://foo/y");
    verifyNorm("http://foo/x/",   "y",       "http://foo/x/y");
    verifyNorm("http://foo/x/y",  "/z",      "http://foo/z");
    verifyNorm("http://foo/x/y",  "z",       "http://foo/x/z");
    verifyNorm("http://foo/x/y/", "z",       "http://foo/x/y/z");
    verifyNorm("http://foo/",     "#f",      "http://foo/#f");
    verifyNorm("http://foo/x",    "#f",      "http://foo/x#f");
    verifyNorm("http://foo/x",    "/y#f",    "http://foo/y#f");
    verifyNorm("http://foo/x",    "y#f",     "http://foo/y#f");
    verifyNorm("http://foo/x/",   "y#f",     "http://foo/x/y#f");
    verifyNorm("http://foo/x/",   "..",      "http://foo/");
    verifyNorm("http://foo/x/y",  "..",      "http://foo/");
    verifyNorm("http://foo/x/y/", "..",      "http://foo/x/");
    verifyNorm("http://foo/x/y",  "../z",    "http://foo/z");
    verifyNorm("http://foo/x/y/", "../z",    "http://foo/x/z");
    verifyNorm("http://foo/x/y/", "../..",   "http://foo/");
    verifyNorm("http://foo/x/y/", "../../z", "http://foo/z");      
    verifyNorm("http://foo/",     "obix:obj","obix:obj");
    
    verifyAuth("http://foo/",           "http://foo/");
    verifyAuth("http://foo/file",       "http://foo/");
    verifyAuth("http://foo/bar#frag",   "http://foo/");
    verifyAuth("http://foo/bar?query",  "http://foo/");
    verifyAuth("http://foo:81/",        "http://foo:81/");
    verifyAuth("http://foo:81/x",       "http://foo:81/");
    verifyAuth("http://www.acme.com/x", "http://www.acme.com/");   
    verifyAuth("ftp://www.acme.com/x",  "ftp://www.acme.com/");   
    
    verify(contains("http://foo/",      "http://foo/"));
    verify(!contains("http://foo/",     "http://foox/"));
    verify(contains("http://foo:81/",   "http://foo:81/"));
    verify(!contains("http://foo/",     "http://foo:81/"));
    verify(contains("http://foo/",      "http://foo/x"));
    verify(contains("http://foo/",      "http://foo/x#frag"));
    verify(contains("http://foo/",      "http://foo/x/y.txt"));
    verify(!contains("http://foo:81/",  "http://foox/x"));
    verify(!contains("http://foo/",     "http://foox/x"));
    verify(!contains("http://foo/",     "http://foo:81/x"));
    verify(contains("http://foo/",      ""));
    verify(contains("http://foo/",      ".."));
    verify(contains("http://foo/",      "/"));
    verify(contains("http://foo/",      "/x"));
    verify(contains("http://foo/",      "x"));
    verify(contains("http://foo/",      "x#frag"));     
    
    verifyParent("http://foo/",     null);
    verifyParent("http://foo/x",    "http://foo/");
    verifyParent("http://foo/x/",   "http://foo/");
    verifyParent("http://foo/x/y",  "http://foo/x");
    verifyParent("http://foo/x/y/", "http://foo/x");
    verifyParent("http://foo/x/y/z?query", "http://foo/x/y");
    verifyParent("/foo/x/y/",       "/foo/x");
    verifyParent("/foo",            null);
    
    verifyQuery("k=v", new String[] { "k", "v" });
    verifyQuery("a=b&c=d", new String[] { "a", "b", "c", "d" });
    verifyQuery("foo=bar&darn=cat&rock=roll", new String[] { "foo", "bar", "darn", "cat", "rock", "roll" });
    verifyQuery("v", new String[] { "v", "true" });
    verifyQuery("v=false", new String[] { "v", "false" });
    verifyQuery("a&b=false&c", new String[] { "a", "true", "b", "false", "c", "true" });
    
    verifyAddQuery("http://foo/",             "v", "true", "http://foo/?v=true");
    verifyAddQuery("http://foo/obix",         "v", "true", "http://foo/obix?v=true");
    verifyAddQuery("http://foo/obix?a=b",     "v", "true", "http://foo/obix?a=b&v=true");
    verifyAddQuery("http://foo/obix?a=b&c",   "v", "true", "http://foo/obix?a=b&c=true&v=true");
    verifyAddQuery("http://foo/obix?v=false", "v", "true", "http://foo/obix?v=true");
    verifyAddQuery("http://foo/obix?v",       "v", "foo",  "http://foo/obix?v=foo");
    verifyAddQuery("http://foo/obix?a=b&v=x", "v", "foo",  "http://foo/obix?a=b&v=foo");
    verifyAddQuery("http://foo/obix?v=x&a=b", "v", "foo",  "http://foo/obix?v=foo&a=b");
    
    verifyAddQuery("http://foo/obix",         "v", null,   "http://foo/obix");
    verifyAddQuery("http://foo/obix?v=x",     "v", null,   "http://foo/obix");
    verifyAddQuery("http://foo/obix?a=b",     "v", null,   "http://foo/obix?a=b");
    verifyAddQuery("http://foo/obix?a=b&v=x", "v", null,   "http://foo/obix?a=b");
    verifyAddQuery("http://foo/obix?v=x&a=b", "v", null,   "http://foo/obix?a=b");
  }       

////////////////////////////////////////////////////////////////
// Verify
////////////////////////////////////////////////////////////////

  public void verifyAbs(String uriStr, String scheme, String host, int port, String path)
    throws Exception         
  {                                                                                                                 
    verifyAbs(uriStr, scheme, host, port, path, null, null);
  }
  
  public void verifyAbs(String uriStr, String scheme, String host, int port, String path, String query, String frag)
    throws Exception         
  {             
    Uri uri = new Uri(uriStr);
    
    /*
    System.out.println("test: " + uri); 
    System.out.println("  scheme: " + uri.getScheme());
    System.out.println("  host:   " + uri.getHost());
    System.out.println("  port:   " + uri.getPort());
    System.out.println("  path:   " + uri.getPath());
    System.out.println("  query:  " + uri.getQuery());
    System.out.println("  frag:   " + uri.getFragment());
    */
    
    verify(uri.isAbsolute());
    verify(!uri.isRelative());
    verify(uri.getScheme().equals(scheme));
    verify(uri.getHost().equals(host)); 
    verify(uri.getPort() == port);
    if (port == -1)
      verify(uri.getAddress().equals(host));
    else
      verify(uri.getAddress().equals(host+":"+port));
    verify(uri.getPath().equals(path));
    Uri.Query q = uri.getQuery();
    if (q == null) 
      verify(query == null);
    else
      verify(q.toString(), query);
    verify(uri.getFragment(), frag);
  }

  public void verifyRel(String uriStr, String path, String query, String frag)
    throws Exception         
  {             
    Uri uri = new Uri(uriStr);    
    /*
    System.out.println("test: " + uri); 
    System.out.println("  scheme: " + uri.getScheme());
    System.out.println("  host:   " + uri.getHost());
    System.out.println("  port:   " + uri.getPort());
    System.out.println("  path:   " + uri.getPath());
    System.out.println("  query:  " + uri.getQuery());
    System.out.println("  frag:   " + uri.getFragment());     
    */
    verify(!uri.isAbsolute());
    verify(uri.isRelative());
    verify(uri.getScheme() == null);
    verify(uri.getHost() == null); 
    verify(uri.getPort() == -1);
    verify(uri.getAuthority() == null);
    verify(uri.getPath().equals(path));
    Uri.Query q = uri.getQuery();
    if (q == null) 
      verify(query == null);
    else
      verify(q.toString(), query);
    verify(uri.getFragment(), frag);
  }

  public void verifyNorm(String base, String rel, String norm)
    throws Exception         
  {
    Uri uri = new Uri(rel).normalize(new Uri(base));
    /*
    System.out.println(uri + " ?= " + norm);
    */
    verify(uri.toString().equals(norm));             
  }

  public void verifyAuth(String uriStr, String authStr)
    throws Exception         
  {
    Uri uri = new Uri(uriStr);
    Uri auth = uri.getAuthorityUri();
    /*
    System.out.println(uri + " -> " + auth);                                                          
    */
    verify(uri.getAuthority().equals(authStr));
    verify(auth.toString().equals(authStr));
  }
  
  public boolean contains(String base, String sub)
  {
    return new Uri(base).contains(new Uri(sub));
  }          
  
  public void verifyParent(String uri, String parentStr)
  {                           
    Uri parent = new Uri(uri).parent();     
    /*
    System.out.println(uri + " -> " + parent + "  ?=  " + parentStr);    
    */
    if (parent == null) verify(parentStr == null);
    else verify(parent.toString().equals(parentStr));
  }                                      
  
  public void verifyQuery(String qstr, String[] pairs)
  {
    Uri.Query q = new Uri("http://foo/?" + qstr).getQuery();
    /*
    System.out.println("verifyQuery " + q);
    */
    verify(q.toString().equals(qstr));
    String[] keys = q.keys();
    verify(keys.length == pairs.length/2);                    
    for(int i=0; i<keys.length; ++i)     
    {                      
      String key = keys[i];   
      /*
      System.out.println("  " + key + " = " + q.get(key, null));
      */
      verify(key.equals(pairs[i*2]));
      verify(q.get(key, null).equals(pairs[i*2+1]));
    }
  }              
  
  public void verifyAddQuery(String base, String key, String val, String result)
  {
    Uri actual = new Uri(base).addQueryParam(key, val);
    /*
    System.out.println(actual);    
    */
    verify(actual.toString().equals(result)); 
  }
  
}
