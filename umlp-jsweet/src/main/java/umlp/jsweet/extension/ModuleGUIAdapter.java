/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.AsyncAwaitPropagationScanner;
import org.jsweet.transpiler.extension.PrinterAdapter;
import standalone.com.sun.source.tree.CompilationUnitTree;
import umlp.jsweet.extension.cocos.JSweetCoCoChecker;
import umlp.jsweet.extension.common.BasicsAdapter;
import umlp.jsweet.extension.common.UMLPEnumAdapter;
import umlp.jsweet.extension.gui.*;
import umlp.jsweet.extension.module.JavaCollectionToJSArrayAdapter;
import umlp.jsweet.extension.module.RelativeImportAdapter;
import umlp.jsweet.extension.common.AdditionalAsyncAwaitPropagationScanner;
import umlp.jsweet.extension.umlp.BackendOnlyAdapter;
import umlp.jsweet.extension.common.JavaStreamAdapter;
import umlp.jsweet.extension.common.JavaTimeAdapter;
import umlp.jsweet.extension.common.MutexAdapter;
import umlp.jsweet.extension.common.FutureAdapter;
import umlp.jsweet.extension.common.OptionalAdapter;
import umlp.jsweet.extension.common.UMLPAsyncAwaitPropagationScanner;

/**
 * This adapter combines the adapters for GUI and CD transpilation
 */
public class ModuleGUIAdapter extends PrinterAdapter {
  public ModuleGUIAdapter(PrinterAdapter parentAdapter) {
    super(
        new JavaStreamAdapter(
        new OptionalAdapter(
        new FutureAdapter(
        new RelativeImportAdapter( // Difference
        new JavaTimeAdapter(
        new BasicsAdapter(
        new JavaCollectionToJSArrayAdapter(
        new GUIComponentObserverPatternAdapter(
        new AngularSpecificsAdapter(
        new GUITypesImportAdapter(
        new TSTypesAdapter(
        new GUIUtilAdapter(
        new BackendOnlyAdapter(
        new MutexAdapter(
        new UMLPEnumAdapter(
        new AnnotationToDecoratorAdapter(
        parentAdapter
    )))))))))))))))));
  }

  @Override
  public void onTranspilationStarted() {
    propagateAsyncFromRTECalls();

    for (CompilationUnitTree compilationUnit : getContext().compilationUnits) {
      if (!getContext().isExcludedSourcePath(compilationUnit.getSourceFile().getName())) {
        new JSweetCoCoChecker(getContext()).scan(compilationUnit, getContext().trees);
      }
    }
    super.onTranspilationStarted();
  }

  public void propagateAsyncFromRTECalls() {
    new UMLPAsyncAwaitPropagationScanner(context, false).process(
          getContext().compilationUnits, getContext().trees);
    new AsyncAwaitPropagationScanner(context).process(getContext().compilationUnits, getContext().trees);
    new UMLPAsyncAwaitPropagationScanner(context, false).process(
        getContext().compilationUnits, getContext().trees);
    new AsyncAwaitPropagationScanner(context).process(getContext().compilationUnits, getContext().trees);
    new AdditionalAsyncAwaitPropagationScanner(context).process(
        getContext().compilationUnits, getContext().trees);
    new AsyncAwaitPropagationScanner(context).process(getContext().compilationUnits, getContext().trees);
  }
}
