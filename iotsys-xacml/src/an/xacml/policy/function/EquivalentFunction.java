package an.xacml.policy.function;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation identifies the annotated method is an equivalent funtion, such as a string-equal function. The
 * purpose of this annotation is to tell indexing mechanism that the Matchable identified by this function can be value
 * indexed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EquivalentFunction {}