package umlp.jsweet.extension.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Marks a method as only executable in the backend.
 * JSweet will replace the method body with an exception.
 * The Annotation jsweet.lang.Replace has precedence.
 * The value dictates the replacement in the fronted.
 */
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
public @interface BackendOnly {
  BackendOnlyModes value();
}
