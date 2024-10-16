/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.domainAdapters.ImportExcludeAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPBasicsAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPCollectionsAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPJavaBasicsAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPRTEImportAdapter;
import umlp.jsweet.extension.domainAdapters.BackendOnlyAdapter;

/**
 * This adapter combines the adapters for domain classes transpilation
 */
public class UMLPJSweetDomainAdapter extends PrinterAdapter {
  public UMLPJSweetDomainAdapter(PrinterAdapter parentAdapter) {
    super(
        new ImportExcludeAdapter(
            new UMLPCollectionsAdapter(
                new UMLPJavaBasicsAdapter(
                    new UMLPRTEImportAdapter(
                        new UMLPBasicsAdapter(
                            new BackendOnlyAdapter(
                                parentAdapter
                            )
                        )
                    )
                )
            )
        )
    );
  }
}
