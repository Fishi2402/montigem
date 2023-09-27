/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.guiAdapters.GUIComponentAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPBasicsAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPCollectionsAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPJavaBasicsAdapter;
import umlp.jsweet.extension.domainAdapters.UMLPRTEImportAdapter;
import umlp.jsweet.extension.guiAdapters.GUIImportAdapter;

public class UMLPJSweetAdapter extends PrinterAdapter {
  public UMLPJSweetAdapter(PrinterAdapter parentAdapter) {
    super(new UMLPCollectionsAdapter(new UMLPJavaBasicsAdapter(new UMLPRTEImportAdapter(
        new UMLPBasicsAdapter(new GUIComponentAdapter(new GUIImportAdapter(parentAdapter)))))));
  }
}
