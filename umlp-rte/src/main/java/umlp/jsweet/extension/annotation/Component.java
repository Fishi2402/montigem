/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(value={ ElementType.TYPE})
public @interface Component {
  boolean standalone() default true;
  String[] imports() default { "...config.imports" };
  String templateUrl() default "config.templateUrl";
  String[] styleUrls() default { };
  String styles() default "config.styles";
  String selector() default "config.selector";
  String cdName() default "";
  String[] tsImports() default {};
}
