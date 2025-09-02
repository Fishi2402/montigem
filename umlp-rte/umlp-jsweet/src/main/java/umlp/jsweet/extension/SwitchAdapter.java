/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.extension.PrinterAdapter;
import umlp.jsweet.extension.util.GUIAdapterUtils;

import javax.lang.model.element.TypeElement;

public class SwitchAdapter extends PrinterAdapter {
  protected final CDAdapter guiTypesAdapter;
  protected final ModuleAdapter guiRTEModuleAdapter;

  public SwitchAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    guiTypesAdapter = new CDAdapter(this);
    guiTypesAdapter.setPrinter(this.getPrinter());
    guiRTEModuleAdapter = new ModuleAdapter(this);
    guiRTEModuleAdapter.setPrinter(this.getPrinter());
  }

  protected boolean done = false;

  @Override
  public boolean substituteType(TypeElement type) {
    // TypeElementAnnotationScanner scanner = new TypeElementAnnotationScanner(getCompilationUnitTree(), getContext());
    // if (scanner.find(GUIAdapterUtils.CD2JAVA_ANNOTATION)) {
    if (!done) {
      if (context.hasAnnotationType(type, GUIAdapterUtils.CD2JAVA_ANNOTATION)) {
        getPrinter().setAdapter(guiTypesAdapter);
      }
      else {
        getPrinter().setAdapter(guiRTEModuleAdapter);
      }
      done = true;
    }
    else {
      done = false;
    }

    return super.substituteType(type);
  }

}
