/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public abstract class DomainImportAdapter extends PrinterAdapter {
  private static final String DOMAIN_LOCATION = "@src";

  public DomainImportAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  public String needsImport(ImportElement importElement, String qualifiedName) {
    if (importElement.getImportedType() != null && qualifiedName.startsWith(getDomainName() + ".")) {
      return getRootRelativeName(importElement.getImportedType());
    }
    return super.needsImport(importElement, qualifiedName);
  }

  @Override
  public ModuleImportDescriptor getModuleImportDescriptor(
      CompilationUnitElement currentCompilationUnit, String importedName,
      TypeElement importedClass) {
    String fqn = importedClass.getQualifiedName().toString();
    if (fqn != null && fqn.startsWith(getDomainName())) {
      return new ModuleImportDescriptor(false,
          (PackageElement) importedClass.getEnclosingElement(), importedName,
          getDomainLocation(importedClass), importedClass);
    }
    return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
  }

  private String getDomainLocation(TypeElement importedClass) {
    String loc = importedClass.getQualifiedName().toString().replace(".", "/");
    return DOMAIN_LOCATION + "/" + loc;
  }

  protected abstract String getDomainName();
}
