/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;

public abstract class UMLPAbstractAdapter extends PrinterAdapter {
  protected HashSet<String> staticLibrary = new HashSet<>();

  public UMLPAbstractAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    initStaticLibary();
  }

  abstract String getLibraryLocation();

  abstract void initStaticLibary();

  @Override
  public String needsImport(ImportElement importElement, String qualifiedName) {
    if (importElement.getImportedType() != null) {
      for (String staticClass : staticLibrary) {
        if (qualifiedName.equals(staticClass)) {
          return getRootRelativeName(importElement.getImportedType());
        }
      }
    }
    return super.needsImport(importElement, qualifiedName);
  }

  @Override
  public ModuleImportDescriptor getModuleImportDescriptor(
    CompilationUnitElement currentCompilationUnit, String importedName, TypeElement importedClass) {
    String fqn = importedClass.getQualifiedName().toString();
    for (String staticClass : staticLibrary) {
      if (fqn.equals(staticClass)) {
        return new ModuleImportDescriptor(false,
          (PackageElement) importedClass.getEnclosingElement(), importedName,
          getLibraryLocation(), importedClass);
      }
    }
    return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
  }
}
