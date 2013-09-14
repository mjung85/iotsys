package an.xacml.engine;

/**
 * This class is used to handle application request, convert it to XACML request, and then send it to PDP for evaluate, 
 * then return response to PEP.
 */
public interface ContextHandler {
    /**
     * The main method of processing the incoming application specific Request. It will create XACML request from
     * application context, and then retrieve corresponding policies, and perform evaluation, at the final, generate
     * application Response and return it.
     */
    public Object handle(Object reqCtx);
}