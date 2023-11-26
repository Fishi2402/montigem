/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import org.jsweet.transpiler.extension.PrinterAdapter;

public class DTCockpitImportAdapter extends DomainImportAdapter {
  public DTCockpitImportAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  protected String getDomainName() {
    return "dtcockpit";
  }
}
