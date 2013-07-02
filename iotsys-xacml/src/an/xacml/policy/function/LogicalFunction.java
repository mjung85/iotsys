package an.xacml.policy.function;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation identifies the annotated method is an logical funtion, such as an "and" function. The purpose of
 * this annotation is to tell Apply or Match element that the parameters passed to this function is no need to be
 * evaluated. The function itself should evaluate it accordingly.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogicalFunction {}