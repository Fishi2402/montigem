/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import standalone.com.sun.source.tree.EnhancedForLoopTree;
import standalone.com.sun.source.tree.ExpressionTree;
import standalone.com.sun.source.tree.MemberSelectTree;
import standalone.com.sun.source.tree.MethodInvocationTree;
import standalone.com.sun.source.tree.TypeCastTree;
import de.se_rwth.commons.logging.Log;
import org.jsweet.transpiler.Java2TypeScriptTranslator;
import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ExtendedElementFactory;
import org.jsweet.transpiler.model.ForeachLoopElement;
import org.jsweet.transpiler.model.LiteralElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.model.NewClassElement;
import org.jsweet.transpiler.util.Util;
import umlp.jsweet.extension.UMLPAbstractAdapter;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class UMLPCollectionsAdapter extends UMLPAbstractAdapter {
  public static final String CustomUMLPListInterfaceName = "IUMLPList";

  public static final String CustomUMLPListImplementationName = "UMLPArrayList";

  public UMLPCollectionsAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    super.addTypeMappings(getStaticLibrary());
    getTypeMirrorMappings().add(0, computeUMLPGuiArrayTypeMapping());
  }

  public Function<TypeMirror, String> computeUMLPGuiArrayTypeMapping() {
    return (TypeMirror type) -> {
      if (type.toString().equals("umlp.guiutil.UMLPGuiArray")) {
        return "Array";
      }
      return null;
    };
  }

  @Override
  protected void initStaticLibrary() {
    putToStaticLibrary(List.class.getName(), CustomUMLPListInterfaceName);
    putToStaticLibrary(AbstractList.class.getName(), CustomUMLPListInterfaceName);
    putToStaticLibrary(ArrayList.class.getName(), CustomUMLPListImplementationName);
    putToStaticLibrary(CopyOnWriteArrayList.class.getName(), CustomUMLPListImplementationName);
    putToStaticLibrary(LinkedList.class.getName(), CustomUMLPListImplementationName);
    putToStaticLibrary(Stack.class.getName(), CustomUMLPListImplementationName);
    putToStaticLibrary(Vector.class.getName(), CustomUMLPListImplementationName);
  }

  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    String targetMethodName = invocation.getMethodName();
    String targetClassName = invocation.getMethod().getEnclosingElement().toString();
    ExtendedElement targetExpression = invocation.getTargetExpression();
    if (targetExpression != null && targetExpression.getTypeAsElement() != null) {
      targetClassName = targetExpression.getTypeAsElement().toString();
    }
    TypeMirror jdkSuperclass = getContext().getJdkSuperclass(targetClassName, new HashSet<>());
    boolean delegate = jdkSuperclass != null;
    if (delegate) {
      targetClassName = jdkSuperclass.toString();
    }

    if (targetClassName != null
        && (targetExpression != null || invocation.getMethod().getModifiers().contains(
        Modifier.STATIC))) {
      switch (targetClassName) {
        case "java.util.List":
        case "java.util.AbstractList":
        case "java.util.LinkedList":
        case "java.util.ArrayList":
        case "java.util.concurrent.CopyOnWriteArrayList":
        case "java.util.Stack":
        case "java.util.Vector":
        case "umlp.backendrte.common.GemList":
          return substituteCommonListInvocations(invocation, targetMethodName);
        case "java.util.Collections":
          if (substituteJavaCollectionsInvocations(invocation, targetMethodName))
            return true;
        case "com.google.common.collect.Lists":
          if (substituteCommonListsInvocations(invocation, targetMethodName))
            return true;
        case "java.util.Arrays":
          if (substituteUtilArraysInvocations(invocation, targetMethodName))
            return true;
        case "com.google.common.collect.Sets":
          if (substituteSetsInvocations(invocation, targetMethodName))
            return true;
        case "umlp.guiutil.UMLPGuiArray":
          if (substituteUMLPGuiArrayInvocations(invocation, targetMethodName))
            return true;
      }
    }

    return super.substituteMethodInvocation(invocation);
  }

  private boolean substituteUMLPGuiArrayInvocations(MethodInvocationElement invocation,
      String targetMethodName) {
    switch (targetMethodName) {
      case "set":
        print(invocation.getTargetExpression()).print("[").print(invocation.getArgument(0)).print("] = ").print(invocation.getArgument(1));
        return true;
      case "get":
        print(invocation.getTargetExpression()).print("[").print(invocation.getArgument(0)).print("]");
        return true;
    }
    return false;
  }

  private boolean substituteSetsInvocations(MethodInvocationElement invocation, String targetMethodName) {
    switch (targetMethodName) {
      case "newHashSet":
        print("[").printArgList(invocation.getArguments()).print("]");
        return true;
    }
    return false;
  }

  private boolean substituteUtilArraysInvocations(MethodInvocationElement invocation, String targetMethodName) {
    switch (targetMethodName) {
      case "asList": {
        printMacroName(targetMethodName);
        print("new ").print(UMLPCollectionsAdapter.CustomUMLPListImplementationName).print("([")
            .printArgList(invocation.getArguments()).print("])");
        getPrinter().useModule(new ModuleImportDescriptor("UMLPArrayList", getLibraryLocation()));
        return true;
      }
    }
    return false;
  }

  private boolean substituteCommonListsInvocations(MethodInvocationElement invocation, String targetMethodName) {
    switch (targetMethodName) {
      case "newArrayList":
        String msg = invocation.getArgumentCount() > 1 ? invocation.getArgument(1).toString() : "";
        print("new " + UMLPCollectionsAdapter.CustomUMLPListImplementationName + "(" + msg + ")");
        getPrinter().useModule(new ModuleImportDescriptor("UMLPArrayList", getLibraryLocation()));
        return true;
    }
    return false;
  }

  private boolean substituteJavaCollectionsInvocations(MethodInvocationElement invocation, String targetMethodName) {
    switch (targetMethodName) {
      case "emptyList":
        printMacroName(targetMethodName);
        print("new " + UMLPCollectionsAdapter.CustomUMLPListImplementationName + "()");
        getPrinter().useModule(new ModuleImportDescriptor("UMLPArrayList", getLibraryLocation()));
        return true;
      //        case "emptySet":
      //          printMacroName(adapter, targetMethodName);
      //          adapter.print("new ArrayList()");
      //          return true;
      case "unmodifiableList":
      case "unmodifiableCollection":
        //        case "unmodifiableSet":
        //        case "unmodifiableSortedSet":
        printMacroName(targetMethodName);
        print("new " + UMLPCollectionsAdapter.CustomUMLPListImplementationName + "(")
            .printArgList(invocation.getArguments()).print(")");
        getPrinter().useModule(new ModuleImportDescriptor("UMLPArrayList", getLibraryLocation()));
        return true;
      //        case "singleton":
      //          printMacroName(adapter, targetMethodName);
      //          adapter.print("new ArrayList([").print(invocation.getArgument(0)).print("])");
      //          return true;
      case "singletonList":
        printMacroName(targetMethodName);
        print("new " + UMLPCollectionsAdapter.CustomUMLPListImplementationName + "([")
            .print(invocation.getArgument(0)).print("])");
        getPrinter().useModule(new ModuleImportDescriptor("UMLPArrayList", getLibraryLocation()));
        return true;
    }
    return false;
  }

  private boolean substituteCommonListInvocations(MethodInvocationElement invocation, String targetMethodName) {
    switch (targetMethodName) {
      case "remove":
        substituteRemoveMethod(invocation);
        return true;
      default:
        getPrinter().printDefaultMethodInvocation(ExtendedElementFactory.toTree(invocation));
        return true;
    }
  }

  private void substituteRemoveMethod(MethodInvocationElement invocation) {
    MethodInvocationTree methodInvocationTree = ExtendedElementFactory.toTree(invocation);
    if (methodInvocationTree.getMethodSelect() instanceof MemberSelectTree) {
      ExecutableType type = Util.getType(
          methodInvocationTree.getMethodSelect()) instanceof ExecutableType
          ? Util.getType(methodInvocationTree.getMethodSelect())
          : null;
      ExpressionTree selected = ((MemberSelectTree) methodInvocationTree.getMethodSelect()).getExpression();
      ExecutableElement methSym = util().findMethodDeclarationInType(
          Util.getTypeElement(selected), "remove", type);

      if (type.getReturnType().toString().equals("boolean")) {
        getContext().addMethodNameMapping(methSym, "removeElement");
      } else {
        getContext().addMethodNameMapping(methSym, "removeIndex");
      }
    }
    getPrinter().printDefaultMethodInvocation(methodInvocationTree);
  }

  @Override
  public boolean substituteNewClass(NewClassElement newClass) {
    switch (newClass.getTypeAsElement().toString()) {
      case "java.util.List":
      case "java.util.AbstractList":
      case "java.util.LinkedList":
      case "java.util.ArrayList":
      case "java.util.concurrent.CopyOnWriteArrayList":
      case "java.util.Stack":
      case "java.util.Vector":
        if (newClass.getArgumentCount() == 0) {
          print("new " + CustomUMLPListImplementationName + "()");
        } else {
          if (util().isNumber(newClass.getArgument(0).getType())
              || (newClass.getArgument(0) instanceof LiteralElement)) {
            print("new " + CustomUMLPListImplementationName + "()");
          } else {
            print("new " + CustomUMLPListImplementationName + "(")
                .print(newClass.getArgument(0)).print(")");
          }
        }
        getPrinter().useModule(new ModuleImportDescriptor("UMLPArrayList", getLibraryLocation()));
        return true;
      case "umlp.guiutil.UMLPGuiArray":
        if (newClass.getArgumentCount() == 0) {
          print("[]");
        } else if (newClass.getArgumentCount() == 1){
          printArgList(newClass.getArguments()).print(".toArray()");
        } else {
          Log.warn("Did not implement new UMLPGuiArray with more than one parameters");
        }
        return true;
    }

//    if (UMLPAdapterUtils.isSubTypeOf(newClass.getType(), "umlp.backendrte.common.GemListBuilder",
//        util(), types(), context)) {
//      print("(await new ").print(newClass.getConstructorAccess()).print("(")
//          .printArgList(newClass.getArguments()).print(").finalizeConstructor())");
//      return true;
//    }

    return super.substituteNewClass(newClass);
  }

  @Override
  public boolean substituteForEachLoop(ForeachLoopElement foreachLoop, boolean targetHasLength, String indexVarName) {
    EnhancedForLoopTree loop = ExtendedElementFactory.toTree(foreachLoop);
    TypeMirror expressionType = Util.getType(loop.getExpression());
    if (types().isSubtype(expressionType, types().erasure(util().getType(List.class)))) {
      printAwaitForEachLoop(loop, indexVarName);
      return true;
    }
    return super.substituteForEachLoop(foreachLoop, targetHasLength, indexVarName);
  }

  protected void printAwaitForEachLoop(EnhancedForLoopTree loop, String indexVarName) {
    print("for await(" + Java2TypeScriptTranslator.VAR_DECL_KEYWORD + " ")
        .print(loop.getVariable().getName().toString() + " of ");
    boolean parentheses = loop.getExpression() instanceof TypeCastTree
        || loop.getExpression() instanceof MethodInvocationTree;
    if (parentheses) { print("("); }
    print(loop.getExpression());
    if (parentheses) { print(")"); }
    getPrinter().print(") {").println().startIndent().printIndent()
        .printIndent().print(loop.getStatement())
        .endIndent().println().printIndent().print("}");
  }

  @Override
  public Java2TypeScriptTranslator getPrinter() {
    if (super.getPrinter() instanceof Java2TypeScriptTranslator) {
      return (Java2TypeScriptTranslator) super.getPrinter();
    } else {
      Log.error("0JS0001 Java2TypeScriptTranslater must be core Adapter");
      return null;
    }
  }

  @Override
  public boolean substituteInstanceof(String exprStr, ExtendedElement expr, TypeMirror type) {
    String typeString = types().erasure(type).toString();
    if (containedInStaticLibrary(typeString)) {
      print(exprStr, expr);
      print(" != null && ");
      print("(");
      print(exprStr, expr);
      print(" instanceof UMLPArrayList || ");
      print(exprStr, expr);
      print(" instanceof GemList || ");
      print(exprStr, expr);
      print(" instanceof Array)");

      getPrinter().useModule(new ModuleImportDescriptor("GemList", getLibraryLocation()));
      getPrinter().useModule(new ModuleImportDescriptor("UMLPArrayList", getLibraryLocation()));
      return true;
    }
    return super.substituteInstanceof(exprStr, expr, type);
  }
}
