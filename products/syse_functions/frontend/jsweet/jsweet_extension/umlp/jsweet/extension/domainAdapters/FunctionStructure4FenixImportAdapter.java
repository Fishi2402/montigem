/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import org.jsweet.transpiler.extension.PrinterAdapter;

public class FunctionStructure4FenixImportAdapter extends FunctionStructure4FenixImportAdapterTOP {
  public FunctionStructure4FenixImportAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  protected void setDomainName() {
    addDomainName("functionstructure4fenix");
  }
}
