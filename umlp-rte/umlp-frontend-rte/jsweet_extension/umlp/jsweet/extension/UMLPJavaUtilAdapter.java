/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.MethodInvocationElement;
import umlp.jsweet.extension.UMLPAbstractAdapter;

/**
 * This adapter is responsible to use some additional {@link java.util} classes.
 */
public class UMLPJavaUtilAdapter extends UMLPAbstractAdapter {

  private static final String UMLP_RTE_LIBRARY_POINTER = "umlp-frontend-rte";

  public UMLPJavaUtilAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  /**
   * Can be used to adapt method invocations like {@code Objects.requireNonNull(...)}
   */
  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    if (invocation.getTargetType() == null) {
      return super.substituteMethodInvocation(invocation);
    }
    // java.util.Objects
    if (invocation.getTargetType().toString().equals("java.util.Objects")) {
      // java.util.Objects.requireNonNull
      if (invocation.getMethodName().equals("requireNonNull")) {
        String msg = invocation.getArgumentCount() > 1 ? invocation.getArgument(1).toString() : "";
        print("/* requireNonNull */")
          .print(String.format(
            "(obj => {if (obj == undefined || obj == null) { throw Object.defineProperty(new Error(%s), '__classes', { configurable: true, value: ['java.lang.Throwable','java.lang.Object','java.lang.RuntimeException','java.lang.NullPointerException','java.lang.Exception'] }) } else { return obj}}) (%s)",
            msg, invocation.getArgument(0).toString()));
        return true;
      }
      // java.util.Objects.equals
      if (invocation.getMethodName().equals("equals")) {
        print("/* equals */")
          .print(
            String.format("((a, b) => { return a === b; }) (%s, %s)",
              invocation.getArgument(0), invocation.getArgument(1)));
        return true;
      }
    }
    return super.substituteMethodInvocation(invocation);
  }

  @Override
  protected void initStaticLibary() {
    staticLibrary.add("java.util.concurrent.atomic.AtomicReference");
    staticLibrary.add("java.util.Random");
  }

  @Override
  String getLibraryLocation() {
    return UMLP_RTE_LIBRARY_POINTER;
  }
}
