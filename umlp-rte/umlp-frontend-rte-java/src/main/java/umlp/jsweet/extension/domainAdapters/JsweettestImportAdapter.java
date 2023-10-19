/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public class JsweettestImportAdapter extends DomainImportAdapter {

  public JsweettestImportAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  protected String getDomainName() {
    return "jsweettest";
  }

}
