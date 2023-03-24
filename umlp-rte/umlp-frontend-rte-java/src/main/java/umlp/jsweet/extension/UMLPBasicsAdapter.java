/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.MethodInvocationElement;

  public class UMLPBasicsAdapter extends UMLPAbstractAdapter{

  public UMLPBasicsAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    initTypeMappings();
  }

  private void initTypeMappings() {
    getTypeMirrorMappings().add(0, UMLPAdapterUtils.computeMCJSonTypeMapping(this, types()));
  }

  @Override
  String getLibraryLocation() {
    return UMLPAdapterUtils.UMLP_RTE_LIBRARY_POINTER;
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
}
