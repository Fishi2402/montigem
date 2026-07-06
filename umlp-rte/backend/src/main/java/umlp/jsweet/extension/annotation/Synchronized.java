/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotation for Java keyword >synchronized<
 */
@Target(value = { ElementType.METHOD })
public @interface Synchronized {
  String key() default "";
}
