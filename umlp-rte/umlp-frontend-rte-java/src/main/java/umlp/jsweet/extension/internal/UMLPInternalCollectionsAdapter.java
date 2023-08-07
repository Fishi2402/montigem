/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.internal;

import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.backendAdapters.UMLPCollectionsAdapter;

public class UMLPInternalCollectionsAdapter extends UMLPCollectionsAdapter {
  public UMLPInternalCollectionsAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  protected String getLibraryLocation() {
    return UMLPInternalAdapter.UMLP_RTE_HWC_POINTER + "/common";
  }
}
