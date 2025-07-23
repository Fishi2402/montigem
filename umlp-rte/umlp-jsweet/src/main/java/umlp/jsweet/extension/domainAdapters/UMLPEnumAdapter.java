/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import de.se_rwth.commons.logging.Log;
import org.jsweet.transpiler.Java2TypeScriptTranslator;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.MethodInvocationElement;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;

public class UMLPEnumAdapter extends PrinterAdapter {
  public UMLPEnumAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocationElement) {
    if (invocationElement == null || invocationElement.getTargetType() == null) {
      return super.substituteMethodInvocation(invocationElement);
    }

    Element targetTypeElement = types().asElement(invocationElement.getTargetType());

    if (targetTypeElement != null && targetTypeElement.getKind() == ElementKind.ENUM
        && (invocationElement.getTargetExpression() != null
        && !"this".equals(invocationElement.getTargetExpression().toString()))) {
      String targetMethodName = invocationElement.getMethodName();
      String relTarget = getRootRelativeName(targetTypeElement);
      switch (targetMethodName) {
        case "toString":
          printMacroName("Enum." + targetMethodName);
          print("(" + relTarget + "[").print(invocationElement.getTargetExpression())
              .print("] ? ")
              .print(relTarget + "[").print(invocationElement.getTargetExpression())
              .print("]")
              .print(" : '')");
          return true;
      }
    }
    return super.substituteMethodInvocation(invocationElement);
  }
}
