/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.util;

import org.apache.commons.lang3.StringUtils;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.OverloadScanner;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.util.Util;
import umlp.jsweet.extension.guiAdapters.GUIComponentObserverPatternAdapter;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Types;

public class GUIAdapterUtils extends UMLPAdapterUtils {
  public static final String COMPONENT_ANNOTATION = "umlp.jsweet.extension.annotation.Component";

  public static final String DIRECTIVE_ANNOTATION = "umlp.jsweet.extension.annotation.Directive";

  public static final String INPUT_ANNOTATION = "umlp.jsweet.extension.annotation.Input";

  public static final String OUTPUT_ANNOTATION = "umlp.jsweet.extension.annotation.Output";

  public static final String SYNCRHONIZED_ANNOTATION = "umlp.jsweet.extension.annotation.UMLPSynchronized";

  public static final String ERASED_ANNOTATION = "jsweet.lang.Replace";

  public static final String SYNCHRONIZED_SUFFIX = "__synchronized";

  public static final String SYNCHRONIZED_RESULT_SUFFIX = "__SynchronizedResult";

  public static final String UPDATE_SUFFIX = "__update";

  public static final String UMLP_COMMON_POINTER = "@umlp/commonj2ts";

  public static final String STATEVAR_PREFIX = "_";

  public GUIAdapterUtils(PrinterAdapter adapter, Types types, JSweetContext context, Util util) {
    super(adapter, types, context, util);
  }

  public GUIAdapterUtils(Types types, JSweetContext context, Util util) {
    super(null, types, context, util);
  }

  public static String getMethodNameWithoutGetIs(MethodInvocationElement invocation) {
    String methodName = invocation.getMethodName().toString();
    return methodName.startsWith("get") ?
        StringUtils.uncapitalize(methodName.substring(3)) :
        StringUtils.uncapitalize(methodName.substring(2));
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

  public boolean isMethodOfDirective(Element element) {
    return element.getEnclosingElement() instanceof TypeElement &&
        (isDirective((TypeElement) element.getEnclosingElement()) &&
            !isHWCofComponent((TypeElement) element.getEnclosingElement()));
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

  boolean isDirectiveMethod(ExecutableElement executable, TypeElement classElement) {
    return isDirective(classElement) &&
        executable.getKind().toString().equals("METHOD") &&
        !util.isInterface(adapter.getParentElement(TypeElement.class)) &&
        !executable.getModifiers().contains(Modifier.ABSTRACT) &&
        !executable.getModifiers().contains(Modifier.STATIC) &&
        !executable.getModifiers().contains(Modifier.DEFAULT);
  }

  public boolean isSynchronizedMethod(ExecutableElement executable,
      GUIComponentObserverPatternAdapter guiComponentAdapter) {
    return isSynchronized(executable) &&
        !guiComponentAdapter.util().isInterface(guiComponentAdapter.getParentElement(TypeElement.class)) &&
        !executable.getModifiers().contains(Modifier.ABSTRACT) &&
        !executable.getModifiers().contains(Modifier.STATIC) &&
        !executable.getModifiers().contains(Modifier.DEFAULT);
  }

  public <T> T getAnnotationPropertyValue(TypeElement classElement, String annotationProperty,
      Class<T> propertyClass, T defaultValue) {
    if (isComponent(classElement) || isDirective(classElement)) {
      String annotation = isDirective(classElement) ?
          GUIAdapterUtils.DIRECTIVE_ANNOTATION :
          GUIAdapterUtils.COMPONENT_ANNOTATION;
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
    //    Log.error("0JSC002 Cannot find " + annotationProperty + " of classElement " + classElement.getSimpleNameOfFQN().toString());
    return defaultValue;
  }

  public boolean isComponentConstructor(ExecutableElement executable, TypeElement classElement) {
    return
        (isDirective(classElement) || isComponent(classElement) || isHWCofComponent(
            classElement))
            && executable.getKind().toString().equals("CONSTRUCTOR");
  }

  public boolean isInitMethod(ExecutableElement executable, TypeElement classElement) {
    return isDirectiveMethod(executable, classElement) &&
        executable.getSimpleName().toString().equals("init");
  }

  SunTypesHelper sunTypesHelper = new SunTypesHelper();
  public standalone.com.sun.tools.javac.tree.JCTree.JCVariableDecl getNewParameter(String variableName) {
    return sunTypesHelper.getNewParameter(variableName);
  }

  public boolean isStateVarGetter(MethodInvocationElement invocation,
      Element parentElement, GUIComponentObserverPatternAdapter guiComponentObserverPatternAdapter) {
    return invocation.getMethodName().startsWith("get") &&
        guiComponentObserverPatternAdapter.util().getAllMembers((TypeElement) guiComponentObserverPatternAdapter.types().asElement(parentElement.asType())).stream()
            .anyMatch(m -> m.getSimpleName().toString().equalsIgnoreCase(
                STATEVAR_PREFIX + invocation.getMethodName().substring(3))) ||
        invocation.getMethodName().startsWith("is") &&
            guiComponentObserverPatternAdapter.util().getAllMembers((TypeElement) guiComponentObserverPatternAdapter.types().asElement(parentElement.asType())).stream()
                .anyMatch(m -> m.getSimpleName().toString().equalsIgnoreCase(
                    STATEVAR_PREFIX + invocation.getMethodName().substring(2)));
  }
}
