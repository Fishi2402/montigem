/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.MethodInvocationElement;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * This adapter is responsible to enable the usage of some additional {@link java.util} classes.
 */
public class UMLPJavaBasicsAdapter extends UMLPAbstractAdapter {

  public UMLPJavaBasicsAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    initTypeMappings();
  }

  private void initTypeMappings() {
    getTypeMirrorMappings().add(0, computeJavaOptionalTypeMapping());
    getTypeMirrorMappings().add(0, computeDateTypeMapping());
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

  public Function<TypeMirror, String> computeDateTypeMapping() {
    return (TypeMirror type) -> {
      switch (type.toString()) {
        case "java.time.LocalDate":
        case "java.util.Date":
          return "Date";
      }
      return null;
    };
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

    return super.substituteMethodInvocation(invocation);
  }

  public boolean substituteMethodInvocation_JavaUtilObjects(MethodInvocationElement invocation) {
    switch (invocation.getMethodName()) {
      case "requireNonNull":
        String msg = invocation.getArgumentCount() > 1 ? invocation.getArgument(1).toString() : "";
        this.print("/* requireNonNull */")
            .print(String.format(
                "(obj => {if (obj == undefined || obj == null) { throw Object.defineProperty(new Error(%s), '__classes', { configurable: true, value: ['java.lang.Throwable','java.lang.Object','java.lang.RuntimeException','java.lang.NullPointerException','java.lang.Exception'] }) } else { return obj}}) (%s)",
                msg, invocation.getArgument(0).toString()));
        return true;
      case "equals":
        this.print("/* equals */")
            .print(
                String.format("((a, b) => { return a === b; }) (%s, %s)",
                    invocation.getArgument(0), invocation.getArgument(1)));
        return true;
    }
    return false;
  }

  public boolean substituteMethodInvocation_JavaLangString(MethodInvocationElement invocation) {
    switch (invocation.getMethodName()) {
      case "format":
        if (invocation.getMethodName().equals("format")) {
          this.print("StringHelper.format(").printArgList(invocation.getArguments()).print(")");
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
