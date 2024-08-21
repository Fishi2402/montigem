/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.guiAdapters;

import de.se_rwth.commons.logging.Log;
import def.js.Array;
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
import standalone.com.sun.source.tree.ClassTree;
import standalone.com.sun.source.tree.MethodTree;
import standalone.com.sun.source.tree.Tree;
import standalone.com.sun.tools.javac.code.Symbol;
import standalone.com.sun.tools.javac.tree.JCTree;
import umlp.jsweet.extension.util.RTEPathUtil;
import umlp.jsweet.extension.util.UMLPAdapterUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
//      printWaitAllInited(type);
      printGetObs(type);
    }
    if (guiAdapterUtils.isComponent(type) ||  guiAdapterUtils.isDirective(type) && !guiAdapterUtils.isTOPComponent(type)) {
      printNgOnDestroy(type);
    }
    super.beforeTypeBody(type);
  }

  private void printGetObs(TypeElement type) {
    String guiComponentName = "\"" + type.getQualifiedName().toString() + "\"";
    if (guiComponentName.endsWith("TOP\"") && guiAdapterUtils.isDirective(type)) {
      guiComponentName = guiComponentName.substring(0, guiComponentName.length() - 4) + "\"";
    }
    printIndent().print("protected getObs<T extends GemIdentifiable>(updateName: string, update: (() => void)): <T extends GemIdentifiable>(obj: T) => T {")
        .println().startIndent().printIndent()
        .print("return (obj: T) => {").println().startIndent().printIndent()
//        .print("console.log(obj, updateName);").println().printIndent()
        .print("if (obj != undefined && obj != null) {").println().startIndent().printIndent()
        .print("return ComponentObserverManager.addObserver(obj, update.bind(this), updateName, "
                + guiComponentName + ");")
        .println().endIndent().printIndent().print("}")
        .print("else {").println().startIndent().printIndent()
        .print("return null;")
        .println().endIndent().printIndent().print("}")
        .println().endIndent().printIndent().print("}")
        .println().endIndent().printIndent().print("}").println().println();

    importFromWithModule("GemIdentifiable", "@umlp/commonj2ts");
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
//      context.addExtraAnnotationType(executable, JSweetConfig.ANNOTATION_ASYNC);
    }
    if (guiAdapterUtils.isDirectiveMethod(executable, classElement, this)) {
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
//        .print("console.log(\"" + methodName + GUIAdapterUtils.UPDATE_SUFFIX + "\");")
        .println().printIndent()
        .print("Promise.resolve(this." + methodName + "(");
    printArguments(executable);
    print(")).then(__v => this." + methodName + guiAdapterUtils.SYNCHRONIZED_RESULT_SUFFIX);
    String arg = "__v === undefined ? null : __v";
    if (UMLPAdapterUtils.isSubTypeOf((TypeElement) types().asElement(executable.getReturnType()),
        "java.util.List", util(), types())) {
      arg = arg
//          + ".toArrayDeep()"
      ;
    }
    printResultCacheSetter(executable, arg);
    print(");").println().endIndent().printIndent().print("}").println().println().printIndent();
  }

  private void printSynchronizerMethod(ExecutableElement executable, boolean overloadMethod, String methodName,
      Tree returnType) {
    print("public " + methodName + guiAdapterUtils.SYNCHRONIZED_SUFFIX + "(");
    printMethodParameters(executable, overloadMethod, guiAdapterUtils.SYNCHRONIZED_SUFFIX);
    print("): ");
    printType(returnType, overloadMethod, true)
        .print(" {").println().startIndent().printIndent()
        .print("if (this." + methodName + guiAdapterUtils.SYNCHRONIZED_RESULT_SUFFIX);
    printResultCacheAccessor(executable);
    print(" === undefined) {").println().startIndent().printIndent()
        .print("this." + methodName + guiAdapterUtils.SYNCHRONIZED_RESULT_SUFFIX);
    printResultCacheSetter(executable, "null");
    print(";").println().printIndent()
//        .print("this.waitAllInited().then(_ => this." + methodName + GUIAdapterUtils.UPDATE_SUFFIX + "(");
        .print("this." + methodName + GUIAdapterUtils.UPDATE_SUFFIX + "(");
    printArguments(executable);
//    print("));").println().endIndent().printIndent().print("}").println().printIndent()
    print(");").println().endIndent().printIndent().print("}").println().printIndent()
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
      printType(returnType, overloadMethod, true)
          .print(" = undefined;");
    } else {
      importFromWithoutModule("ManyKeysMap", "many-keys-map");
      print("ManyKeysMap<any, ");
      printType(returnType, overloadMethod, true).print(">");
      print(" = new ManyKeysMap()");
    }
    println().printIndent();
  }

  @Override
  public boolean substituteMethodBody(TypeElement parentTypeElement, ExecutableElement method) {
    if (guiAdapterUtils.isSynchronized(method)) {
      String updateName = method.getSimpleName().toString() + guiAdapterUtils.UPDATE_SUFFIX;
      printIndent().print("let " + OBSERVER_FUNCTION_NAME + " = this.getObs('" + updateName + "', ")
              .print("() => this." + updateName + "(");
      printArguments(method);
      print("));").println();
//      printIndent().print("let " + OBSERVER_FUNCTION_NAME
//              + " = (obj: any) => ComponentObserverManager.addObserver(obj, ")
//          .print("() => this." + updateName)
//          .print(".bind(this)(");
//      printArguments(method);
//      String guiComponentName = "\"" + parentTypeElement.getQualifiedName().toString() + "\"";
//      if (guiComponentName.endsWith("TOP\"") && guiAdapterUtils.isDirective(parentTypeElement)) {
//        guiComponentName = guiComponentName.substring(0, guiComponentName.length() - 4) + "\"";
//      }
//      print("), '" + updateName + "', " + guiComponentName + ");").println();

      for (VariableElement parameter : method.getParameters()) {
        if (UMLPAdapterUtils.isSubTypeOf(Util.getElement(parameter.asType()),
            RTEPathUtil.getBackendRTECommonPath("GemIdentifiable"), util(), types())) {
          printIndent().print(
              OBSERVER_FUNCTION_NAME + "(" + context.getActualName(parameter) + ");").println();
        }
//        else if (UMLPAdapterUtils.isSubTypeOf(Util.getElement(parameter.asType()),
//            "java.util.List", util(), types())) {
//          Type innerType = ((Type.ClassType) parameter.asType()).allparams().get(0);
//          if (UMLPAdapterUtils.isSubTypeOf(Util.getElement(innerType),
//              RTEPathUtil.getBackendRTECommonPath("GemIdentifiable"), util(), types())) {
//            printIndent().print("for await (const __v of ").print(context.getActualName(parameter))
//                    .print(") { ").print(OBSERVER_FUNCTION_NAME + "(__v); }").println();
//          }
//        }
      }
    }
    return super.substituteMethodBody(parentTypeElement, method);
  }

  // Add Observers to Domain Objects
  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    boolean shouldPrintObs = false;
    boolean shouldPrintToArray = false;
    ExecutableElement surroundingMethod = null;
    surroundingMethod = Util.getElement(getPrinter().getParent(MethodTree.class));

    Element invocatedType = types().asElement(invocation.getType());

    if (surroundingMethod != null && guiAdapterUtils.isSynchronized(surroundingMethod)) {
      // targets invocated Element p.getBestFriend()...
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

    if (invocatedType != null && invocatedType instanceof TypeElement &&
        UMLPAdapterUtils.isSubTypeOf((TypeElement) invocatedType, "java.util.List", util(), types())) {
      shouldPrintToArray = true;
    }

    if (shouldPrintObs) {
      print(OBSERVER_FUNCTION_NAME + "(");
    }
    if (shouldPrintToArray) {
      print("((__var: any) => __var instanceof Array ? __var : __var.toArray())(");
    }

    boolean res = super.substituteMethodInvocation(invocation);

    if (shouldPrintToArray) {
      print(")");
    }
    if (shouldPrintObs) {
      print(")");
    }
    return res;
  }

  private void printInitPromiseMethod(ExecutableElement executable, boolean overloadMethod) {
    if (executable.getReturnType().toString().equals("void")) {
      return;
    }

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
    printType(returnType, overloadMethod, false)
        .print("> = undefined;").println().printIndent();

    // Method Header
    print("public ").print(methodName + GUIAdapterUtils.INIT_PROMISE_SUFFIX).print("(");
    printMethodParameters(executable, overloadMethod, GUIAdapterUtils.INIT_PROMISE_SUFFIX);
    print("): Promise<");
    printType(returnType, overloadMethod, false)
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

  private GUIComponentAdapter printType(Tree returnType, boolean overloadMethod,
      boolean replaceListByArray) {
    if (overloadMethod) {
      print("any");
      return this;
    }
    else if (returnType == null) {
      print("any");
      return this;
    }
    else {
      TypeMirror typeType = Util.getType(returnType);
//      if (replaceListByArray && UMLPAdapterUtils.isSubTypeOf((TypeElement) types().asElement(typeType),
//          "java.util.List", util(), types())) {
      if (false) {
        standalone.com.sun.tools.javac.util.List<JCTree.JCExpression> typeArguments = ((JCTree.JCTypeApply) returnType).getTypeArguments();
        if (typeArguments.size() > 0) {
          printType(typeArguments.get(0), overloadMethod, replaceListByArray);
        }
        print("[]");
      } else {
        getPrinter().substituteAndPrintType(returnType);
      }
      return this;
    }
  }

  private TypeMirror mapListToArray(TypeMirror type) {
    if (types().isSubtype(types().erasure(type), types().erasure(util().getType(List.class)))
        && type instanceof DeclaredType) {
      List<? extends TypeMirror> typeArgs = ((DeclaredType) type).getTypeArguments();
      if (typeArgs.size() > 0) {
        TypeMirror backingType = typeArgs.get(0);
        if (backingType.getKind() == TypeKind.DECLARED) {
          TypeMirror mappedBackingType = mapListToArray(backingType);
          DeclaredType result = types().getDeclaredType(
              Util.getElement(util().getType(Array.class)), mappedBackingType);
          return result;
        }
      }
    }
    return type;
  }

  private void printAdaptedConstructor(ExecutableElement executable) {
    TypeElement classElement = getParentElement(TypeElement.class);

    if (guiAdapterUtils.isDirective(classElement)) {
      // Print Nothing, probably an error
      return;
    } else if (guiAdapterUtils.isComponent(classElement)) {
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
