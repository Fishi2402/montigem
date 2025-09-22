/* (c) https://github.com/MontiCore/monticore */
package domainAdapter;

import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.umlp.UMLPDomainImportAdapter;

public class RelativeDomainImportAdapter extends UMLPDomainImportAdapter {
  public RelativeDomainImportAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  protected void setDomainName() {
    addDomainName("relative");
  }
}

