/*
 * This code licensed to public domain
 */
package obix.test;

import obix.*;
import obix.Enum;
import obix.io.ObixDecoder;

/**
 * FragmentsTest is used to verify that fragment hrefs
 * are immediately resolved during decoding.
 *
 * @author    Brian Frank
 * @creation  21 Sept 05
 * @version   $Revision$ $Date$
 */
public class FragmentsTest
  extends Test
{                    

////////////////////////////////////////////////////////////////
// Main
////////////////////////////////////////////////////////////////  

  public void run()
    throws Exception
  {         
    Obj obj = make( 
      "<obj>\n" +
      
      // references
      "  <str name='a' href='#a' val='cool'/>\n" +
      "  <ref name='ra' href='#a'/>\n" +
      "  <ref name='bad1' href='#notFound'/>\n" +
      "  <ref name='rb1' href='#b'>\n" +
      "    <ref name='rb2' href='#b'/>\n" +
      "  </ref>\n" + 
      "  <str name='b' href='#b' val='cool'/>\n" +
      "  <ref name='bad2' href='#notFound'/>\n" +
      "  <ref name='bad3' href='#anotherNotFound'/>\n" +
      "  <ref name='rb3' href='#b'/>\n" +
      
      // is
      "  <obj name='c1' href='#c1'/>\n" +
      "  <obj name='is1' is='#c1'/>\n" +
      "  <obj name='is2' is='#c1 #c2'/>\n" +
      "  <obj name='is3' is='#c2 /defs/foo'/>\n" +
      "  <list name='list' of='#c2'/>\n" +
      "  <op name='op' in='#c1 #c2' out='#c2'/>\n" +
      "  <feed name='feed' in='#c2' of='#c1 #notFound'/>\n" +
      "  <obj name='c2' href='#c2'/>\n" +
      
      // facets
      "  <obj  name='units' href='#u'/>\n" +
      "  <int  name='i' unit='#u'/>\n" +
      "  <real name='r' unit='#u'/>\n" +
      "  <bool name='z' range='#r'/>\n" +
      "  <enum name='e' range='#r'/>\n" +
      "  <obj  name='range' href='#r'/>\n" +
      "</obj>\n");     
      
    Str a     = (Str)obj.get("a");
    Str b     = (Str)obj.get("b");
    Ref ra    = (Ref)obj.get("ra");
    Ref rb1   = (Ref)obj.get("rb1");
    Ref rb2   = (Ref)rb1.get("rb2");
    Ref rb3   = (Ref)obj.get("rb3");
    Ref bad1  = (Ref)obj.get("bad1");
    Ref bad2  = (Ref)obj.get("bad2");
    Ref bad3  = (Ref)obj.get("bad3");
    Obj c1    = obj.get("c1");
    Obj c2    = obj.get("c2");
    Obj is1   = obj.get("is1");
    Obj is2   = obj.get("is2");
    Obj is3   = obj.get("is3");
    List list = (List)obj.get("list");
    Op op     = (Op)obj.get("op");
    Feed feed = (Feed)obj.get("feed");
    Int i     = (Int)obj.get("i");
    Real r    = (Real)obj.get("r");
    Bool z    = (Bool)obj.get("z");
    Enum e    = (Enum)obj.get("e");
    Obj range = obj.get("range");
    Obj units = obj.get("units");
    
    // check previous frag reference
    verify(ra.getHref().toString().equals("#a"));
    verify(ra.isResolved());
    verify(ra.getResolved() == a);
    
    // check forward frag reference
    verify(rb1.getHref().toString().equals("#b"));
    verify(rb1.isResolved());
    verify(rb1.getResolved() == b);
    verify(rb2.getHref().toString().equals("#b"));
    verify(rb2.isResolved());
    verify(rb2.getResolved() == b);
    
    // check previous for b
    verify(rb3.getHref().toString().equals("#b"));
    verify(rb3.isResolved());
    verify(rb3.getResolved() == b);
    
    // check not found frag reference
    verify(bad1.getHref().toString().equals("#notFound"));
    verify(!bad1.isResolved());
    verify(bad1.getResolved() == null);

    // check not found frag reference
    verify(bad2.getHref().toString().equals("#notFound"));
    verify(!bad2.isResolved());
    verify(bad2.getResolved() == null);

    // check not found frag reference
    verify(bad3.getHref().toString().equals("#anotherNotFound"));
    verify(!bad3.isResolved());
    verify(bad3.getResolved() == null);    
    
    // verify is1
    verify(is1.getIs().size() == 1);
    verify(is1.getIs().get(0).isResolved());
    verify(is1.getIs().get(0).getResolved() == c1);
    
    // verify is2
    verify(is2.getIs().size() == 2);
    verify(is2.getIs().get(0).isResolved());
    verify(is2.getIs().get(0).getResolved() == c1);
    verify(is2.getIs().get(1).isResolved());
    verify(is2.getIs().get(1).getResolved() == c2);
    
    // verify is3
    verify(is3.getIs().size() == 2);
    verify(is3.getIs().get(0).isResolved());
    verify(is3.getIs().get(0).getResolved() == c2);
    verify(!is3.getIs().get(1).isResolved());
    verify(is3.getIs().get(1).toString().equals("/defs/foo"));
    
    // verify list
    verify(list.getOf().size() == 1);
    verify(list.getOf().get(0).isResolved());
    verify(list.getOf().get(0).getResolved() == c2);
    
    // verify op
    verify(op.getIn().size() == 2);
    verify(op.getIn().get(0).getResolved() == c1);
    verify(op.getIn().get(1).getResolved() == c2);
    verify(op.getOut().size() == 1);
    verify(op.getOut().get(0).getResolved() == c2);
    
    // verify feed
    verify(feed.getIn().size() == 1);
    verify(feed.getIn().get(0).getResolved() == c2);
    verify(feed.getOf().size() == 2);
    verify(feed.getOf().get(0).getResolved() == c1);
    verify(!feed.getOf().get(1).isResolved());
    
    // verify facets
    verify(i.getUnit().getResolved() == units);
    verify(r.getUnit().getResolved() == units);
    verify(z.getRange().getResolved() == range);
    verify(e.getRange().getResolved() == range);
  }

////////////////////////////////////////////////////////////////
// Decode
////////////////////////////////////////////////////////////////  

  public Obj make(String xml)
    throws Exception
  {
    return ObixDecoder.fromString(xml);
  }
  
}
