

/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import org.jsweet.transpiler.extension.PrinterAdapter;

public class CarrentalImportAdapter extends DomainImportAdapter {
  public CarrentalImportAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  protected void setDomainName() {
    addDomainName("carrental", "security");
  }
}

