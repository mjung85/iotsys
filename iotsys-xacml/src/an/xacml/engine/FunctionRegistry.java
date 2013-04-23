package an.xacml.engine;

import static an.util.PackageUtil.findClassesByPackage;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import an.log.LogFactory;
import an.log.Logger;
import an.xacml.policy.function.BuiltInFunction;
import an.xacml.policy.function.XACMLFunction;
import an.xacml.policy.function.XACMLFunctionProvider;
import an.xml.XMLGeneralException;

/**
 * Before a function could be use, it should be registered using its signature.
 */
public class FunctionRegistry {
    public static String XACML_FUNCTION_PACKAGE = "an.xacml.policy.function";
    private static FunctionRegistry functionReg;
    private Map<URI, BuiltInFunction> functions = new HashMap<URI, BuiltInFunction>();
    private Logger logger;

    private FunctionRegistry() throws IOException, ClassNotFoundException, BuiltInFunctionExistsException {
        logger = LogFactory.getLogger();
        initialize();
    }

    /**
     * Get a FunctionRegistry instance by given PDP.
     * @param pdp
     * @return
     * @throws XMLGeneralException 
     * @throws ClassNotFoundException 
     * @throws IOException 
     * @throws BuiltInFunctionExistsException 
     */
    public static synchronized FunctionRegistry getInstance()
    throws IOException, ClassNotFoundException, BuiltInFunctionExistsException {
        if (functionReg == null) {
            functionReg = new FunctionRegistry();
        }
        return functionReg;
    }

    /**
     * Load all configured functions to the registry using current PDP's configuration.
     * @throws ClassNotFoundException 
     * @throws IOException 
     * @throws BuiltInFunctionExistsException 
     */
    @SuppressWarnings("unchecked")
    protected void initialize() throws IOException, ClassNotFoundException, BuiltInFunctionExistsException {
        Set<Class<?>> providerClasses = new HashSet<Class<?>>();

        findClassesByPackage(XACML_FUNCTION_PACKAGE, true, providerClasses);
        // loop on function provider classes
        for (Class<?> provider : providerClasses) {
            try {
                if (provider.isAnnotationPresent(XACMLFunctionProvider.class)) {
                    Method[] methods = provider.getMethods();
                    // find the matching method on function provider class
                    for (final Method current : methods) {
                        XACMLFunction funcAnn = current.getAnnotation(XACMLFunction.class);
                        if (funcAnn != null) {
                            if (!Modifier.isStatic(current.getModifiers())) {
                                throw new IllegalArgumentException("We expected method '" + provider.getSimpleName() + ":" +
                                        current.getName() + "' is static, but it isn't, we can't load a non-static function.");
                            }

                            String[] funcNames = funcAnn.value();
                            if (funcNames.length == 0) {
                                // If no annotation value provided, we use the method name instead.
                                funcNames = new String[] {current.getName()};
                            }

                            // function attributes map
                            final Map funcAttrs = new HashMap();
                            Annotation[] anns = current.getDeclaredAnnotations();
                            for (Annotation ann : anns) {
                                // We don't need the function id's annotation
                                if (!ann.annotationType().equals(XACMLFunction.class)) {
                                    funcAttrs.put(ann.annotationType(), ann);
                                }
                            }

                            for (String funcName : funcNames) {
                                if (funcName != null && funcName.trim().length() > 0) {
                                    final URI functionId = new URI(funcName);

                                    if (functions.get(functionId) != null) {
                                        throw new BuiltInFunctionExistsException("The built-in function '" + 
                                                functionId + "' has been registered.");
                                    }

                                    BuiltInFunction function = new BuiltInFunction() {
                                        public URI getFunctionId() {
                                            return functionId;
                                        }

                                        public Object invoke(EvaluationContext ctx, Object[] params)
                                            throws Exception {
                                            // All methods that need to be registered to functions should be static 
                                            // method, otherwise, we may not invoke it successfully.
                                            return current.invoke(null, ctx, params);
                                        }

                                        public Object[] getAllAttributes() {
                                            return funcAttrs.values().toArray();
                                        }

                                        public Object getAttribute(Object key) {
                                            return funcAttrs.get(key);
                                        }
                                    };
                                    register(function);
                                }
                            }
                        }
                    }
                }
            }
            catch (BuiltInFunctionExistsException existEx) {
                throw existEx;
            }
            catch (Exception e) {
                logger.error("Error occurs when loading function, will skip current one and continue with next", e);
            }
        }
    }

    public void register(BuiltInFunction function) {
        functions.put(function.getFunctionId(), function);
    }

    public BuiltInFunction unregister(BuiltInFunction function) throws BuiltInFunctionNotFoundException {
        return unregister(function.getFunctionId());
    }

    public BuiltInFunction unregister(URI funcId) throws BuiltInFunctionNotFoundException {
        BuiltInFunction willRemove = lookup(funcId);
        if (willRemove != null) {
            functions.remove(funcId);
        }
        return willRemove;
    }

    protected void unregisterAll() {
        functions.clear();
    }

    public BuiltInFunction lookup(URI funcId) throws BuiltInFunctionNotFoundException {
        BuiltInFunction func = functions.get(funcId);
        if (func == null) {
            throw new BuiltInFunctionNotFoundException("The built-in function " + funcId +
                " could not be found in registry.");
        }
        return func;
    }
}