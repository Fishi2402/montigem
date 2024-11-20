

/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import com.google.common.collect.FluentIterable;
import de.se_rwth.commons.Splitters;
import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;
public class JavaProjectImportAdapter extends UMLPDomainImportAdapter {
  public JavaProjectImportAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  protected void setDomainName() {
    addDomainName("javaproject");
    addDomainName("baseartifactstest");
  }

  @Override
  public ModuleImportDescriptor getModuleImportDescriptor(
      CompilationUnitElement currentCompilationUnit, String importedName,
      TypeElement importedClass) {
    return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
  }

  @Override
  public String needsImport(ImportElement importElement, String qualifiedName) {
    var interres = super.needsImport(importElement, qualifiedName);
    return interres;
  }
}

