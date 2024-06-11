/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.guiAdapters;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.OverloadScanner;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Types;

public class GUIAdapterUtils {
  public static final String COMPONENT_ANNOTATION = "umlp.jsweet.extension.annotation.Component";

  public static final String DIRECTIVE_ANNOTATION = "umlp.jsweet.extension.annotation.Directive";

  public static final String INPUT_ANNOTATION = "umlp.jsweet.extension.annotation.Input";

  public static final String OUTPUT_ANNOTATION = "umlp.jsweet.extension.annotation.Output";

  public static final String SYNCRHONIZED_ANNOTATION = "umlp.jsweet.extension.annotation.UMLPSynchronized";

  public static final String ERASED_ANNOTATION = "jsweet.lang.Replace";

  public static final String ONINIT_ANNOTATION = "umlp.jsweet.extension.annotation.OnInit";

  public static final String SYNCHRONIZED_SUFFIX = "__synchronized";

  public static final String SYNCHRONIZED_RESULT_SUFFIX = "__SynchronizedResult";

  public static final String INIT_PROMISE_RESULT_SUFFIX = "__InitedResult";

  public static final String INIT_PROMISE_SUFFIX = "__Inited";

  static final String UPDATE_SUFFIX = "__update";

  private final Types types;

  private final JSweetContext context;

  public GUIAdapterUtils(Types types, JSweetContext context) {
    this.types = types;
    this.context = context;
  }

  public boolean isOutput(VariableElement variable) {
    return context.hasAnnotationType(variable, OUTPUT_ANNOTATION);
  }

  public boolean isInput(VariableElement variable) {
    return context.hasAnnotationType(variable, INPUT_ANNOTATION);
  }

  public boolean isComponent(TypeElement type) {
    return context.hasAnnotationType(type, COMPONENT_ANNOTATION);
  }

  public boolean isDirective(TypeElement classElement) {
    return context.hasAnnotationType(classElement, DIRECTIVE_ANNOTATION);
  }

  public boolean isHWCofComponent(TypeElement classElement) {
    if (classElement.getSuperclass() == null || classElement.getSuperclass().toString() == "none") {
      return false;
    }
    Element superType = types.asElement(classElement.getSuperclass());
    return superType.getSimpleName().toString().endsWith("TOP")
        && context.hasAnnotationType(superType, GUIAdapterUtils.DIRECTIVE_ANNOTATION);
  }

  public boolean isTOPComponent(TypeElement classElement) {
    return isDirective(classElement) && !isHWCofComponent(classElement) &&
        classElement.getSimpleName().toString().endsWith("TOP");
  }

  public boolean isMethodOfComponent(Element element) {
    return element.getEnclosingElement() instanceof TypeElement &&
        (isComponent((TypeElement) element.getEnclosingElement()) ||
            isHWCofComponent((TypeElement) element.getEnclosingElement()) ||
            isDirective((TypeElement) element.getEnclosingElement()));
  }

  public boolean isSynchronized(ExecutableElement executable) {
    if (context.hasAnnotationType(executable, SYNCRHONIZED_ANNOTATION)) {
      return true;
    }
    if (context.hasAnnotationType(executable, ERASED_ANNOTATION)) {
      return false;
    }
    OverloadScanner.Overload overload = context.getOverload(
        (TypeElement) executable.getEnclosingElement(), executable);
    if (overload == null || overload.getEntries() == null) {
      return false;
    }

    for (OverloadScanner.OverloadMethodEntry overloadEntry : overload.getEntries()) {
      if (context.hasAnnotationType(overloadEntry.methodElement, SYNCRHONIZED_ANNOTATION)) {
        return true;
      }
    }

    return false;
  }

  public String getOverloadMethodName(ExecutableElement method) {
    if (method.getKind() == ElementKind.CONSTRUCTOR) {
      return "constructor";
    }
    StringBuilder nameBuilder = new StringBuilder(method.getSimpleName().toString());
    nameBuilder.append("$");
    for (VariableElement paramElement : method.getParameters()) {
      nameBuilder.append(
          types.erasure(paramElement.asType()).toString().replace('.', '_').replace("[]", "_A"));
      nameBuilder.append("$");
    }
    if (!method.getParameters().isEmpty()) {
      nameBuilder.deleteCharAt(nameBuilder.length() - 1);
    }
    return nameBuilder.toString();
  }

  public <T> T getAnnotationPropertyValue(TypeElement classElement, String annotationProperty,
      Class<T> propertyClass, T defaultValue) {
    if (isComponent(classElement) || isDirective(classElement)) {
      String annotation = isDirective(classElement) ? GUIAdapterUtils.DIRECTIVE_ANNOTATION : GUIAdapterUtils.COMPONENT_ANNOTATION;
      T value = context.getAnnotationValue(classElement, annotation, annotationProperty,
          propertyClass, null);
      if (value != null) {
        return value;
      }
    }
    if (isHWCofComponent(classElement)) {
      T value = getAnnotationPropertyValue(
          (TypeElement) types.asElement(classElement.getSuperclass()),
          annotationProperty, propertyClass, defaultValue);
      if (value != null) {
        return value;
      }
    }
    //    Log.error("0JSC002 Cannot find " + annotationProperty + " of classElement " + classElement.getSimpleName().toString());
    return defaultValue;
  }

  boolean isDirectiveMethod(ExecutableElement executable, TypeElement classElement,
      GUIComponentAdapter guiComponentAdapter) {
    return isDirective(classElement) &&
        executable.getKind().toString().equals("METHOD") &&
        !guiComponentAdapter.util().isInterface(guiComponentAdapter.getParentElement(TypeElement.class)) &&
        !executable.getModifiers().contains(Modifier.ABSTRACT) &&
        !executable.getModifiers().contains(Modifier.STATIC) &&
        !executable.getModifiers().contains(Modifier.DEFAULT);
  }

  boolean isSynchronizedMethod(ExecutableElement executable,
      GUIComponentAdapter guiComponentAdapter) {
    return isSynchronized(executable) &&
        !guiComponentAdapter.util().isInterface(guiComponentAdapter.getParentElement(TypeElement.class)) &&
        !executable.getModifiers().contains(Modifier.ABSTRACT) &&
        !executable.getModifiers().contains(Modifier.STATIC) &&
        !executable.getModifiers().contains(Modifier.DEFAULT);
  }

  boolean isComponentConstructor(ExecutableElement executable, TypeElement classElement) {
    return
        (isDirective(classElement) || isComponent(classElement) || isHWCofComponent(
            classElement))
            && executable.getKind().toString().equals("CONSTRUCTOR");
  }
}
