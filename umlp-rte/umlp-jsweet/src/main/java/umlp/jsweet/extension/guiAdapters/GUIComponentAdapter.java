/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.guiAdapters;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.StringUtils;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.Java2TypeScriptTranslator;
import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.OverloadScanner;
import org.jsweet.transpiler.extension.AnnotationManager;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.util.Util;
import standalone.com.sun.source.tree.MethodTree;
import standalone.com.sun.source.tree.Tree;
import standalone.com.sun.tools.javac.code.Symbol;
import umlp.jsweet.extension.util.RTEPathUtil;
import umlp.jsweet.extension.util.UMLPAdapterUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GUIComponentAdapter extends PrinterAdapter {

  private static final String OBSERVER_FUNCTION_NAME = "__obs";

  private GUIAdapterUtils guiAdapterUtils;

  public GUIComponentAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    addAnnotationManager(getRenamingUnsynchronizedAnnotationManager());
    this.guiAdapterUtils = new GUIAdapterUtils(types(), context);
  }

  @Override
  public boolean substituteType(TypeElement classElement) {
    if (guiAdapterUtils.isComponent(classElement)) {
      Boolean standAlone = guiAdapterUtils.getAnnotationPropertyValue(classElement, "standalone", Boolean.class,
          true);
      String[] imports = guiAdapterUtils.getAnnotationPropertyValue(classElement, "imports",
          String[].class, new String[] { "...config.imports" });
      String selector = guiAdapterUtils.getAnnotationPropertyValue(classElement, "selector", String.class,
          "config.selector");
      String templateUrl = guiAdapterUtils.getAnnotationPropertyValue(classElement, "templateUrl", String.class,
          "config.templateUrl");
      String styles = guiAdapterUtils.getAnnotationPropertyValue(classElement, "styles", String.class,
          "config.styles");
      String[] styleUrls = guiAdapterUtils.getAnnotationPropertyValue(classElement, "styleUrls",
          String[].class, new String[] { "...config.styleUrls" });
      print("@Component({").println().startIndent().printIndent()
          .print("standalone: " + standAlone + ",").println().printIndent()
          .print("imports: " + Arrays.toString(imports) + ",").println().printIndent()
          .print("selector: " + selector + ",").println().printIndent()
          .print("templateUrl: " + templateUrl + ",").println().printIndent()
          .print("styles: " + styles + ",").println().printIndent()
          .print("styleUrls: " + Arrays.toString(styleUrls)).println().endIndent()
          .print("})").println().printIndent();
      importFromWithModule("Component", "@angular/core");
    }
    else if (guiAdapterUtils.isHWCofComponent(classElement)) {
      Log.error("0JSC003 " + classElement.getQualifiedName().toString()
          + " Handwritten Adaption of a GUI Component must be tagged with @Component()");
    }
    else if (guiAdapterUtils.isDirective(classElement)) {
      print("@Directive()").println().printIndent();
      importFromWithModule("Directive", "@angular/core");
    }
    if (guiAdapterUtils.isDirective(classElement) || guiAdapterUtils.isComponent(classElement)) {
      importFromWithModule("ActivatedRoute, Router", "@angular/router");
      importFromWithModule("ComponentObserverManager", "@umlp/commonj2ts");
    }
    return super.substituteType(classElement);
  }

  @Override
  public boolean substituteImplements(TypeElement type) {
    boolean res = super.substituteImplements(type);

    if ((guiAdapterUtils.isComponent(type))) {
      importFromWithModule("OnDestroy", "@angular/core");
      if (util().getInterfaces(type).isEmpty()) {
        print(" implements OnDestroy");
      } else {
        print(", OnDestroy");
      }
    }

    return res;
  }

  @Override
  public void beforeTypeBody(TypeElement type) {
    if (guiAdapterUtils.isDirective(type)) {
      println().printIndent().print("protected router: Router;").println().println();
      printWaitAllInited(type);
    }
    if (guiAdapterUtils.isComponent(type) ||  guiAdapterUtils.isDirective(type) && !guiAdapterUtils.isTOPComponent(type)) {
      printNgOnDestroy(type);
    }
    super.beforeTypeBody(type);
  }

  private void printNgOnDestroy(TypeElement typeElement) {
    String guiComponentName = "\"" + typeElement.getQualifiedName().toString() + "\"";
    if (guiComponentName.endsWith("TOP\"") && guiAdapterUtils.isDirective(typeElement)) {
      guiComponentName = guiComponentName.substring(0, guiComponentName.length() - 4) + "\"";
    }
    printIndent().print("public ngOnDestroy(): void {").println().startIndent().printIndent()
        .print("ComponentObserverManager.removeObservers(" + guiComponentName + ");")
        .println().endIndent().printIndent().print("}")
        .println().println();
  }

  @Override
  public boolean substituteVariable(VariableElement variable) {
    Element enclosingElement = variable.getEnclosingElement();
    if (enclosingElement != null && (enclosingElement instanceof TypeElement) &&
        guiAdapterUtils.isHWCofComponent((TypeElement) enclosingElement)) {
      return super.substituteVariable(variable);
    }
    if (enclosingElement == null || !(enclosingElement instanceof TypeElement) ||
        !guiAdapterUtils.isDirective((TypeElement) enclosingElement)) {
      return super.substituteVariable(variable);
    }

    // Wait Init
    String variableName = variable.getSimpleName().toString();
    print("private async wait" + variableName + "__inited(): Promise<void> {")
        .println().startIndent().printIndent()
        .print("for (let counter = 0; counter < 100 && this." + variableName
            + " == null; counter++) {")
        .println().startIndent().printIndent()
        .print("await new Promise(r => setTimeout(r, 100));")
        .println().endIndent().printIndent().print("}").println().printIndent()
        .print("if (this." + variableName + " == null) {")
        .println().startIndent().printIndent()
        .print("console.error('" + variableName
            + " did not return a result after waiting for 10 seconds');")
        .println().endIndent().printIndent().print("}")
        .println().endIndent().printIndent().print("}")
        .println().printIndent();

    // Below is depricated, might be important in the future
    if (guiAdapterUtils.isInput(variable)) {
      importFromWithModule("Input", "@angular/core");
      print("public ").print(variable.getSimpleName()).print("RoutingId : number;")
          .println().println().printIndent();
      print("@Input()").println().printIndent();
    }
    if (guiAdapterUtils.isOutput(variable)) {
      importFromWithModule("Output", "@angular/core");
      print("@Output()").println().printIndent();
    }
    return super.substituteVariable(variable);
  }

  @Override
  public boolean substituteExecutable(ExecutableElement executable) {
    TypeElement classElement = getParentElement(TypeElement.class);
    if (guiAdapterUtils.isComponentConstructor(executable, classElement)) {
      // handle Constrcuctor
      String cdName = guiAdapterUtils.getAnnotationPropertyValue(classElement, "cdName", String.class, "");
      String managerName = cdName + "Manager";
      String pathToManager = "@src/" + StringUtils.lowerCase(cdName) + "/" + managerName;
      importFromWithModule(managerName, pathToManager);
      printAdaptedConstructor(executable);
      return true;
    }
    else if (guiAdapterUtils.isSynchronizedMethod(executable, this) &&
        !context.hasAnnotationType(executable, Override.class.getName())) {
      //      printSynchronizedMethod(executable, false);
      printMyNewSynchronizedMethod(executable, false);
    }
    if (guiAdapterUtils.isDirectiveMethod(executable, classElement, this)
    ) {
      if (executable.getSimpleName().contentEquals("init")) {
      }
      else {
        //        printInitPromiseMethod(executable, false);
      }
    }
    return super.substituteExecutable(executable);
  }

  private void printWaitAllInited(TypeElement classElement) {
    printIndent().print("protected allInitedPromise: Promise<void>;").println().printIndent();
    print("protected waitAllInited(): Promise<void> {").println().startIndent().printIndent()
        .print("if (this.allInitedPromise === undefined) {").println().startIndent().printIndent()
        .print("this.allInitedPromise = Promise.resolve()");
    for (Element enclosedElement : classElement.getEnclosedElements()) {
      if (enclosedElement instanceof VariableElement) {
        print(".then(() => this.wait" + enclosedElement.getSimpleName().toString() + "__inited())");
      }
    }
    print(";").println().endIndent().printIndent().print("}").println().printIndent()
        .print("return this.allInitedPromise;").println().endIndent().printIndent()
        .print("}").println().println().printIndent();
  }

  private Map<String, Set<OverloadScanner.Overload>> overloads = new HashMap<>();

  @Override
  public boolean substituteOverloadMethodBody(TypeElement parentTypeElement,
      OverloadScanner.Overload overload) {
    if (guiAdapterUtils.isSynchronized(overload.getCoreMethodElement())) {
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
        //        printSynchronizedMethod(overload.getCoreMethodElement(), true);
      }
    }
    if (guiAdapterUtils.isDirective(type) && overloads.containsKey(type.getQualifiedName().toString())) {
      for (OverloadScanner.Overload overload : overloads.get(type.getQualifiedName().toString())) {
        //        printInitPromiseMethod(overload.getCoreMethodElement(), true);
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
    if (types().isSubtype(executable.getReturnType(),
        types().erasure(util().getType(List.class)))) {
      isList = true;
      returnType = null;
      optionalArrayStart = "Array<";
      optionalArrayEnd = ">";
      initializer = "[]";
      return; // TODO: Do not do stuff for GemLists - they are Observeable by default
    }

    // MethodName
    String methodName;
    if (!overloadMethod && context.isInvalidOverload(executable) && !util().hasTypeParameters(
        executable)) {
      methodName = guiAdapterUtils.getOverloadMethodName(executable);
    }
    else {
      methodName = context.getActualName(executable);
    }

    // Duplicated Print
    if (!overloadMethod && visitedMethods.contains(
        getParentElement().toString() + "." + methodName)) {
      return;
    }
    visitedMethods.add(getParentElement().toString() + "." + methodName);

    // Private fieldName
    String fieldName = methodName + GUIAdapterUtils.SYNCHRONIZED_RESULT_SUFFIX;
    print("protected " + fieldName + ": ");
    printType(returnType, optionalArrayStart, optionalArrayEnd, overloadMethod)
        .print(" = undefined;").println().printIndent();

    // Method Header
    print("public ").print(methodName + GUIAdapterUtils.SYNCHRONIZED_SUFFIX).print("(");
    boolean isAsyncE = context.hasAnnotationType(executable, JSweetConfig.ANNOTATION_ASYNC);
    printMethodParameters(executable, overloadMethod, GUIAdapterUtils.SYNCHRONIZED_SUFFIX);
    print("): ");
    printType(returnType, optionalArrayStart, optionalArrayEnd, overloadMethod)
        .print(" {").println().startIndent().printIndent();

    // Method Body
    if (isAsyncE) {
      print("// TODO: make me properly!");
      print("/* Printed by ").print(getClass().getName()).print(" */").println();
      print("if (this." + fieldName + " === undefined) {").startIndent().println().printIndent();
      print("this." + fieldName + " = " + initializer + ";").println().printIndent();
      // TODO: Add this to the set{fieldName?cap_first}_internal method too?
      print("new Promise<");
      printType(returnType, optionalArrayStart, optionalArrayEnd, overloadMethod);
      print(">(async resolve => {").println().startIndent().printIndent();
      print("resolve(");
      if (isList) {
        importFromWithModule("GemListUtil", "@umlp/commonj2ts");
        print("GemListUtil.toArray(");
      }
      print("(");
      if (context.hasAnnotationType(executable, JSweetConfig.ANNOTATION_ASYNC)) {
        print("await ");
      }
      print("this.").print(executable.getSimpleName()).print("(");
      if (executable.getParameters().size() > 0) {
        print(executable.getParameters().stream().map(p -> p.getSimpleName().toString()).collect(
            Collectors.joining(", ")));
      }
      print("))");
      if (isList) {
        //        .printArgList((List<? extends ExtendedElement>) executable.getParameters())
        print(")");
      }
      print(");").println().endIndent().printIndent();
      print("}).then(val => { this." + fieldName + " = val; });").println().endIndent()
          .printIndent();
      print("}").println().printIndent();
      print("return this." + fieldName + ";");
    }
    else {
      print("//Simply delegate without additional sync").println();
      print("return this." + methodName + "(");
      if (executable.getParameters().size() >= 1 && executable.getParameters()
          .get(0) instanceof ExtendedElement) {
        throw new IllegalStateException("TODO");
      }
      else if (executable.getParameters().size() > 0) {
        for (VariableElement parameter : executable.getParameters()) {
          String paramName = context.getActualName(parameter);
          print(paramName);
          print(", ");
        }
        getPrinter().removeLastChars(2);
      }
      print(");");
    }
    endIndent().println().printIndent().print("}").println().println().printIndent();
  }

  private void printMyNewSynchronizedMethod(ExecutableElement executable, boolean overloadMethod) {
    if (executable.getReturnType().toString().equals("void")) {
      return;
    }

    TypeElement classElement = getParentElement(TypeElement.class);
    TypeElement superType = (TypeElement) types().asElement(classElement.getSuperclass());
    if (util().getAllMembers(superType).stream().anyMatch(e -> e.getSimpleName().equals(executable.getSimpleName()))) {
      return;
    }

    // MethodName
    String methodName;
    if (!overloadMethod && context.isInvalidOverload(executable) && !util().hasTypeParameters(
        executable)) {
      methodName = guiAdapterUtils.getOverloadMethodName(executable);
    } else {
      methodName = context.getActualName(executable);
    }

    Tree returnType = trees().getTree(executable).getReturnType();

    printResultCache(executable, overloadMethod, methodName, returnType);

    printSynchronizerMethod(executable, overloadMethod, methodName, returnType);

    printUpdateMethod(executable, overloadMethod, methodName);
  }

  private void printUpdateMethod(ExecutableElement executable, boolean overloadMethod, String methodName) {
    print("public " + methodName + GUIAdapterUtils.UPDATE_SUFFIX + "(");
    printMethodParameters(executable, overloadMethod, GUIAdapterUtils.UPDATE_SUFFIX);
    print("): void {").println().startIndent().printIndent()
        .print("Promise.resolve(this." + methodName + "(");
    printArguments(executable);
    print(")).then(__v => this." + methodName + guiAdapterUtils.SYNCHRONIZED_RESULT_SUFFIX);
    printResultCacheSetter(executable, "__v");
    print(");").println().endIndent().printIndent().print("}").println().println().printIndent();
  }

  private void printSynchronizerMethod(ExecutableElement executable, boolean overloadMethod, String methodName,
      Tree returnType) {
    print("public " + methodName + guiAdapterUtils.SYNCHRONIZED_SUFFIX + "(");
    printMethodParameters(executable, overloadMethod, guiAdapterUtils.SYNCHRONIZED_SUFFIX);
    print("): ").getPrinter().substituteAndPrintType(returnType)
        .print(" {").println().startIndent().printIndent()
        .print("if (this." + methodName + guiAdapterUtils.SYNCHRONIZED_RESULT_SUFFIX);
    printResultCacheAccessor(executable);
    print(" === undefined) {").println().startIndent().printIndent()
        .print("this." + methodName + guiAdapterUtils.SYNCHRONIZED_RESULT_SUFFIX);
    printResultCacheSetter(executable, "null");
    print(";").println().printIndent()
        .print("this.waitAllInited().then(_ => this." + methodName + GUIAdapterUtils.UPDATE_SUFFIX + "(");
    printArguments(executable);
    print("));").println().endIndent().printIndent().print("}").println().printIndent()
        .print("return this." + methodName + guiAdapterUtils.SYNCHRONIZED_RESULT_SUFFIX);
    printResultCacheAccessor(executable);
    print(";").println().endIndent().printIndent().print("}").println().println().printIndent();
  }

  private void printArguments(ExecutableElement executable) {
    if (executable.getParameters().size() >= 1 && executable.getParameters()
        .get(0) instanceof ExtendedElement) {
      throw new IllegalStateException("TODO");
    }
    else if (executable.getParameters().size() > 0) {
      for (VariableElement parameter : executable.getParameters()) {
        String paramName = context.getActualName(parameter);
        print(paramName);
        print(", ");
      }
      getPrinter().removeLastChars(2);
    }
  }

  private void printResultCacheAccessor(ExecutableElement executable) {
    if (!executable.getParameters().isEmpty()) {
      print(".get([");
      for (VariableElement parameter : executable.getParameters()) {
        print(context.getActualName(parameter));
        if (UMLPAdapterUtils.isSubTypeOf((TypeElement) types().asElement(parameter.asType()),
            RTEPathUtil.getBackendRTECommonPath("GemIdentifiable"), util(), types())) {
          print(".getGemId()");
        }
        print(", ");
      }
      getPrinter().removeLastChars(2);
      print("])");
    }
  }

  private void printResultCacheSetter(ExecutableElement executable, String value) {
    if (!executable.getParameters().isEmpty()) {
      print(".set([");
      for (VariableElement parameter : executable.getParameters()) {
        print(context.getActualName(parameter));
        if (UMLPAdapterUtils.isSubTypeOf((TypeElement) types().asElement(parameter.asType()),
            RTEPathUtil.getBackendRTECommonPath("GemIdentifiable"), util(), types())) {
          print(".getGemId()");
        }
        print(", ");
      }
      getPrinter().removeLastChars(2);
      print("], " + value + ")");
    } else {
      print(" = " + value);
    }
  }

  private void printResultCache(ExecutableElement executable, boolean overloadMethod, String methodName,
      Tree returnType) {
    // Print result cache
    print("private ");
    print(methodName + guiAdapterUtils.SYNCHRONIZED_RESULT_SUFFIX + ": ");
    if (executable.getParameters().isEmpty()) {
      printType(returnType, "", "", overloadMethod)
          .print(" = undefined;");
    } else {
      importFromWithoutModule("ManyKeysMap", "many-keys-map");
      print("ManyKeysMap<any, ");
      getPrinter().substituteAndPrintType(returnType).print(">");
      print(" = new ManyKeysMap()");
    }
    println().printIndent();
  }

  @Override
  public boolean substituteMethodBody(TypeElement parentTypeElement, ExecutableElement method) {
    if (guiAdapterUtils.isSynchronized(method)) {
      String updateName = method.getSimpleName().toString() + guiAdapterUtils.UPDATE_SUFFIX;
      printIndent().print("let " + OBSERVER_FUNCTION_NAME
              + " = (obj: any) => ComponentObserverManager.addObserver(obj, ")
          .print("() => this." + updateName)
          .print(".bind(this)(");
      printArguments(method);
      String guiComponentName = "\"" + parentTypeElement.getQualifiedName().toString() + "\"";
      if (guiComponentName.endsWith("TOP\"") && guiAdapterUtils.isDirective(parentTypeElement)) {
        guiComponentName = guiComponentName.substring(0, guiComponentName.length() - 4) + "\"";
      }
      print("), '" + updateName + "', " + guiComponentName + ");").println();

      for (VariableElement parameter : method.getParameters()) {
        printIndent().print(OBSERVER_FUNCTION_NAME + "(" + context.getActualName(parameter) + ");").println();
      }
    }
    return super.substituteMethodBody(parentTypeElement, method);
  }

  // Add Observers to Domain Objects
  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    boolean shouldPrintObs = false;
    ExecutableElement surroundingMethod = null;
    surroundingMethod = Util.getElement(getPrinter().getParent(MethodTree.class));

    if (surroundingMethod != null && guiAdapterUtils.isSynchronized(surroundingMethod)) {
      // targets invocated Element p.getBestFriend()...
      Element invocatedType = types().asElement(invocation.getType());
      if (invocatedType != null && invocatedType instanceof TypeElement &&
          UMLPAdapterUtils.isSubTypeOf((TypeElement) invocatedType,
              RTEPathUtil.getBackendRTECommonPath("GemIdentifiable"), util(), types())) {
        shouldPrintObs = true;
      }

      //      // Outermost variable access: p.getName()
      //      if (invocation.getTargetExpression() instanceof VariableAccessElement) {
      //        Element targetType = types().asElement(invocation.getTargetType());
      //        if (targetType != null && targetType instanceof TypeElement &&
      //            UMLPAdapterUtils.isSubTypeOf((TypeElement) targetType,
      //                RTEPathUtil.getBackendRTECommonPath("GemIdentifiable"), guiAdapterUtils(), types())) {
      //          shouldPrintObs = true;
      //        }
      //      }
    }

    if (shouldPrintObs) {
      print(OBSERVER_FUNCTION_NAME + "(");
    }
    boolean res = super.substituteMethodInvocation(invocation);
    if (shouldPrintObs) {
      print(")");
    }
    return res;
  }

  private void printInitPromiseMethod(ExecutableElement executable, boolean overloadMethod) {
    if (executable.getReturnType().toString().equals("void")) {
      return;
    }

    boolean isList = false;
    String optionalArrayStart = "";
    String optionalArrayEnd = "";
    String initializer = "null";
    Tree returnType = trees().getTree(executable).getReturnType();

    // MethodName
    String methodName;
    if (!overloadMethod && context.isInvalidOverload(executable) && !util().hasTypeParameters(
        executable)) {
      methodName = guiAdapterUtils.getOverloadMethodName(executable);
    } else {
      methodName = context.getActualName(executable);
    }

    // Duplicated Print
    if (!overloadMethod && visitedMethods.contains(
        getParentElement().toString() + "." + methodName)) {
      return;
    }
    visitedMethods.add(getParentElement().toString() + "." + methodName);

    // Private fieldName
    String fieldName = methodName + GUIAdapterUtils.INIT_PROMISE_RESULT_SUFFIX;
    print("protected " + fieldName + ": Promise<");
    printType(returnType, optionalArrayStart, optionalArrayEnd, overloadMethod)
        .print("> = undefined;").println().printIndent();

    // Method Header
    print("public ").print(methodName + GUIAdapterUtils.INIT_PROMISE_SUFFIX).print("(");
    printMethodParameters(executable, overloadMethod, GUIAdapterUtils.INIT_PROMISE_SUFFIX);
    print("): Promise<");
    printType(returnType, optionalArrayStart, optionalArrayEnd, overloadMethod)
        .print("> {").println().startIndent().printIndent();
    print("if (this." + fieldName + " == undefined) {").println().startIndent().printIndent()
        .print("this." + fieldName + " = this.waitAllInited().then(() => Promise.resolve(this."
            + methodName + "(");
    printArguments(executable);
    print(")));");
    println().endIndent().printIndent().print("}").println().printIndent()
        .print("return this." + fieldName + ";")
        .println().endIndent().printIndent().print("}").println().println().printIndent();
  }

  private void printMethodParameters(ExecutableElement executable, boolean overloadMethod,
      String methodSuffix) {
    if (executable.getParameters().size() >= 1 && executable.getParameters()
        .get(0) instanceof ExtendedElement) {
      printArgList((List<? extends ExtendedElement>) executable.getParameters());
    }
    else if (executable.getParameters().size() > 0) {
      for (VariableElement parameter : executable.getParameters()) {
        Tree tree = trees().getTree(parameter);
        String paramName = context.getActualName(parameter);
        print(paramName).print(overloadMethod ? "?" : "").print(": ").getPrinter()
            .substituteAndPrintType(tree).print(", ");
        int beginIndex = getPrinter().getOutput().lastIndexOf(methodSuffix + "(") +
            (methodSuffix + "(").length();
        String currentHeader = getPrinter().getOutput().substring(beginIndex);
        if (currentHeader.indexOf(paramName + ": ") != currentHeader.lastIndexOf(
            paramName + ": ")) {
          getPrinter().removeLastChars(getPrinter().getOutput().length() - beginIndex);
          print(currentHeader.substring((paramName + ": ").length()));
        }
      }
      getPrinter().removeLastChars(2);
    }
  }

  private GUIComponentAdapter printType(Tree returnType, String optionalArrayStart,
      String optionalArrayEnd, boolean overloadMethod) {
    if (overloadMethod) {
      print("any");
      return this;
    }
    else if (returnType == null) {
      print(optionalArrayStart + "any" + optionalArrayEnd);
      return this;
    }
    else {
      print(optionalArrayStart).getPrinter().substituteAndPrintType(returnType)
          .print(optionalArrayEnd);
      return this;
    }
  }

  private void printAdaptedConstructor(ExecutableElement executable) {
    TypeElement classElement = getParentElement(TypeElement.class);

    if (guiAdapterUtils.isDirective(classElement)) {
      // Print Nothing, probably an error
      return;
    }
    else if (guiAdapterUtils.isComponent(classElement)) {
      print("public constructor(route: ActivatedRoute, protected router: Router) {");
      startIndent();
      Element parentClass = types().asElement(classElement.getSuperclass());
      Optional<? extends Element> parentConstructor = parentClass.getEnclosedElements().stream()
          .filter(s -> s.getKind().toString().equals("CONSTRUCTOR")).findAny();
      if (guiAdapterUtils.getAnnotationPropertyValue(classElement, "isEmpty", Boolean.class, false)) {
        println().printIndent().print("super(router);");
      }
      else if (guiAdapterUtils.getAnnotationPropertyValue(classElement, "isCreate", Boolean.class, false)) {
        println().printIndent().print("super(router);");
      }
      else {
        println().printIndent().print("super(route, router);");
      }
      endIndent().println().printIndent().print("}");
    }
  }

  @Override
  public void afterType(TypeElement type) {
    // Remove standard constructor
    if (guiAdapterUtils.isDirective(type) || guiAdapterUtils.isComponent(type) || guiAdapterUtils.isHWCofComponent(type)) {
      int publicConstructor = getPrinter().getOutput().lastIndexOf("constructor()");
      if (publicConstructor < 0) {
        return;
      }
      int length = getPrinter().getOutput().length();
      getPrinter().removeLastChars(length - publicConstructor);
      println().print("}").println();
      print(type.getSimpleName()).print("[\"__class\"] = \"").print(type.getQualifiedName())
          .print("\";");
    }
  }

  @Override
  public boolean substituteExtends(TypeElement type) {
    if (guiAdapterUtils.isHWCofComponent(type)) {
      String componentName = type.getSimpleName().toString() + "Component";
      print(" extends " + componentName);
      String path = type.getQualifiedName().toString().replace(".", "/");

      importFromWithModule(componentName + ", config", "@src/" + path + "Component");
      importFromWithModule("ActivatedRoute, Router", "@angular/router");
      return true;
    }
    return super.substituteExtends(type);
  }

  @Override
  public Java2TypeScriptTranslator getPrinter() {
    if (super.getPrinter() instanceof Java2TypeScriptTranslator) {
      return (Java2TypeScriptTranslator) super.getPrinter();
    }
    else {
      Log.error("0JSC001 Java2TypeScriptTranslater must be core Adapter");
      return null;
    }
  }

  private void importFromWithModule(String importedName, String pathToImportedClass) {
    getPrinter().useModule(
        new ModuleImportDescriptor(importedName, pathToImportedClass));
  }

  private void importFromWithoutModule(String importedName, String pathToImportedClass) {
    Set<String> importedNames = context.getImportedNames(context.currentCompilationUnit.get().getSourceFile().getName());
    if (!importedNames.contains(importedName)) {
      context.addHeader("import." + importedName, "import " + importedName + " from '" + pathToImportedClass + "';");
    }
  }

  private AnnotationManager getRenamingUnsynchronizedAnnotationManager() {
    return new AnnotationManager() {
      @Override
      public Action manageAnnotation(Element element, String annotationType) {
        // add the @Name annotation to non-public elements
        return GUIAdapterUtils.SYNCRHONIZED_ANNOTATION.equals(annotationType) &&
            element.getKind().toString().equals("METHOD") &&
            element.getSimpleName().toString().startsWith("get") &&
            !((Symbol.MethodSymbol) element).getReturnType().getKind().equals(TypeKind.VOID) &&
            guiAdapterUtils.isMethodOfComponent(element) ?
            Action.ADD :
            Action.VOID;
      }
    };
  }
}
