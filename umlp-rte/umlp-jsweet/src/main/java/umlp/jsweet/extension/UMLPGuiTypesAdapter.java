/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPBasicsAdapter;
import umlp.jsweet.extension.guiTypesAdapters.UMLPJavaBasicsAdapter;
import umlp.jsweet.extension.guiTypesAdapters.UMLPRTEImportAdapter;

public class UMLPGuiTypesAdapter extends PrinterAdapter {
  public UMLPGuiTypesAdapter(PrinterAdapter parentAdapter) {
    super(
        new UMLPRTEImportAdapter(
            new UMLPJavaBasicsAdapter(
                new UMLPBasicsAdapter(
                    parentAdapter
                )
            )
        )
    );
  }
}
