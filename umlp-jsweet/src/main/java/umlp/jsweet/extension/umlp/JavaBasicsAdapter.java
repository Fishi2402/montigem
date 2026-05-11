/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.umlp;

import org.jsweet.JSweetConfig;
import org.jsweet.transpiler.JSweetProblem;
import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ExtendedElementFactory;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.util.Util;
import standalone.com.sun.source.tree.ExpressionTree;
import standalone.com.sun.source.tree.LambdaExpressionTree;
import standalone.com.sun.source.tree.MemberSelectTree;
import standalone.com.sun.source.tree.MethodInvocationTree;
import standalone.com.sun.source.tree.MethodTree;
import standalone.com.sun.source.tree.ParenthesizedTree;
import standalone.com.sun.source.tree.Tree;
import standalone.com.sun.source.tree.TypeCastTree;
import umlp.jsweet.extension.AbstractAdapter;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * This adapter is responsible to enable the usage of some additional {@link java.util} classes.
 */
public class JavaBasicsAdapter extends AbstractAdapter {

  public JavaBasicsAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    initTypeMappings();
  }

  private void initTypeMappings() {
  }

  /**
   * Can be used to adapt method invocations like {@code Objects.requireNonNull(...)}
   */
  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    if (invocation.getTargetType() == null) {
      return super.substituteMethodInvocation(invocation);
    }
    String targetType = invocation.getTargetType().toString();
    switch (targetType) {
      case "java.util.Objects":
        if (substituteMethodInvocation_JavaUtilObjects(invocation)) {
          return true;
        }
        break;
      case "java.util.Arrays":
        if (substituteMethodInvocation_JavaUtilArrays(invocation)) {
          getPrinter().useModule(new ModuleImportDescriptor("Arrays", getLibraryLocation()));
          return true;
        }
        break;
      case "java.lang.String":
        if (substituteMethodInvocation_JavaLangString(invocation)) {
          return true;
        }
        break;
      default:
        if (isJavaUtilFunctionSupplier(invocation, targetType)
            && substituteMethodInvocation_JavaUtilFunctionSupplier(invocation)) {
          return true;
        }
        break;
    }

    String targetClassName = invocation.getMethod().getEnclosingElement().toString();
    ExtendedElement targetExpression = invocation.getTargetExpression();
    if (targetExpression != null && targetExpression.getTypeAsElement() != null) {
      targetClassName = targetExpression.getTypeAsElement().toString();
    }
    if (targetClassName != null
        && (targetExpression != null || invocation.getMethod().getModifiers().contains(
        Modifier.STATIC))) {
    }

    return super.substituteMethodInvocation(invocation);
  }

  private boolean isJavaUtilFunctionSupplier(MethodInvocationElement invocation, String targetType) {
    return targetType.startsWith("java.util.function.Supplier")
        || invocation.getMethod().getEnclosingElement().toString()
        .startsWith("java.util.function.Supplier");
  }

  public boolean substituteMethodInvocation_JavaUtilFunctionSupplier(MethodInvocationElement invocation) {
    switch (invocation.getMethodName()) {
      case "get":
        if (canUseAwaitInCurrentExpression()) {
          MethodInvocationTree methodInvocationTree = ExtendedElementFactory.toTree(invocation);
          LambdaExpressionTree supplierLambda = getSupplierLambda(methodInvocationTree);
          if (supplierLambda != null) {
            this.print("(await (");
            if (!context.isAsyncLambda(supplierLambda)) {
              this.print("async ");
            }
            this.print(supplierLambda);
            this.print(")())");
          }
          else {
            this.print("(await ");
            getPrinter().printDefaultMethodInvocation(methodInvocationTree);
            this.print(")");
          }
          return true;
        }
        break;
    }
    return false;
  }

  private boolean canUseAwaitInCurrentExpression() {
    LambdaExpressionTree lambda = getPrinter().getParent(LambdaExpressionTree.class);
    return isInAsyncMethod() && (lambda == null || context.isAsyncLambda(lambda) || isSupplierLambda(lambda));
  }

  private LambdaExpressionTree getSupplierLambda(MethodInvocationTree invocation) {
    if (!(invocation.getMethodSelect() instanceof MemberSelectTree)) {
      return null;
    }
    ExpressionTree target = ((MemberSelectTree) invocation.getMethodSelect()).getExpression();
    Tree unwrapped = unwrapSupplierTarget(target);
    return unwrapped instanceof LambdaExpressionTree ? (LambdaExpressionTree) unwrapped : null;
  }

  private Tree unwrapSupplierTarget(Tree tree) {
    while (tree instanceof ParenthesizedTree || tree instanceof TypeCastTree) {
      if (tree instanceof ParenthesizedTree) {
        tree = ((ParenthesizedTree) tree).getExpression();
      }
      else {
        tree = ((TypeCastTree) tree).getExpression();
      }
    }
    return tree;
  }

  private boolean isSupplierLambda(LambdaExpressionTree lambda) {
    TypeMirror type = Util.getType(lambda);
    return type != null && type.toString().startsWith("java.util.function.Supplier");
  }

  public boolean substituteMethodInvocation_JavaUtilObjects(MethodInvocationElement invocation) {
    switch (invocation.getMethodName()) {
      case "requireNonNull":
        String msg = invocation.getArgumentCount() > 1 ? invocation.getArgument(1).toString() : "";
        this.print("/* requireNonNull */")
            .print(String.format(
                "(obj => {if (obj == undefined || obj == null) { throw Object.defineProperty(new Error(%s), '__classes', { configurable: true, value: ['java.lang.Throwable','java.lang.Object','java.lang.RuntimeException','java.lang.NullPointerException','java.lang.Exception'] }) } else { return obj}}) (",
                msg))
            .print(invocation.getArgument(0))
            .print(")");
        return true;
      case "equals":
        boolean isDate = false;
        for (ExtendedElement argument : invocation.getArguments()) {
          switch (argument.getType().toString()) {
            case "java.time.LocalDateTime":
            case "java.time.LocalDate":
            case "java.util.Date":
              isDate = true;
          }
        }
        if (isDate) {
          this.print("/* equals */")
              .print("((a, b) => { return a.getTime() === b.getTime(); }) (")
              .printArgList(invocation.getArguments())
              .print(")");
        }
        else {
          getPrinter().useModule(new ModuleImportDescriptor("JavaUtilUtils", getLibraryLocation()));
          boolean inAsyncMethod = isInAsyncMethod();
          this.print("/* equals */");
          if (inAsyncMethod) {
            this.print("await ");
          }
          this.print(inAsyncMethod ? "JavaUtilUtils.equals(" : "JavaUtilUtils.equalsSync(")
              .printArgList(invocation.getArguments())
              .print(")");
        }
        return true;
    }
    return false;
  }

  protected boolean isInAsyncMethod() {
    MethodTree methodTree = getPrinter().getParent(MethodTree.class);
    if (methodTree == null) {
      return false;
    }
    Element methodElement = Util.getElement(methodTree);
    return methodElement != null && context.hasAnnotationType(methodElement, JSweetConfig.ANNOTATION_ASYNC);
  }

  public boolean substituteMethodInvocation_JavaUtilArrays(MethodInvocationElement invocation) {
    switch (invocation.getMethodName()) {
      case "asList": {
        if(invocation.getArgumentCount() == 1 && invocation.getArgument(0).getType().getKind() == TypeKind.ARRAY) {
          // emulate Java varargs
          this.print("Arrays.asList(...").print(invocation.getArgument(0)).print(")");
          return true;
        } else {
          this.print("Arrays.asList(").printArgList(invocation.getArguments()).print(")");
          return true;
        }
      }
      case "copyOf": {
        if (invocation.getArgumentCount() == 2) {
          this.print("Arrays.copyOf(").printArgList(invocation.getArguments()).print(")");
          return true;
        } else {
          report(invocation, JSweetProblem.USER_ERROR, "Signature not supported, use copyOf(T[], int) instead.");
          return false;
        }
      }
      case "copyOfRange": {
        if (invocation.getArgumentCount() == 3) {
          this.print("Arrays.copyOfRange(").printArgList(invocation.getArguments()).print(")");
          return true;
        } else {
          report(invocation, JSweetProblem.USER_ERROR, "Signature not supported, use copyOfRange(T[], int, int) instead.");
          return false;
        }
      }
      case "fill": {
        if (invocation.getArgumentCount() == 2) {
          this.print("Arrays.fill(").printArgList(invocation.getArguments()).print(")");
          return true;
        } else if (invocation.getArgumentCount() == 4) {
          this.print("Arrays.fillRange(").printArgList(invocation.getArguments()).print(")");
          return true;
        } else {
          report(invocation, JSweetProblem.USER_ERROR, "Parameter count mismatch.");
          return false;
        }
      }
      case "sort": {
        if (invocation.getArgumentCount() == 1) {
          this.print("Arrays.sort(").printArgList(invocation.getArguments()).print(")");
          return true;
        } else {
          report(invocation, JSweetProblem.USER_ERROR, "Signature not supported, use sort(T[])");
          return false;
        }
      }
      case "stream": {
        if (invocation.getArgumentCount() == 1) {
          this.print("Arrays.stream(").printArgList(invocation.getArguments()).print(")");
          return true;
        } else if (invocation.getArgumentCount() == 3) {
          this.print("Arrays.streamRange(").printArgList(invocation.getArguments()).print(")");
          return true;
        } else {
          report(invocation, JSweetProblem.USER_ERROR, "Parameter count mismatch.");
          return false;
        }
      }
      case "toString": {
        this.print("Arrays.toString(").printArgList(invocation.getArguments()).print(")");
        return true;
      }
      default: {
        report(invocation, JSweetProblem.USER_ERROR, "Unsupported method.");
        return false;
      }
    }
  }

  public boolean substituteMethodInvocation_JavaLangString(MethodInvocationElement invocation) {
    switch (invocation.getMethodName()) {
      case "format":
        this.print("StringHelper.format(").printArgList(invocation.getArguments()).print(")");
        return true;
      case "isBlank": {
        print("(");
        print(invocation.getTargetExpression());
        print(".trim().length === 0)");
        return true;
      }
      case "split": {
        print("StringHelper.split(");
        print(invocation.getTargetExpression());
        print(", ");
        printArgList(invocation.getArguments());
        print(")");
        return true;
      }
    }
    return false;
  }

  @Override
  public TypeMirror mapType(TypeMirror type) {
    return null;
  }

  @Override
  protected void initStaticLibrary() {
    addToStaticLibrary("java.util.concurrent.atomic.AtomicReference");
    addToStaticLibrary("java.util.Random");
    putToStaticLibrary("java.lang.String", "StringHelper");
  }
}
