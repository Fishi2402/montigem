/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.domainAdapters.ImportExcludeAdapter;
import umlp.jsweet.extension.domainAdapters.SynchronizedAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPBasicsAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPJavaBasicsAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPRTEImportAdapter;
import umlp.jsweet.extension.domainAdapters.BackendOnlyAdapter;
import umlp.jsweet.extension.guiAdapters.GUIComponentObserverPatternAdapter;
import umlp.jsweet.extension.guiAdapters.GUIComponentBasicsAdapter;
import umlp.jsweet.extension.guiAdapters.GUITypesImportAdapter;
import umlp.jsweet.extension.guiAdapters.GUIUtilAdapter;
import umlp.jsweet.extension.guiAdapters.DelegateGemListToArrayAdapter;
import umlp.jsweet.extension.guiAdapters.TSTypesAdapter;
import umlp.jsweet.extension.guiAdapters.UMLPGUICollectionsAdapter;

/**
 * This adapter combines the adapters for GUI transpilation
 */
public class UMLPJSweetGUIAdapter extends PrinterAdapter {
  public UMLPJSweetGUIAdapter(PrinterAdapter parentAdapter) {
    super(
        new ImportExcludeAdapter(
            new UMLPJavaBasicsAdapter(
                new UMLPGUICollectionsAdapter(
                    new UMLPRTEImportAdapter(
                        new UMLPBasicsAdapter(
                            new DelegateGemListToArrayAdapter(
                                new GUIComponentObserverPatternAdapter(
                                    new GUIComponentBasicsAdapter(
                                        new GUITypesImportAdapter(
                                            new TSTypesAdapter(
                                                new GUIUtilAdapter(
                                                    new BackendOnlyAdapter(
                                                        new SynchronizedAdapter(
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
                )
            )
        )
    );
  }
}
