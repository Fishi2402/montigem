/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.domainAdapters.ImportExcludeAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPBasicsAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPCollectionsAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPJavaBasicsAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPRTEImportAdapter;
import umlp.jsweet.extension.guiAdapters.BackendOnlyAdapter;
import umlp.jsweet.extension.guiAdapters.GUIComponentAdapter;
import umlp.jsweet.extension.guiAdapters.GUIImportAdapter;
import umlp.jsweet.extension.guiAdapters.GUIUtilAdapter;
import umlp.jsweet.extension.guiAdapters.TSTypesAdapter;

public class UMLPJSweetAdapter extends PrinterAdapter {
  public UMLPJSweetAdapter(PrinterAdapter parentAdapter) {
    super(
        new ImportExcludeAdapter(
            new UMLPCollectionsAdapter(
                new UMLPJavaBasicsAdapter(
                    new UMLPRTEImportAdapter(
                        new UMLPBasicsAdapter(
                            new GUIComponentAdapter(
                                new GUIImportAdapter(
                                    new TSTypesAdapter(
                                        new GUIUtilAdapter(
                                            new BackendOnlyAdapter(
                                                parentAdapter
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    );
  }
}
