/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import org.jsweet.transpiler.extension.PrinterAdapter;

public class DefaultImportAdapter extends UMLPDomainImportAdapter {

  public DefaultImportAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  protected void setDomainName() {}
}
