/* (c) https://github.com/MontiCore/monticore */
package domainAdapter;

import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.umlp.UMLPDomainImportAdapter;

public class CommentModuleDomainImportAdapter extends UMLPDomainImportAdapter {
  public CommentModuleDomainImportAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  protected void setDomainName() {
    addDomainName("commentmodule");
  }
}

