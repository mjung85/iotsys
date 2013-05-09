/*
 * This code licensed to public domain
 */
package obix.net;

import obix.*;

/**
 * WatchListener provides callbacks when watch objects are updated.
 *
 * @author    Brian Frank
 * @creation  30 May 06
 * @version   $Revision$ $Date$
 */
public class BatchIn
  extends List
{            


////////////////////////////////////////////////////////////////
// Constructor
////////////////////////////////////////////////////////////////
  
  /**
   * Package private constructor - see ObixSession.makeBatch()
   */
  BatchIn(ObixSession session)
  {                          
    setOf(uriContract);
    setIs(batchInContract);
    this.session = session;
  }                        
  
////////////////////////////////////////////////////////////////
// Access
////////////////////////////////////////////////////////////////
  
  /**
   * Get associated session for this watch.
   */
  public ObixSession getSession() 
  { 
    return session; 
  }                        

  /**
   * Read the obix document as an Obj instance using the 
   * specified uri relative to the base address of the sesssion.
   */
  public void read(Uri uri)
    throws Exception
  {                
    uri.setIs(readContract);
    add(uri);
  }       
  
  /**
   * Write the specified obj back to the server using the
   * obj's href.  Return a new object containing the
   * server's result.
   * Note that the Obj passed in must not have its 'name'
   * attribute set, as this method must set the name to 'in', as
   * required by the obix:Write contract.
   */
  public void write(Obj obj)
    throws Exception
  {                
    Uri uri = obj.getHref();  
    uri.setIs(writeContract);
    obj.setName("in");
    uri.add(obj);
    add(uri);
  }

  /**
   * Convenience for <code>invoke(op.getHref(), in)</code>.
   */
  public void invoke(Op op, Obj in)
    throws Exception
  {                        
    Uri href = op.getHref();
    if (href == null) throw new Exception("op.href is null");
    invoke(href, in);
  }

  /**
   * Invoke the op at the given href with the given input
   * parameter and return the output parameter.
   * Note that the Obj passed in must not have its 'name'
   * attribute set, as this method must set the name to 'in', as
   * required by the obix:Invoke contract.
   */
  public void invoke(Uri uri, Obj in)
    throws Exception
  {                  
    if (in == null) { in = new Obj(); in.setNull(true); }
    uri.setIs(invokeContract);
    in.setName("in");
    uri.add(in);
    add(uri);
  }              
  
  /**
   * Commit this batch of requests to the server and
   * return the BatchOut response.
   */       
  public List commit()
    throws Exception
  {                         
    /*
    System.out.println("====== BATCH.commit========>");                       
    dump();        
    System.out.println("<====== BATCH.commit========");                       
    */
    return (List)session.invoke(session.batchUri, this);
  }    

////////////////////////////////////////////////////////////////
// Fields
////////////////////////////////////////////////////////////////

  public static final Contract uriContract = new Contract("obix:uri");
  public static final Contract batchInContract = new Contract("obix:BatchIn");
  public static final Contract readContract = new Contract("obix:Read");
  public static final Contract writeContract = new Contract("obix:Write");
  public static final Contract invokeContract = new Contract("obix:Invoke");

  ObixSession session; 
  
} 
