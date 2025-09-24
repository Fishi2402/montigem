<#-- (c) https://github.com/MontiCore/monticore -->
${signature("initializationTypeName", "domName")}

/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import org.jsweet.transpiler.extension.PrinterAdapter;

public class ${initializationTypeName} extends UMLPDomainImportAdapter {
  public ${initializationTypeName}(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  protected void setDomainName() {
    addDomainName("${domName?lower_case}");
  }
}

