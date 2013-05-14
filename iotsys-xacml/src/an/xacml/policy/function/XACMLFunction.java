package an.xacml.policy.function;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation identifies the annotated method is an XACML funtion. The value of this annotation is the corresponding
 * XACML function's id. This annotation allows register 1 java method for multiple XACML functions.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface XACMLFunction {
    String[] value() default {};
}