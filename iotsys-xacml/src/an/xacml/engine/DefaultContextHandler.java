package an.xacml.engine;

import an.log.LogFactory;
import an.log.Logger;
import an.xacml.Constants;
import an.xacml.IndeterminateException;
import an.xacml.context.Request;
import an.xacml.context.Response;
import an.xacml.context.Result;

/**
 * The default implementation of ContextHandler, if "an.xacml.engine.ContextHandler"
 * is not configured, this defult ContextHandler will be used instead.
 */
public class DefaultContextHandler implements ContextHandler {
    private PDP pdp;
    Logger logger = LogFactory.getLogger();

    public DefaultContextHandler(PDP pdp) {
        this.pdp = pdp;
    }

    public Object handle(Object reqCtx) {
        Result evalResult = null;
        ContextFactory ctxFactory = null;

        try {
            CacheManager cacheMgr = CacheManager.getInstance(pdp);
            ctxFactory = ContextFactoryHelper.getContextFactory(pdp);
            Request req = ctxFactory.createRequestFromCtx(reqCtx);

            // Get cached evaluation result.
            evalResult = cacheMgr.getEvalResultByRequest(req);
            // If no cached evaluation result,
            if (evalResult == null) {
                // pass all potential matched policies to PDP for final decision. This method also process 
                // NotApplicable situation.  We don't need perform match operation on each potential matched
                // policy because we will perform "evaluate" operation on them in subsequent call.
                evalResult = pdp.getMultiPoliciesDecision(new EvaluationContext(req), cacheMgr.getPoliciesByRequest(req));
            }
            cacheMgr.addEvalResult(req, evalResult);
        }
        catch (Exception e) {
            logger.error("There are errors occur while handling the request.", e);

            if (!(e instanceof IndeterminateException)) {
                e = new IndeterminateException("Server encounter errors while handling the request", 
                        e, Constants.STATUS_SERVERERROR);
            }
            evalResult = new Result((IndeterminateException)e);
        }

        // ctxFactory won't be null because we have initialize it while starting PDP
        return ctxFactory.createResponseCtx(new Response(new Result[] {evalResult}));
    }
}