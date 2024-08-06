/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.domainAdapters.ImportExcludeAdapter;
import umlp.jsweet.extension.domainAdapters.JavaRTEMethodsAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPBasicsAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPCollectionsAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPJavaBasicsAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPRTEImportAdapter;
import umlp.jsweet.extension.guiAdapters.BackendOnlyAdapter;

public class UMLPJSweetDomainAdapter extends PrinterAdapter {
  public UMLPJSweetDomainAdapter(PrinterAdapter parentAdapter) {
    super(
        new ImportExcludeAdapter(
            new UMLPCollectionsAdapter(
                new UMLPJavaBasicsAdapter(
                    new UMLPRTEImportAdapter(
                        new UMLPBasicsAdapter(
                            new JavaRTEMethodsAdapter(
                                new BackendOnlyAdapter(
                                    parentAdapter
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
