/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(value = { ElementType.METHOD })
public @interface NotifyAboutFieldUpdate {
  String observedField();

  // CBu I do not know why this has to exist
  @Deprecated
  String methodRequiringUpdate() default "";
}
