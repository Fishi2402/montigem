/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.umlp.BackendOnlyAdapter;
import umlp.jsweet.extension.umlp.MutexAdapter;
import umlp.jsweet.extension.common.BasicsAdapter;
import umlp.jsweet.extension.common.UMLPEnumAdapter;
import umlp.jsweet.extension.umlp.JavaBasicsAdapter;
import umlp.jsweet.extension.gui.AnnotationToDecoratorAdapter;
import umlp.jsweet.extension.gui.DelegateGemListToArrayAdapter;
import umlp.jsweet.extension.gui.GUIComponentBasicsAdapter;
import umlp.jsweet.extension.gui.GUIComponentObserverPatternAdapter;
import umlp.jsweet.extension.gui.GUITypesImportAdapter;
import umlp.jsweet.extension.gui.GUIUtilAdapter;
import umlp.jsweet.extension.gui.TSTypesAdapter;
import umlp.jsweet.extension.gui.UMLPGUICollectionsAdapter;
import umlp.jsweet.extension.module.RelativeImportAdapter;

/**
 * This adapter combines the adapters for GUI transpilation
 */
public class ModuleAdapter extends PrinterAdapter {
  public ModuleAdapter(PrinterAdapter parentAdapter) {
    super(
        new RelativeImportAdapter(
            new JavaBasicsAdapter(
                new UMLPGUICollectionsAdapter(
                    new BasicsAdapter(
                        new DelegateGemListToArrayAdapter(
                            new GUIComponentObserverPatternAdapter(
                                new GUIComponentBasicsAdapter(
                                    new GUITypesImportAdapter(
                                        new TSTypesAdapter(
                                            new GUIUtilAdapter(
                                                new BackendOnlyAdapter(
                                                    new MutexAdapter(
                                                        new UMLPEnumAdapter(
                                                            new AnnotationToDecoratorAdapter(
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
        )
    );
  }
}
