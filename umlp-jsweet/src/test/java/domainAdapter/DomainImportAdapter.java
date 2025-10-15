/* (c) https://github.com/MontiCore/monticore */
package domainAdapter;

import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.umlp.UMLPDomainImportAdapter;

public class DomainImportAdapter extends UMLPDomainImportAdapter {
  public DomainImportAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  protected void setDomainName() {
    addDomainName("domain");
  }
}

