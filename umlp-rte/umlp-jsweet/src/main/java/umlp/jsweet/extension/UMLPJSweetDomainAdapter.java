/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.extension.PrinterAdapter;
import standalone.com.sun.source.tree.CompilationUnitTree;
import umlp.jsweet.extension.cocos.JSweetCoCoChecker;
import umlp.jsweet.extension.domainAdapters.MutexAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPBasicsAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPCollectionsAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPEnumAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPJavaBasicsAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPRTEImportAdapter;
import umlp.jsweet.extension.domainAdapters.BackendOnlyAdapter;
import umlp.jsweet.extension.guiAdapters.GUITypesImportAdapter;

/**
 * This adapter combines the adapters for domain classes transpilation
 */
public class UMLPJSweetDomainAdapter extends PrinterAdapter {
  public UMLPJSweetDomainAdapter(PrinterAdapter parentAdapter) {
    super(
        new GUITypesImportAdapter(
            new UMLPCollectionsAdapter(
                new UMLPJavaBasicsAdapter(
                    new UMLPRTEImportAdapter(
                        new UMLPBasicsAdapter(
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

  @Override
  public void onTranspilationStarted() {
    for (CompilationUnitTree compilationUnit : getContext().compilationUnits) {
      if(!getContext().isExcludedSourcePath(compilationUnit.getSourceFile().getName())) {
        new JSweetCoCoChecker(getContext()).scan(compilationUnit, getContext().trees);
      }
    }
    super.onTranspilationStarted();
  }
}
