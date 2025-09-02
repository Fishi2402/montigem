/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.umlp.MutexAdapter;
import umlp.jsweet.extension.common.BasicsAdapter;
import umlp.jsweet.extension.umlp.CollectionsAdapter;
import umlp.jsweet.extension.common.UMLPEnumAdapter;
import umlp.jsweet.extension.umlp.JavaBasicsAdapter;
import umlp.jsweet.extension.umlp.UMLPRTEImportAdapter;
import umlp.jsweet.extension.umlp.BackendOnlyAdapter;
import umlp.jsweet.extension.gui.GUITypesImportAdapter;

/**
 * This adapter combines the adapters for domain classes transpilation
 */
public class UMLPAdapter extends PrinterAdapter {
  public UMLPAdapter(PrinterAdapter parentAdapter) {
    super(
        new GUITypesImportAdapter(
            new CollectionsAdapter(
                new JavaBasicsAdapter(
                    new UMLPRTEImportAdapter(
                        new BasicsAdapter(
                            new BackendOnlyAdapter(
                                new MutexAdapter(
                                    new UMLPEnumAdapter(
                                        parentAdapter
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
