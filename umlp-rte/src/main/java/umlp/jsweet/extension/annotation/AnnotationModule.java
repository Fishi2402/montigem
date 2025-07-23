/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.annotation;

import java.lang.annotation.*;

/**
 * Like @Module, just for annotations
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE })
@Documented
public @interface AnnotationModule {
  public String value();

}
