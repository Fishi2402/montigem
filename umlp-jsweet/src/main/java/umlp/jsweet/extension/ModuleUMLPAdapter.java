/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.extension.PrinterAdapter;
import standalone.com.sun.source.tree.CompilationUnitTree;
import umlp.jsweet.extension.cocos.JSweetCoCoChecker;
import umlp.jsweet.extension.common.BasicsAdapter;
import umlp.jsweet.extension.common.UMLPEnumAdapter;
import umlp.jsweet.extension.gui.GUITypesImportAdapter;
import umlp.jsweet.extension.module.RelativeImportAdapter;
import umlp.jsweet.extension.umlp.BackendOnlyAdapter;
import umlp.jsweet.extension.umlp.CollectionsAdapter;
import umlp.jsweet.extension.umlp.JavaBasicsAdapter;
import umlp.jsweet.extension.umlp.JavaStreamAdapter;
import umlp.jsweet.extension.umlp.JavaTimeAdapter;
import umlp.jsweet.extension.umlp.MutexAdapter;
import umlp.jsweet.extension.umlp.FutureAdapter;
import umlp.jsweet.extension.umlp.OptionalAdapter;
import umlp.jsweet.extension.umlp.UMLPRTEImportAdapter;

/**
 * This adapter combines the adapters for domain classes transpilation
 * with relative imports (see {@link RelativeImportAdapter})
 */
public class ModuleUMLPAdapter extends PrinterAdapter {
  public ModuleUMLPAdapter(PrinterAdapter parentAdapter) {
    super(
        new RelativeImportAdapter(
            new GUITypesImportAdapter(
                new CollectionsAdapter(
                    new OptionalAdapter(
                        new FutureAdapter(
                            new JavaStreamAdapter(
                                new JavaTimeAdapter(
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
      if (!getContext().isExcludedSourcePath(compilationUnit.getSourceFile().getName())) {
        new JSweetCoCoChecker(getContext()).scan(compilationUnit, getContext().trees);
      }
    }
    super.onTranspilationStarted();
  }
}
