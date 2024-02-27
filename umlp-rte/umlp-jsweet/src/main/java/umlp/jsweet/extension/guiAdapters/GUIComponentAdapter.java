/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.guiAdapters;

import standalone.com.sun.source.tree.Tree;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.StringUtils;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.Java2TypeScriptTranslator;
import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.OverloadScanner;
import org.jsweet.transpiler.extension.AnnotationManager;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.util.Util;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GUIComponentAdapter extends PrinterAdapter {

  public static final String COMPONENT_ANNOTATION = "umlp.jsweet.extension.annotation.Component";
  public static final String DIRECTIVE_ANNOTATION = "umlp.jsweet.extension.annotation.Directive";
  public static final String INPUT_ANNOTATION = "umlp.jsweet.extension.annotation.Input";
  public static final String OUTPUT_ANNOTATION = "umlp.jsweet.extension.annotation.Output";
  public static final String SYNCRHONIZED_ANNOTATION = "umlp.jsweet.extension.annotation.UMLPSynchronized";
  public static final String ERASED_ANNOTATION = "jsweet.lang.Replace";
  public static final String ONINIT_ANNOTATION = "umlp.jsweet.extension.annotation.OnInit";
  public static final String SYNCHRONIZED_SUFFIX = "_synchronized";

  public static final String SYNCHRONIZED_RESULT_SUFFIX = "_SynchronizedResult";

  public GUIComponentAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    addAnnotationManager(getRenamingUnsynchronizedAnnotationManager());
  }

  @Override
  public boolean substituteType(TypeElement classElement) {
    if (isComponent(classElement)) {
      Boolean standAlone = getAnnotationPropertyValue(classElement, "standalone", Boolean.class, true);
      String[] imports = getAnnotationPropertyValue(classElement, "imports",
          String[].class, new String[] { "...config.imports" });
      String selector = getAnnotationPropertyValue(classElement, "selector", String.class, "config.selector");
      String templateUrl = getAnnotationPropertyValue(classElement, "templateUrl", String.class, "config.templateUrl");
      String styles = getAnnotationPropertyValue(classElement, "styles", String.class, "config.styles");
      String[] styleUrls = getAnnotationPropertyValue(classElement, "styleUrls",
          String[].class, new String[]{ "...config.styleUrls" });
      print("@Component({").println().startIndent().printIndent()
          .print("standalone: " + standAlone + ",").println().printIndent()
          .print("imports: " + Arrays.toString(imports) + ",").println().printIndent()
          .print("selector: " + selector + ",").println().printIndent()
          .print("templateUrl: " + templateUrl + ",").println().printIndent()
          .print("styles: " + styles + ",").println().printIndent()
          .print("styleUrls: " + Arrays.toString(styleUrls)).println().endIndent()
          .print("})").println().printIndent();
      importFrom("Component", "@angular/core");
    }
    else if (isHWCofComponent(classElement)) {
      Log.error("0JSC003 " + classElement.getQualifiedName().toString() + " Handwritten Adaption of a GUI Component must be tagged with @Component()");
    }
    else if (isDirective(classElement)) {
      print("@Directive()").println().printIndent();
      importFrom("Directive", "@angular/core");
    }
    importFrom("ActivatedRoute, Router", "@angular/router");
    return super.substituteType(classElement);
  }

  @Override
  public void beforeTypeBody(TypeElement type) {
    if (isDirective(type)) {
      println().printIndent().print("protected router: Router;").println();
    }
    super.beforeTypeBody(type);
  }

  @Override
  public boolean substituteVariable(VariableElement variable) {
    if (isInput(variable)) {
      importFrom("Input", "@angular/core");
      print("public ").print(variable.getSimpleName()).print("RoutingId : number;")
          .println().println().printIndent();
      print("@Input()").println().printIndent();
    }
    if (isOutput(variable)) {
      importFrom("Output", "@angular/core");
      print("@Output()").println().printIndent();
    }
    return super.substituteVariable(variable);
  }

  @Override
  public boolean substituteExecutable(ExecutableElement executable) {
    TypeElement classElement = getParentElement(TypeElement.class);
    if ((isDirective(classElement) || isComponent(classElement) || isHWCofComponent(classElement))
        && executable.getKind().toString().equals("CONSTRUCTOR")) {
      // handle Constrcuctor
      String cdName = getAnnotationPropertyValue(classElement, "cdName", String.class, "");
      String managerName = cdName + "Manager";
      String pathToManager = "@src/" + StringUtils.uncapitalize(cdName) + "/" + managerName;
      importFrom(managerName, pathToManager);
      printAdaptedConstructor(executable);
      return true;
    }
    else if (isSynchronized(executable) && executable.getKind().toString().equals("METHOD") &&
      !util().isInterface(getParentElement(TypeElement.class)) &&
        !executable.getModifiers().contains(Modifier.ABSTRACT) &&
        !executable.getModifiers().contains(Modifier.STATIC) &&
        !executable.getModifiers().contains(Modifier.DEFAULT)) {
      printSynchronizedMethod(executable, false);
    }
    return super.substituteExecutable(executable);
  }

  private Map<String, Set<OverloadScanner.Overload>> overloads = new HashMap<>();

  @Override
  public boolean substituteOverloadMethodBody(TypeElement parentTypeElement,
      OverloadScanner.Overload overload) {
    if (isSynchronized(overload.getCoreMethodElement())) {
      Set classOverloads = this.overloads.computeIfAbsent(
          parentTypeElement.getQualifiedName().toString(),
          a -> new HashSet<>());
      classOverloads.add(overload);
    }
    return super.substituteOverloadMethodBody(parentTypeElement, overload);
  }

  @Override
  public void afterTypeBody(TypeElement type) {
    if (overloads.containsKey(type.getQualifiedName().toString())) {
      for (OverloadScanner.Overload overload : overloads.get(type.getQualifiedName().toString())) {
        printSynchronizedMethod(overload.getCoreMethodElement(), true);
      }
    }
    super.afterTypeBody(type);
  }

  Set<String> visitedMethods = new HashSet<>();
  private void printSynchronizedMethod(ExecutableElement executable, boolean overloadMethod) {
    // IsList
    boolean isList = false;
    String optionalArrayStart = "";
    String optionalArrayEnd = "";
    String initializer = "null";
    Tree returnType = trees().getTree(executable).getReturnType();
    if (types().isSubtype(executable.getReturnType(), types().erasure(util().getType(List.class)))) {
      isList = true;
      returnType = null;
      optionalArrayStart = "Array<";
      optionalArrayEnd = ">";
      initializer = "[]";
    }

    // MethodName
    String methodName;
    if (!overloadMethod && context.isInvalidOverload(executable) && !util().hasTypeParameters(executable)) {
      methodName = getOverloadMethodName(executable);
    } else {
      methodName = context.getActualName(executable);
    }

    // Duplicated Print
    if (!overloadMethod && visitedMethods.contains(getParentElement().toString() + "." + methodName)) {
      return;
    }
    visitedMethods.add(getParentElement().toString() + "." + methodName);

    // Private fieldName
    String fieldName = methodName + SYNCHRONIZED_RESULT_SUFFIX;
    print("protected " + fieldName + ": ");
    printType(returnType, optionalArrayStart, optionalArrayEnd, overloadMethod)
        .print(" = undefined;").println().printIndent();

    // Method Header
    print("public ").print(methodName + SYNCHRONIZED_SUFFIX).print("(");
    if (executable.getParameters().size() >= 1 && executable.getParameters().get(0) instanceof ExtendedElement) {
      printArgList((List<? extends ExtendedElement>) executable.getParameters());
    } else if (executable.getParameters().size() > 0){
      for (VariableElement parameter : executable.getParameters()) {
        Tree tree = trees().getTree(parameter);
        String paramName = context.getActualName(parameter);
        print(paramName).print(overloadMethod ? "?" : "").print(": ").getPrinter().substituteAndPrintType(tree).print(", ");
        int beginIndex = getPrinter().getOutput().lastIndexOf(SYNCHRONIZED_SUFFIX + "(") +
            (SYNCHRONIZED_SUFFIX + "(").length();
        String currentHeader = getPrinter().getOutput().substring(beginIndex);
        if (currentHeader.indexOf(paramName + ": ") != currentHeader.lastIndexOf(paramName + ": ")) {
          getPrinter().removeLastChars(getPrinter().getOutput().length() - beginIndex);
          print(currentHeader.substring((paramName + ": ").length()));
        }
      }
      getPrinter().removeLastChars(2);
    }
    print("): ");
    printType(returnType, optionalArrayStart, optionalArrayEnd, overloadMethod)
        .print(" {").println().startIndent().printIndent();

    // Method Body
    print("if (this." + fieldName + " === undefined) {").startIndent().println().printIndent();
    print("this." + fieldName + " = " + initializer + ";").println().printIndent();
    print("new Promise<");
    printType(returnType, optionalArrayStart, optionalArrayEnd, overloadMethod);
    print(">(async resolve => {").println().startIndent().printIndent();
    print("resolve(");
    if (isList) {
      importFrom("GemListUtil", "@umlp/commonj2ts");
      print("GemListUtil.toArray(");
    }
    print("(");
    if (context.hasAnnotationType(executable, JSweetConfig.ANNOTATION_ASYNC)) {
      print("await ");
    }
    print("this.").print(executable.getSimpleName()).print("(");
    if (executable.getParameters().size() > 0){
      print(executable.getParameters().stream().map(p -> p.getSimpleName().toString()).collect(
          Collectors.joining(", ")));
    }
    print("))");
    if (isList) {
      //        .printArgList((List<? extends ExtendedElement>) executable.getParameters())
      print(")");
    }
    print(");").println().endIndent().printIndent();
    print("}).then(val => { this." + fieldName + " = val; });").println().endIndent().printIndent();
    print("}").println().printIndent();
    print("return this." + fieldName + ";");
    endIndent().println().printIndent().print("}").println().println().printIndent();
  }

  private String getOverloadMethodName(ExecutableElement method) {
    if (method.getKind() == ElementKind.CONSTRUCTOR) {
      return "constructor";
    }
    StringBuilder nameBuilder = new StringBuilder(method.getSimpleName().toString());
    nameBuilder.append("$");
    for (VariableElement paramElement : method.getParameters()) {
      nameBuilder.append(types().erasure(paramElement.asType()).toString().replace('.', '_').replace("[]", "_A"));
      nameBuilder.append("$");
    }
    if (!method.getParameters().isEmpty()) {
      nameBuilder.deleteCharAt(nameBuilder.length() - 1);
    }
    return nameBuilder.toString();
  }

  private GUIComponentAdapter printType(Tree returnType, String optionalArrayStart, String optionalArrayEnd, boolean overloadMethod) {
    if (overloadMethod) {
      print("any");
      return this;
    } else if (returnType == null) {
      print(optionalArrayStart + "any" + optionalArrayEnd);
      return this;
    } else {
      print(optionalArrayStart).getPrinter().substituteAndPrintType(returnType)
          .print(optionalArrayEnd);
      return this;
    }
  }

  private void printNgOnInit(ExecutableElement executable, String cdName) {
    println().println().printIndent().print("public ngOnInit(): void {").println().startIndent().printIndent();
    for (VariableElement parameter : executable.getParameters()) {
      Name typeSimpleName = Util.getElement(parameter.asType()).getSimpleName();
      print("if (super.").print(parameter.getSimpleName()).print(" == null) {").startIndent()
          .println().printIndent();
      print(cdName + "Manager.get" + typeSimpleName + "Sure(super.")
          .print(parameter.getSimpleName()).print("RoutingId).then(res => { ")
          .startIndent().println().printIndent()
          .print("super.").print(parameter.getSimpleName()).print(" = res;")
          .endIndent().println().printIndent().print("});");
      endIndent().println().printIndent().print("}").println().printIndent();
    }
    println().printIndent().print("this.init();");
    endIndent().println().printIndent().print("}");
  }

  private void printAdaptedConstructor(ExecutableElement executable) {
    TypeElement classElement = getParentElement(TypeElement.class);

    if (isDirective(classElement)) {
      // Print Nothing, probably an error
      return;
    } else if (isComponent(classElement)) {
      print("public constructor(route: ActivatedRoute, protected router: Router) {");
      startIndent();
      Element parentClass = types().asElement(classElement.getSuperclass());
      Optional<? extends Element> parentConstructor = parentClass.getEnclosedElements().stream()
          .filter(s -> s.getKind().toString().equals("CONSTRUCTOR")).findAny();
      if (getAnnotationPropertyValue(classElement, "isEmpty", Boolean.class, false)) {
        println().printIndent().print("super(router)");
      } else if (getAnnotationPropertyValue(classElement, "isCreate", Boolean.class, false)) {
        println().printIndent().print("super(router);");
      } else {
        println().printIndent().print("super(route, router);");
      }
      endIndent().println().printIndent().print("}");
    }
  }

  @Override
  public void afterType(TypeElement type) {
    // Remove standard constructor
    if (isDirective(type) || isComponent(type) || isHWCofComponent(type)) {
      int publicConstructor = getPrinter().getOutput().lastIndexOf("constructor()");
      if (publicConstructor < 0) {
        return;
      }
      int length = getPrinter().getOutput().length();
      getPrinter().removeLastChars(length - publicConstructor);
      println().print("}").println();
      print(type.getSimpleName()).print("[\"__class\"] = \"").print(type.getQualifiedName()).print("\";");
    }
  }

  @Override
  public boolean substituteExtends(TypeElement type) {
    if (isHWCofComponent(type)) {
      String componentName = type.getSimpleName().toString() + "Component";
      print(" extends " + componentName);
      String path = type.getQualifiedName().toString().replace(".", "/");

      importFrom(componentName + ", config", "@src/" + path + "Component");
      importFrom("ActivatedRoute, Router", "@angular/router");
      return true;
    }
    return super.substituteExtends(type);
  }

  @Override
  public Java2TypeScriptTranslator getPrinter() {
    if (super.getPrinter() instanceof Java2TypeScriptTranslator) {
      return (Java2TypeScriptTranslator) super.getPrinter();
    } else {
      Log.error("0JSC001 Java2TypeScriptTranslater must be core Adapter");
      return null;
    }
  }

  private boolean isComponent(TypeElement type) {
    return context.hasAnnotationType(type, COMPONENT_ANNOTATION);
  }

  private boolean isOutput(VariableElement variable) {
    return context.hasAnnotationType(variable, OUTPUT_ANNOTATION);
  }

  private boolean isInput(VariableElement variable) {
    return context.hasAnnotationType(variable, INPUT_ANNOTATION);
  }

  private boolean isDirective(TypeElement classElement) {
    return context.hasAnnotationType(classElement, DIRECTIVE_ANNOTATION);
  }

  private boolean isSynchronized(ExecutableElement executable) {
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

  private boolean isOnInit(ExecutableElement executable) {
    if (!executable.getKind().toString().equals("CONSTRUCTOR")) {
      return false;
    }

    if (context.hasAnnotationType(executable, ONINIT_ANNOTATION)) {
      return true;
    }
    OverloadScanner.Overload overload = context.getOverload(
        (TypeElement) executable.getEnclosingElement(), executable);

    for (OverloadScanner.OverloadMethodEntry overloadEntry : overload.getEntries()) {
      context.hasAnnotationType(overloadEntry.methodElement, ONINIT_ANNOTATION);
    }

    return false;
  }

  private void importFrom(String importedName, String pathToImportedClass) {
    getPrinter().useModule(
        new ModuleImportDescriptor(importedName, pathToImportedClass));
  }

  private <T> T getAnnotationPropertyValue(TypeElement classElement, String annotationProperty,
      Class<T> propertyClass, T defaultValue) {
    if (isComponent(classElement) || isDirective(classElement)) {
      String annotation = isDirective(classElement) ? DIRECTIVE_ANNOTATION : COMPONENT_ANNOTATION;
      T value = context.getAnnotationValue(classElement, annotation, annotationProperty,
          propertyClass, null);
      if (value != null) {
        return value;
      }
    }
    if (isHWCofComponent(classElement)) {
      T value = getAnnotationPropertyValue((TypeElement) types().asElement(classElement.getSuperclass()),
          annotationProperty, propertyClass, defaultValue);
      if (value != null) {
        return value;
      }
    }
//    Log.error("0JSC002 Cannot find " + annotationProperty + " of classElement " + classElement.getSimpleName().toString());
    return defaultValue;
  }

  private boolean isHWCofComponent(TypeElement classElement) {
    if (classElement.getSuperclass() == null || classElement.getSuperclass().toString() == "none") {
      return false;
    }
    Element superType = types().asElement(classElement.getSuperclass());
    return superType.getSimpleName().toString().endsWith("TOP")
         && context.hasAnnotationType(superType, DIRECTIVE_ANNOTATION);
  }

  private AnnotationManager getRenamingUnsynchronizedAnnotationManager() {
    return new AnnotationManager() {
      @Override
      public Action manageAnnotation(Element element, String annotationType) {
        // add the @Name annotation to non-public elements
        return SYNCRHONIZED_ANNOTATION.equals(annotationType)
            && element.getKind().toString().equals("METHOD") &&
            element.getSimpleName().toString().startsWith("get") ?
            Action.ADD : Action.VOID;
      }
    };
  }
}
