package an.xacml.engine;

import an.xacml.context.Request;
import an.xacml.context.Response;

/**
 * A factory class that used to create Request from context, create response context from Response, and create
 * ContextHandler.
 */
public interface ContextFactory {
    /**
     * Transform the application specific request to an XACML Request.
     * @param request
     * @return
     */
    public Request createRequestFromCtx(Object reqCtx);

    /**
     * Transform the XACML Response to an application specific response.
     * @param response
     * @return
     */
    public Object createResponseCtx(Response response);

    /**
     * Get a ContextHandler object. The ContextHandler controls the engine's process flow.
     * @return
     */
    public ContextHandler getContextHandler();
}