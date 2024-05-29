<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "domName")}

/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import org.jsweet.transpiler.extension.PrinterAdapter;

public class ${className} extends DomainImportAdapter {
  public ${className}(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  protected void setDomainName() {
    addDomainName("${domName?lower_case}");
  }
}

