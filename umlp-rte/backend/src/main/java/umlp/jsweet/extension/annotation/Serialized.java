/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Serializes the ForAngular update runs of a binding method: updates are
 * chained instead of firing in parallel, additional triggers coalesce.
 */
@Target(value = { ElementType.METHOD })
public @interface Serialized {
}
