package umlp.jsweet.extension;/* (c) https://github.com/MontiCore/monticore */

import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public class LogAadapter extends org.jsweet.transpiler.extension.PrinterAdapter {

  public LogAadapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  public String needsImport(ImportElement importElement, String qualifiedName) {
    if (importElement.getImportedType() != null && "de.se_rwth.commons.logging.Log".equals(
        qualifiedName)) {
      return getRootRelativeName(importElement.getImportedType());
    }
    return super.needsImport(importElement, qualifiedName);
  }

  @Override
  public ModuleImportDescriptor getModuleImportDescriptor(
      CompilationUnitElement currentCompilationUnit, String importedName,
      TypeElement importedClass) {
    if (importedClass.getQualifiedName().toString().equals("de.se_rwth.commons.logging.Log")) {
      return new ModuleImportDescriptor(false, (PackageElement) importedClass.getEnclosingElement(), importedName,
          "@umlp-rte/logging/Log", importedClass);
    }
    return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
  }
}
