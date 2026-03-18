/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.gui.*;
import umlp.jsweet.extension.umlp.JavaStreamAdapter;
import umlp.jsweet.extension.umlp.JavaTimeAdapter;
import umlp.jsweet.extension.umlp.MutexAdapter;
import umlp.jsweet.extension.common.BasicsAdapter;
import umlp.jsweet.extension.common.UMLPEnumAdapter;
import umlp.jsweet.extension.umlp.JavaBasicsAdapter;
import umlp.jsweet.extension.umlp.UMLPRTEImportAdapter;
import umlp.jsweet.extension.umlp.BackendOnlyAdapter;
import standalone.com.sun.source.tree.CompilationUnitTree;
import umlp.jsweet.extension.cocos.JSweetCoCoChecker;

/**
 * This adapter combines the adapters for GUI transpilation
 */
public class GUIAdapter extends PrinterAdapter {
  public GUIAdapter(PrinterAdapter parentAdapter) {
    super(
        new JavaStreamAdapter(
            new JavaBasicsAdapter(
                new UMLPGUICollectionsAdapter(
                    new UMLPRTEImportAdapter(
                        new JavaTimeAdapter(
                            new BasicsAdapter(
                                new DelegateGemListToArrayAdapter(
                                    new GUIComponentObserverPatternAdapter(
                                        new AppRouterAdapter(
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
