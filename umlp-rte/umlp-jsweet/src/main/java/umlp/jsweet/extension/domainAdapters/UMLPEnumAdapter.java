/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import de.se_rwth.commons.logging.Log;
import org.jsweet.transpiler.Java2TypeScriptTranslator;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.MethodInvocationElement;
import umlp.jsweet.extension.util.UMLPAdapterUtils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;

public class UMLPEnumAdapter extends PrinterAdapter {

  UMLPAdapterUtils adapterUtils;

  public UMLPEnumAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    this.adapterUtils = new UMLPAdapterUtils(this, types(), context, util());
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
      String path = util().getQualifiedName(targetTypeElement).replace(".", "/");
      adapterUtils.importFromWithModule(relTarget, path);
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
