package test_gui;

import jsweet.lang.Decorator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Decorator that declares a DOM event to listen for, and provides a handler method to run when that
 * event occurs.
 * Angular invokes the supplied handler method when the host element emits the specified event, and
 * updates the bound element with the result.
 * If the handler method returns false, applies preventDefault on the bound element.
 */
@Target(value={ ElementType.METHOD})
@umlp.jsweet.extension.annotation.DecoratorNoObj() // Use the HostListener Decorator (and use unnamed args)
@umlp.jsweet.extension.annotation.AnnotationModule("@angular/core") // from which module this should be imported
public @interface HostListener {

  String eventName();

  String[] args() default {};
}
