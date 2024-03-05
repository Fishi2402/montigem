<#-- (c) https://github.com/MontiCore/monticore -->
${signature("domName")}

/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import org.jsweet.transpiler.extension.PrinterAdapter;

public class ${domName}ImportAdapter extends DomainImportAdapter {
  public ${domName}ImportAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  protected void setDomainName() {
    addDomainName("${domName?lower_case}", "security");
  }
}

