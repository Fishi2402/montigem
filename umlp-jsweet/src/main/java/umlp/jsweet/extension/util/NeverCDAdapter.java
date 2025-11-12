/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.util;

import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.TypeElementAnnotationScanner;

/**
 * Skips delegate adapter whenever condition is met, otherwise calls the method
 * of the delegate.
 */
public class NeverCDAdapter extends ConditionalAdapter {

  public NeverCDAdapter(PrinterAdapter delegate) {
    super(delegate);
  }

  @Override
  boolean skipCondition() {
    return new TypeElementAnnotationScanner(getCompilationUnitTree(), context).find(GUIAdapterUtils.CD2JAVA_ANNOTATION);
  }

}
