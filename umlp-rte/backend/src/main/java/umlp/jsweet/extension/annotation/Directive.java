/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface Directive {
//  String templateUrl() default "";
//  String styleUrls() default "";
//  String selector() default "";
  String cdName() default "";
  boolean isEmpty() default false;
  boolean isCreate() default false;
}
