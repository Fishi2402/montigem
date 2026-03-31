/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.umlp;

import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.ExtendedElementFactory;
import org.jsweet.transpiler.model.MethodInvocationElement;
import standalone.com.sun.source.tree.LambdaExpressionTree;
import standalone.com.sun.source.tree.Tree;
import umlp.jsweet.extension.AbstractAdapter;

import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * This adapter is responsible to enable the usage of some additional {@link java.util} classes.
 */
public class JavaBasicsAdapter extends AbstractAdapter {

  public JavaBasicsAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    initTypeMappings();
  }

  private void initTypeMappings() {
    getTypeMirrorMappings().add(0, computeJavaOptionalTypeMapping());
  }

  public Function<TypeMirror, String> computeJavaOptionalTypeMapping() {
    return (TypeMirror type) -> {
      TypeMirror mappedType = mapOptionalType(type);
      if (mappedType != null) {
        return this.getMappedType(mappedType);
      }
      return null;
    };
  }

  public TypeMirror mapOptionalType(TypeMirror type) {
    if (this.types().isSubtype(this.types().erasure(type), this.types().erasure(
        this.util().getType(Optional.class)))
        && type instanceof DeclaredType) {
      List<? extends TypeMirror> typeArgs = ((DeclaredType) type).getTypeArguments();
      if (typeArgs.size() > 0) {
        TypeMirror backingType = typeArgs.get(0);
        if (backingType.getKind() == TypeKind.DECLARED) {
          return backingType;
        }
      }
    }
    return null;
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
      case "java.lang.String":
        if (substituteMethodInvocation_JavaLangString(invocation)) {
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
      switch (targetClassName) {
        case "java.util.Optional":
          if (substituteMethodInvocation_Optional(invocation)) {
            return true;
          }
      }
    }

    return super.substituteMethodInvocation(invocation);
  }

  private boolean substituteMethodInvocation_Optional(MethodInvocationElement invocation) {
    switch (invocation.getMethodName()) {
      case "or":
        Tree argumentTree = ExtendedElementFactory.toTree(invocation.getArgument(0));
        boolean isAsync = argumentTree instanceof LambdaExpressionTree &&
            context.isAsyncLambda((LambdaExpressionTree) argumentTree);
        printMacroName(invocation.getMethodName());
        if (isAsync) {
          print("(await ");
        }
        print("((v, get) => v == null ? get() : v)(").print(invocation.getTargetExpression())
            .print(",");
        print(invocation.getArgument(0)).print(")");
        if (isAsync) {
          print(")");
        }
        return true;
    }
    return false;
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
          this.print("/* equals */")
              .print("((a, b) => { return a === b; }) (")
              .printArgList(invocation.getArguments())
              .print(")");
        }
        return true;
    }
    return false;
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
    return mapOptionalType(type);
  }

  @Override
  protected void initStaticLibrary() {
    addToStaticLibrary("java.util.concurrent.atomic.AtomicReference");
    addToStaticLibrary("java.util.Random");
    putToStaticLibrary("java.lang.String", "StringHelper");
  }
}
