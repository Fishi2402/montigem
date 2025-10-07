/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.guiTypesAdapters;

import org.jsweet.transpiler.extension.PrinterAdapter;

public class UMLPJavaBasicsAdapter extends
    umlp.jsweet.extension.domainAdapters.UMLPJavaBasicsAdapter {
  public UMLPJavaBasicsAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  protected void initStaticLibrary() {
    addToStaticLibrary("java.util.concurrent.atomic.AtomicReference");
    addToStaticLibrary("java.util.Random");
  }
}
