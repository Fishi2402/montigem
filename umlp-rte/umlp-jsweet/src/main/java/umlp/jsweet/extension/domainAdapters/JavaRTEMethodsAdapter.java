package umlp.jsweet.extension.domainAdapters;

import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.MethodInvocationElement;
import umlp.jsweet.extension.UMLPAbstractAdapter;

/**
 * This adapter replaces missing Java RTE calls, like String#isBlank
 */
public class JavaRTEMethodsAdapter extends UMLPAbstractAdapter {

  public JavaRTEMethodsAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    if (invocation.getTargetType() == null) {
      return super.substituteMethodInvocation(invocation);
    }

    String targetType = invocation.getTargetType().toString();
    String methodName = invocation.getMethodName();
    switch (targetType + "#" + methodName){
      case "java.lang.String#isBlank": {
        print("(");
        print(invocation.getTargetExpression());
        print(".trim().length === 0)");
        return true;
      }
    }

    return super.substituteMethodInvocation(invocation);
  }
}
