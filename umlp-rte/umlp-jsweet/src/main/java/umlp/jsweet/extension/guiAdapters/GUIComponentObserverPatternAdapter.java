/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.guiAdapters;

import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.StringUtils;
import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.OverloadScanner;
import org.jsweet.transpiler.extension.AnnotationManager;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ExtendedElementFactory;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.model.support.MethodInvocationElementSupport;
import org.jsweet.transpiler.util.Util;
import standalone.com.sun.source.tree.ClassTree;
import standalone.com.sun.source.tree.MethodInvocationTree;
import standalone.com.sun.source.tree.MethodTree;
import standalone.com.sun.source.tree.Tree;
import standalone.com.sun.source.tree.VariableTree;
import standalone.com.sun.tools.javac.code.Symbol;
import standalone.com.sun.tools.javac.tree.JCTree;
import umlp.jsweet.extension.util.GUIAdapterUtils;
import umlp.jsweet.extension.util.RTEPathUtil;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static umlp.jsweet.extension.guiAdapters.DelegateGemListToArrayAdapter.shouldPrintToArray;

public class GUIComponentObserverPatternAdapter extends PrinterAdapter {

  private static final String DOMAIN_OBSERVER_FUNCTION_NAME = "__domainObs";

  private static final String STATEVAR_OBSERVER_FUNCTION_NAME = "__statevarObs";

  private static final String GET_DOMAIN_OBS = "getDomainObs";

  private static final String GET_STATEVAR_OBS = "getStateVarObs";

  private GUIAdapterUtils adapterUtils;

  public GUIComponentObserverPatternAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    addAnnotationManager(getSynchronizedAnnotationManager());
    addAnnotationManager(getAlterInitMethodAnnotationManager());
    addAnnotationManager(addComponentManagerListenerToSetAnnotationManager());
    this.adapterUtils = new GUIAdapterUtils(this, types(), context, util());
  }

  @Override
  public void beforeTypeBody(TypeElement type) {
    if (adapterUtils.isDirective(type)) {
      printUniqueIds(type);
      printHashes(type);
      printGetObs(type);
    }
    if (adapterUtils.isComponent(type) ||  adapterUtils.isDirective(type) && !adapterUtils.isTOPComponent(type)) {
      printNgOnDestroy(type);
    }
    adapterUtils.importFromWithModule("GemListUtil", GUIAdapterUtils.UMLP_COMMON_POINTER);
    super.beforeTypeBody(type);
  }

  @Override
  public boolean substituteImplements(TypeElement type) {
    boolean res = super.substituteImplements(type);

    if ((adapterUtils.isComponent(type))) {
      adapterUtils.importFromWithModule("OnDestroy", "@angular/core");
      if (util().getInterfaces(type).isEmpty()) {
        print(" implements OnDestroy");
      } else {
        print(", OnDestroy");
      }
    }

    return res;
  }

  private void printUniqueIds(TypeElement type) {
    String guiComponentName = type.getSimpleName().toString();
    printIndent().print("private static nextId: number = 0;").println()
        .printIndent().print("protected uniqueIdentifier:number = " + guiComponentName + ".nextId++;")
        .println().println();
  }

  private void printHashes(TypeElement type) {
    adapterUtils.importFromWithoutModule("ManyKeysMap", "many-keys-map");
    String guiComponentName = type.getSimpleName().toString();
    printIndent().print("private static nextHash: number = 0;").println();
    printIndent().print("protected hashes: ManyKeysMap<any, number> = new ManyKeysMap();").println();
    printIndent().print("protected computeHash(objs: any[]): number {").println();
    startIndent().printIndent().print("let key = [];").println();
    printIndent().print("for (const obj of objs) {").println();
    startIndent().printIndent().print("if (typeof obj.getGemId === 'function') {").println();
    startIndent().printIndent().print("key.push(obj.getGemId());").println();
    endIndent().printIndent().print("} else {").println();
    startIndent().printIndent().print("key.push(obj);").println();
    endIndent().printIndent().print("}").println();
    endIndent().printIndent().print("}").println();
    printIndent().print("if (this.hashes.has(key)) {").println();
    startIndent().printIndent().print("return this.hashes.get(key);").println();
    endIndent().printIndent().print("} else {").println();
    startIndent().printIndent().print("let hash: number = " + guiComponentName + ".nextHash++;").println();
    printIndent().print("this.hashes.set(key, hash);").println();
    printIndent().print("return hash;").println();
    endIndent().printIndent().print("}").println();
    endIndent().printIndent().print("}").println().println();
  }

  private void printGetObs(TypeElement type) {
    String guiComponentName = "\"" + type.getQualifiedName().toString() + "\"";
    if (guiComponentName.endsWith("TOP\"") && adapterUtils.isDirective(type)) {
      guiComponentName = guiComponentName.substring(0, guiComponentName.length() - 4) + "\"";
    }
    guiComponentName = guiComponentName + " + this.uniqueIdentifier";
    printIndent().print("protected " + GET_DOMAIN_OBS
            + "<T extends GemIdentifiable>(updateName: string, update: (() => void)):  <T extends GemIdentifiable>(obj: T, type: string) => T {")
        .println().startIndent().printIndent()
        .print("return (obj: T, type: string) => {").println().startIndent().printIndent()
        .print("if (obj != undefined && obj != null) {").println().startIndent().printIndent()
        .print("return ComponentObserverManager.addDomainObjectObserver(obj, type, update.bind(this), updateName, "
                + guiComponentName + ");")
        .println().endIndent().printIndent().print("}")
        .print(" else {").println().startIndent().printIndent()
        .print("return null;")
        .println().endIndent().printIndent().print("}")
        .println().endIndent().printIndent().print("}")
        .println().endIndent().printIndent().print("}").println().println();

    printIndent().print("protected " + GET_STATEVAR_OBS
            + "<T>(updateName: string, update: (() => void)): <T>(obj: T, variableName: string) => T {")
        .println().startIndent().printIndent()
        .print("return (obj: T, variableName: string) => {").println().startIndent().printIndent()
        .print(
            "return ComponentObserverManager.addStateVariableObserver(obj, variableName, update.bind(this), updateName, "
                + guiComponentName + ");")
        .println().endIndent().printIndent().print("}")
        .println().endIndent().printIndent().print("}").println().println();

    adapterUtils.importFromWithModule("GemIdentifiable", GUIAdapterUtils.UMLP_COMMON_POINTER);
  }

  private void printNgOnDestroy(TypeElement typeElement) {
    String guiComponentName = "\"" + typeElement.getQualifiedName().toString() + "\"";
    if (guiComponentName.endsWith("TOP\"") && adapterUtils.isDirective(typeElement)) {
      guiComponentName = guiComponentName.substring(0, guiComponentName.length() - 4) + "\"";
    }
    guiComponentName = guiComponentName + " + this.uniqueIdentifier";
    printIndent().print("public ngOnDestroy(): void {").println().startIndent().printIndent();
    if (adapterUtils.isHWCofComponent(typeElement)) {
      print("super.ngOnDestroy();").println().printIndent();
    }
    print("ComponentObserverManager.removeObservers(" + guiComponentName + ");")
        .println().endIndent().printIndent().print("}")
        .println().println();
  }

  @Override
  public boolean substituteVariable(VariableElement variable) {
    if (variable.getSimpleName().toString().equals(DOMAIN_OBSERVER_FUNCTION_NAME)) {
      Symbol.MethodSymbol method = getParentElement(Symbol.MethodSymbol.class);
      String updateName = method.getSimpleName().toString() + adapterUtils.UPDATE_SUFFIX;
      print(DOMAIN_OBSERVER_FUNCTION_NAME + " = this." + GET_DOMAIN_OBS + "('" + updateName + "'")
          .print(" + this.computeHash([");
      printArguments(method);
      print("]), () => this." + updateName + "(");
      printArguments(method);
      print("))");
      return true;
    }

    if (variable.getSimpleName().toString().equals(STATEVAR_OBSERVER_FUNCTION_NAME)) {
      Symbol.MethodSymbol method = getParentElement(Symbol.MethodSymbol.class);
      String updateName = method.getSimpleName().toString() + adapterUtils.UPDATE_SUFFIX;
      print(STATEVAR_OBSERVER_FUNCTION_NAME + " = this." + GET_STATEVAR_OBS + "('" + updateName + "'")
          .print(" + this.computeHash([");
      printArguments(method);
      print("]), () => this." + updateName + "(");
      printArguments(method);
      print("))");
      return true;
    }

    return super.substituteVariable(variable);
  }

  @Override
  public boolean substituteExecutable(ExecutableElement executable) {
    TypeElement classElement = getParentElement(TypeElement.class);
    if (adapterUtils.isSynchronizedMethod(executable, this) &&
        !context.hasAnnotationType(executable, Override.class.getName()))
      printSynchronizerMethods(executable, false);
    if (adapterUtils.isSynchronizedMethod(executable, this)) {
      addObserversToParameters(executable, classElement);
    }
    if (adapterUtils.isInitMethod(executable, classElement)) {
      print("protected allVarsInited: boolean = false;").println().printIndent();
    }
    return super.substituteExecutable(executable);
  }

  private void addObserversToParameters(ExecutableElement executable, TypeElement classElement) {
    MethodTree tree = trees().getTree(executable);
    JCTree.JCVariableDecl domainObserver = adapterUtils.getNewParameter(DOMAIN_OBSERVER_FUNCTION_NAME);
    JCTree.JCVariableDecl statevarObserver = adapterUtils.getNewParameter(STATEVAR_OBSERVER_FUNCTION_NAME);
    List<? extends VariableTree> parameters = tree.getParameters();
    if (parameters.size() > 0 && parameters.get(0) instanceof JCTree.JCVariableDecl
        && tree instanceof JCTree.JCMethodDecl) {
      ((JCTree.JCMethodDecl) tree).params =
          ((standalone.com.sun.tools.javac.util.List<JCTree.JCVariableDecl>) parameters)
              .append(domainObserver).append(statevarObserver);
    } else if (parameters.size() == 0 && tree instanceof JCTree.JCMethodDecl) {
      ((JCTree.JCMethodDecl) tree).params = standalone.com.sun.tools.javac.util.List.of(
          domainObserver, statevarObserver);
    } else if (parameters.size() == 0) {
      Log.error("0JS0002 unexpected error while transpiling class " + classElement.getQualifiedName().toString());
    } else if (parameters.size() > 0) {
      Log.error("0JS0003 unexpected error while transpiling class " + classElement.getQualifiedName().toString());
    }
  }

  private Map<String, Set<OverloadScanner.Overload>> overloads = new HashMap<>();

  @Override
  public boolean substituteOverloadMethodBody(TypeElement parentTypeElement,
      OverloadScanner.Overload overload) {
    if (adapterUtils.isSynchronized(overload.getCoreMethodElement())) {
      Set classOverloads = this.overloads.computeIfAbsent(
          parentTypeElement.getQualifiedName().toString(),
          a -> new HashSet<>());
      classOverloads.add(overload);
    }
    return super.substituteOverloadMethodBody(parentTypeElement, overload);
  }

  private void printSynchronizerMethods(ExecutableElement executable, boolean overloadMethod) {
    if (executable.getReturnType().toString().equals("void")) {
      return;
    }

    TypeElement classElement = getParentElement(TypeElement.class);
    TypeElement superType = (TypeElement) types().asElement(classElement.getSuperclass());
    if (util().getAllMembers(superType).stream().anyMatch(e -> e.getSimpleName().equals(executable.getSimpleName()))) {
      // only for the base method, when overloads are present
      return;
    }

    // MethodName
    String methodName;
    if (!overloadMethod && context.isInvalidOverload(executable) && !util().hasTypeParameters(
        executable)) {
      methodName = adapterUtils.getOverloadMethodName(executable);
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
    print(")).then(__v => this." + methodName + adapterUtils.SYNCHRONIZED_RESULT_SUFFIX);
    String arg = "__v === undefined ? null : __v";
    printResultCacheSetter(executable, arg);
    print(");").println().endIndent().printIndent().print("}").println().println().printIndent();
  }

  private void printSynchronizerMethod(ExecutableElement executable, boolean overloadMethod, String methodName,
      Tree returnType) {
    print("public " + methodName + adapterUtils.SYNCHRONIZED_SUFFIX + "(");
    printMethodParameters(executable, overloadMethod, adapterUtils.SYNCHRONIZED_SUFFIX);
    print("): ");
    printType(returnType, overloadMethod, true)
        .print(" {").println().startIndent().printIndent()
        .print("if (!this.allVarsInited) {").println().startIndent().printIndent()
        .print("return undefined;").println().endIndent().printIndent().print("}").println().printIndent()
        .print("if (this." + methodName + adapterUtils.SYNCHRONIZED_RESULT_SUFFIX);
    printResultCacheAccessor(executable);
    print(" === undefined) {").println().startIndent().printIndent()
        .print("this." + methodName + adapterUtils.SYNCHRONIZED_RESULT_SUFFIX);
    printResultCacheSetter(executable, "null");
    print(";").println().printIndent()
        .print("this." + methodName + GUIAdapterUtils.UPDATE_SUFFIX + "(");
    printArguments(executable);
    print(");").println().endIndent().printIndent().print("}").println().printIndent()
        .print("return this." + methodName + adapterUtils.SYNCHRONIZED_RESULT_SUFFIX);
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
        if (adapterUtils.isSubTypeOf((TypeElement) types().asElement(parameter.asType()),
            RTEPathUtil.getBackendRTECommonPath("GemIdentifiable"))) {
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
        if (adapterUtils.isSubTypeOf((TypeElement) types().asElement(parameter.asType()),
            RTEPathUtil.getBackendRTECommonPath("GemIdentifiable"))) {
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
    print(methodName + adapterUtils.SYNCHRONIZED_RESULT_SUFFIX + ": ");
    if (executable.getParameters().isEmpty()) {
      printType(returnType, overloadMethod, true)
          .print(" = undefined;");
    } else {
      adapterUtils.importFromWithoutModule("ManyKeysMap", "many-keys-map");
      print("ManyKeysMap<any, ");
      printType(returnType, overloadMethod, true).print(">");
      print(" = new ManyKeysMap()");
    }
    println().printIndent();
  }

  @Override
  public boolean substituteMethodBody(TypeElement parentTypeElement, ExecutableElement method) {
    if (adapterUtils.isSynchronized(method)) {
      for (VariableElement parameter : method.getParameters()) {
        if (adapterUtils.isSubTypeOf(Util.getElement(parameter.asType()),
            RTEPathUtil.getBackendRTECommonPath("GemIdentifiable"))) {
          String typeName = "\"" +
              adapterUtils.getTypeNameWithoutBuilder((TypeElement) types().asElement(parameter.asType())) +
              "\"";
          String actualName = context.getActualName(parameter);
          printIndent().print(
              DOMAIN_OBSERVER_FUNCTION_NAME + "(" + actualName + ", " + typeName + ");").println();
        }
      }
    }
    return super.substituteMethodBody(parentTypeElement, method);
  }

  // Add Observers to Domain Objects and state variables
  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    boolean shouldPrintDomainObs = false;
    boolean shouldPrintStatevarObs = false;
    boolean shouldAddObservers = false;
    ExecutableElement surroundingMethod = Util.getElement(getPrinter().getParent(MethodTree.class));
    boolean isSynchronizedMethod = surroundingMethod != null && adapterUtils.isSynchronized(surroundingMethod);

    Element invocatedType = types().asElement(invocation.getType());

    if (isSynchronizedMethod) {
      // targets invocated Element p.getBestFriend()...
      if (invocatedType != null && invocatedType instanceof TypeElement &&
          adapterUtils.isSubTypeOf((TypeElement) invocatedType,
              RTEPathUtil.getBackendRTECommonPath("GemIdentifiable")) &&
          adapterUtils.isSubTypeOf((TypeElement) invocatedType,
              RTEPathUtil.getBackendRTECommonPath("GemBuildable"))) {
        shouldPrintDomainObs = true;
      }
    }

    if (isSynchronizedMethod && adapterUtils.isSynchronized(invocation.getMethod())) {
      shouldAddObservers = true;
    }

    ClassTree parent = getPrinter().getParent(ClassTree.class);
    Element parentElement = Util.getElement(parent);
    if (isSynchronizedMethod && adapterUtils.isStateVarGetter(invocation, parentElement, this)) {
      shouldPrintStatevarObs = true;
    }


    if (shouldPrintDomainObs) {
      print(DOMAIN_OBSERVER_FUNCTION_NAME + "(");
    }
    if (shouldPrintStatevarObs) {
      print(STATEVAR_OBSERVER_FUNCTION_NAME + "(");
    }

    boolean res = super.substituteMethodInvocation(invocation);

    if (shouldAddObservers) {
      MethodInvocationTree methodInvocationTree = ExtendedElementFactory.toTree(invocation);
      boolean removeBracketFromAwaitInvocation =
          context.isAwaitInvocation(methodInvocationTree) &&

              // only in this case the await is wrapped in brackets
              (getParentElement() instanceof MethodInvocationTree) ||
              (getParentElement() instanceof MethodInvocationElementSupport) ||
              shouldPrintToArray(invocatedType,getPrinter(),adapterUtils);

      int parenthesisCount = 0;
      if (getPrinter().getOutput().endsWith(")")) {
        parenthesisCount++;
        getPrinter().removeLastChar();
      }
      if (removeBracketFromAwaitInvocation && getPrinter().getOutput().endsWith(")")) {
        parenthesisCount++;
        getPrinter().removeLastChar();
      }
      if (invocation.getArgumentCount() > 0) {
        print(",");
      }
      print(DOMAIN_OBSERVER_FUNCTION_NAME + ", " + STATEVAR_OBSERVER_FUNCTION_NAME);
      for (int i = 0; i < parenthesisCount; i++) {
        print(")");
      }
    }
    if (shouldPrintStatevarObs) {
      print(", \"" + GUIAdapterUtils.STATEVAR_PREFIX + GUIAdapterUtils.getMethodNameWithoutGetIs(invocation) + "\")");
    }
    if (shouldPrintDomainObs) {
      print(", \"").print(adapterUtils.getTypeNameWithoutBuilder((TypeElement) invocatedType))
          .print("\")");
    }
    return res;
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
        print(paramName).print(overloadMethod ? "?" : "").print(": ");
        getPrinter().substituteAndPrintType(((JCTree.JCVariableDecl) tree).getType()).print(", ");
      }
      getPrinter().removeLastChars(2);
    }
  }

  private GUIComponentObserverPatternAdapter printType(Tree returnType, boolean overloadMethod,
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
      getPrinter().substituteAndPrintType(returnType);
      return this;
    }
  }

  private AnnotationManager getSynchronizedAnnotationManager() {
    return new AnnotationManager() {
      @Override
      public Action manageAnnotation(Element element, String annotationType) {
        return GUIAdapterUtils.SYNCRHONIZED_ANNOTATION.equals(annotationType) &&
            element.getKind().toString().equals("METHOD") &&
            (element.getSimpleName().toString().startsWith("get") || element.getSimpleName().toString().startsWith("is")) &&
            !((Symbol.MethodSymbol) element).getReturnType().getKind().equals(TypeKind.VOID) &&
            adapterUtils.isMethodOfComponent(element) ?
            Action.ADD :
            Action.VOID;
      }
    };
  }

  private AnnotationManager getAlterInitMethodAnnotationManager() {
    return new AnnotationManager() {
      @Override
      public Action manageAnnotation(Element element, String annotationType) {
        // add the @Name annotation to non-public elements
        return isInit(element, annotationType) ?
            Action.ADD :
            Action.VOID;
      }

      @Override
      public <T> T getAnnotationValue(Element element,
          String annotationType, String propertyName,
          Class<T> propertyClass, T defaultValue) {
        if (isInit(element, annotationType)) {
          return propertyClass.cast("{{body}}{{baseIndent}}this.allVarsInited = true;");
        } else {
          return null;
        }
      }

      private boolean isInit(Element element, String annotationType) {
        return JSweetConfig.ANNOTATION_REPLACE.equals(annotationType) &&
            element.getKind().toString().equals("METHOD") &&
            element.getSimpleName().toString().equals("init") &&
            adapterUtils.isMethodOfDirective(element);
      }
    };
  }

  private AnnotationManager addComponentManagerListenerToSetAnnotationManager() {
    return new AnnotationManager() {
      @Override
      public Action manageAnnotation(Element element, String annotationType) {
        // add the @Name annotation to non-public elements
        return isStateVarSetMethod(element, annotationType) ?
            Action.ADD :
            Action.VOID;
      }

      @Override
      public <T> T getAnnotationValue(Element element,
          String annotationType, String propertyName,
          Class<T> propertyClass, T defaultValue) {
        if (isStateVarSetMethod(element, annotationType)) {
          String stateVarName = getPropertyWithoutSetter(element);
          Symbol.TypeSymbol parentElement = getParentElement(Symbol.TypeSymbol.class);
          String fqn = parentElement.getQualifiedName().toString();
          if (fqn.endsWith("TOP")) {
            fqn = fqn.substring(0, fqn.length() - 3);
          }
          return propertyClass.cast(
              "{{body}}\n{{baseIndent}}ComponentObserverManager.callStateVariableObserver(\""
                  + stateVarName + "\", \"" + fqn + "\" + this.uniqueIdentifier);\n"
                  + "{{baseIndent}}this." + computeIsGetterFromSetter(element) +
                  GUIAdapterUtils.UPDATE_SUFFIX + "();");
        } else {
          return null;
        }
      }

      private boolean isStateVarSetMethod(Element element, String annotationType) {
        if (!(element.getKind().toString().equals("METHOD"))) {
          return false;
        }
        if (!JSweetConfig.ANNOTATION_REPLACE.equals(annotationType)) {
          return false;
        }
        boolean res = isSetterMethod(element);
        return res && adapterUtils.isMethodOfDirective(element);
      }

      private boolean isSetterMethod(Element element) {
        if (!element.getSimpleName().toString().startsWith("set")) {
          return false;
        }
        Symbol.TypeSymbol parentElement = Util.getElement(getPrinter().getParent(ClassTree.class));
        return parentElement.getEnclosedElements().stream()
            .anyMatch(m -> m.getSimpleName().toString().equals(getPropertyWithoutSetter(element)));
      }

      private String getPropertyWithoutSetter(Element element) {
        return GUIAdapterUtils.STATEVAR_PREFIX + StringUtils.uncapitalize(
            element.getSimpleName().toString().substring(3));
      }

      private String computeIsGetterFromSetter(Element element) {
        ExecutableElement method = (ExecutableElement) element;
        boolean isBooleanParameter =
            types().erasure(method.getParameters().get(0).asType()).toString().equals("boolean") ||
                types().erasure(method.getParameters().get(0).asType()).toString()
                    .equals("java.lang.Boolean");
        String substring = method.getSimpleName().toString().substring(3);
        return isBooleanParameter ? "is" + substring : "get" + substring;
      }
    };
  }
}
