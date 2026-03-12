/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.cd.JavaBasicsAdapter;
import umlp.jsweet.extension.cd.UMLPRTEImportAdapter;
import umlp.jsweet.extension.common.BasicsAdapter;
import umlp.jsweet.extension.common.UMLPEnumAdapter;
import umlp.jsweet.extension.gui.AnnotationToDecoratorAdapter;
import umlp.jsweet.extension.gui.DelegateGemListToArrayAdapter;
import umlp.jsweet.extension.gui.GUIComponentBasicsAdapter;
import umlp.jsweet.extension.gui.GUIComponentObserverPatternAdapter;
import umlp.jsweet.extension.gui.GUITypesImportAdapter;
import umlp.jsweet.extension.gui.GUIUtilAdapter;
import umlp.jsweet.extension.gui.TSTypesAdapter;
import umlp.jsweet.extension.gui.UMLPGUICollectionsAdapter;
import umlp.jsweet.extension.module.RelativeImportAdapter;
import umlp.jsweet.extension.umlp.BackendOnlyAdapter;
import umlp.jsweet.extension.umlp.JavaStreamAdapter;
import umlp.jsweet.extension.umlp.JavaTimeAdapter;
import umlp.jsweet.extension.umlp.MutexAdapter;
import umlp.jsweet.extension.util.NeverCDAdapter;
import umlp.jsweet.extension.util.OnlyCDAdapter;

/**
 * This adapter combines the adapters for GUI and CD transpilation
 */
public class ModuleGUIAdapter extends PrinterAdapter {
  public ModuleGUIAdapter(PrinterAdapter parentAdapter) {
    super(
        new JavaStreamAdapter(
        new UMLPGUICollectionsAdapter(
            new OnlyCDAdapter(new UMLPRTEImportAdapter(
                new RelativeImportAdapter(
                    new NeverCDAdapter(new AnnotationToDecoratorAdapter(
                        new NeverCDAdapter(new DelegateGemListToArrayAdapter(
                            new NeverCDAdapter(new GUIComponentBasicsAdapter(
                                new NeverCDAdapter(new GUIComponentObserverPatternAdapter(
                                    new NeverCDAdapter(new GUITypesImportAdapter(
                                        new NeverCDAdapter(new GUIUtilAdapter(
                                            new NeverCDAdapter(new TSTypesAdapter(
                                                new OnlyCDAdapter(new JavaBasicsAdapter(
                                                    new BackendOnlyAdapter(
                                                        new JavaTimeAdapter(
                                                            new BasicsAdapter(
                                                                new MutexAdapter(
                                                                    new UMLPEnumAdapter(
                                                                        parentAdapter
                                                                    )))))))))))))))))))))))))));
  }
}
