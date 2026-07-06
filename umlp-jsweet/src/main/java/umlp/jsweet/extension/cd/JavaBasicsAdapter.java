/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.cd;

import org.jsweet.transpiler.extension.PrinterAdapter;

public class JavaBasicsAdapter extends
    umlp.jsweet.extension.common.JavaBasicsAdapter {
  public JavaBasicsAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  protected void initStaticLibrary() {
    addToStaticLibrary("java.util.concurrent.atomic.AtomicReference");
    addToStaticLibrary("java.util.Random");
  }
}
