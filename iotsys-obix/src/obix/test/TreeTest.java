/*
 * This code licensed to public domain
 */
package obix.test;

import obix.Obj;

/**
 * TreeTest tests child/parent Obj tree integrity.
 *
 * @author    Brian Frank
 * @creation  27 Apr 05
 * @version   $Revision$ $Date$
 */
public class TreeTest
  extends Test
{ 

////////////////////////////////////////////////////////////////
// Test
////////////////////////////////////////////////////////////////

  public void run()
    throws Exception
  {
    Obj p = new Obj();
    Obj a = new Obj("a");
    Obj b = new Obj("b");
    Obj c = new Obj("c");
    Obj d = new Obj("d");
    Obj x = new Obj();
    Obj y = new Obj();
    Obj z = new Obj();
    
    // empty
    verify(p, new Obj[] {});     
    
    // add all
    p.add(a); verify(p, new Obj[] {a}); 
    p.add(b); verify(p, new Obj[] {a, b}); 
    p.add(c); verify(p, new Obj[] {a, b, c}); 
    p.add(d); verify(p, new Obj[] {a, b, c, d}); 
    
    // remove all
    p.remove(b); verify(p, new Obj[] {a, c, d}); 
    p.remove(a); verify(p, new Obj[] {c, d}); 
    p.remove(d); verify(p, new Obj[] {c}); 
    p.remove(c); verify(p, new Obj[] {}); 
    verify(a.getParent() == null);
    
    // re-add all
    p.add(a); verify(p, new Obj[] {a}); 
    p.add(b); verify(p, new Obj[] {a, b}); 
    p.add(c); verify(p, new Obj[] {a, b, c}); 
    p.add(d); verify(p, new Obj[] {a, b, c, d}); 
    
    // interleave remove/add
    p.remove(a); verify(p, new Obj[] {b, c, d}); 
    p.add(a);    verify(p, new Obj[] {b, c, d, a}); 
    p.remove(c); verify(p, new Obj[] {b, d, a}); 
    p.remove(a); verify(p, new Obj[] {b, d}); 
    p.add(c);    verify(p, new Obj[] {b, d, c}); 
    p.remove(c); verify(p, new Obj[] {b, d}); 
    p.add(a);    verify(p, new Obj[] {b, d, a}); 
    p.remove(d); verify(p, new Obj[] {b, a}); 
    p.remove(b); verify(p, new Obj[] {a}); 
    p.remove(a); verify(p, new Obj[] {});   
    
    // verify unnamed
    p.add(a); verify(p, new Obj[] {a}); 
    p.add(x); verify(p, new Obj[] {a, x}); 
    p.add(b); verify(p, new Obj[] {a, x, b}); 
    p.add(y); verify(p, new Obj[] {a, x, b, y}); 
    p.add(c); verify(p, new Obj[] {a, x, b, y, c}); 
    p.add(z); verify(p, new Obj[] {a, x, b, y, c, z});     
    
    // verify replace
    Obj a0 = new Obj("a");
    Obj a1 = new Obj("a");
    Obj b0 = new Obj("b");
    Obj b1 = new Obj("b");
    Obj c0 = new Obj("c");
    p = new Obj();
    p.add(a0); verify(p, new Obj[] {a0});
    p.replace(a0, a1); verify(p, new Obj[] {a1}); 
    p.replace(a1, a0); p.add(b0); verify(p, new Obj[] {a0, b0});
    p.replace(a0, a1); verify(p, new Obj[] {a1, b0}); 
    p.remove(b0); verify(p, new Obj[] {a1});
    p.remove(a1); verify(p, new Obj[] {});
    p.add(a0); p.add(b0); verify(p, new Obj[] {a0, b0});
    p.replace(b0, b1); verify(p, new Obj[] {a0, b1}); 
    p.add(c0); verify(p, new Obj[] {a0, b1, c0}); 
    p.replace(b1, b0); verify(p, new Obj[] {a0, b0, c0}); 
  }

////////////////////////////////////////////////////////////////
// Util
////////////////////////////////////////////////////////////////

  public void verify(Obj p, Obj[] kids)
    throws Exception
  {                         
    int len = kids.length;
    
    // verify size()                
    verify(p.size() == len);
    
    // verify parent  
    for(int i=0; i<len; ++i)
      verify(kids[i].getParent() == p);    
    
    // verify list() and order
    Obj[] x = p.list();
    verify(x.length == len);
    for(int i=0; i<len; ++i)
      verify(x[i] == kids[i]);
    
    // verify name
    for(int i=0; i<len; ++i)
      if (kids[i].getName() != null)
        verify(p.get(kids[i].getName()) == kids[i]);
  }
  
}
