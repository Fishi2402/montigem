/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.backendAdapters;

import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.ExtendedElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.model.NewClassElement;
import umlp.jsweet.extension.UMLPAdapterUtils;

public class UMLPBasicsAdapter extends UMLPAbstractAdapter {

  public UMLPBasicsAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    initTypeMappings();
  }

  private void initTypeMappings() {
    getTypeMirrorMappings().add(0, UMLPAdapterUtils.computeMCJSonTypeMapping(this, types()));
  }


  @Override
  protected void initStaticLibrary() {
    addToStaticLibrary("de.se_rwth.commons.logging.Log");
    addToStaticLibrary("de.monticore.symboltable.serialization.JsonPrinter");
  }

  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    if (invocation.getTargetType() == null) {
      return super.substituteMethodInvocation(invocation);
    }
    if (UMLPAdapterUtils.substituteMethodInvocation_MCJson(this, invocation)) {
      return true;
    }
    return super.substituteMethodInvocation(invocation);
  }

  @Override
  public boolean substituteNewClass(NewClassElement newClass) {
    String className = newClass.getTypeAsElement().toString();
    switch (className) {
      case "umlp.backendrte.command.SimpleResult":
        if (newClass.getArguments().size() == 3) {
          ExtendedElement argument = newClass.getArgument(1);
          if (argument.getTypeAsElement().toString().equals("umlp.backendrte.common.LoadingState")) {
            print("new SimpleResult().createFromLoadingState(").printArgList(newClass.getArguments()).print(")");
            return true;
          }
        }
    }

    return super.substituteNewClass(newClass);
  }
}
