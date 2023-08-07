/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;
import umlp.jsweet.extension.util.UMLPAdapterUtils;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.HashMap;

public abstract class UMLPAbstractAdapter extends PrinterAdapter {
  private static final String UMLP_RTE_LIBRARY_POINTER = "umlp-frontend-rte";

  private HashMap<String, String> staticLibrary = new HashMap<>();

  public UMLPAbstractAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    initStaticLibrary();
  }

  protected void initStaticLibrary() {}

  @Override
  public String needsImport(ImportElement importElement, String qualifiedName) {
    if (importElement.getImportedType() != null && staticLibrary.containsKey(qualifiedName)) {
      return getRootRelativeName(importElement.getImportedType());
    }
    return super.needsImport(importElement, qualifiedName);
  }

  @Override
  public ModuleImportDescriptor getModuleImportDescriptor(
      CompilationUnitElement currentCompilationUnit, String importedName,
      TypeElement importedClass) {
    String fqn = importedClass.getQualifiedName().toString();
    if (staticLibrary.containsKey(fqn)) {
      String staticClass = staticLibrary.get(fqn);
      return new ModuleImportDescriptor(false,
          (PackageElement) importedClass.getEnclosingElement(),
          UMLPAdapterUtils.getSimpleName(staticClass),
          getLibraryLocation(), importedClass);
    }
    return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
  }

  protected void addToStaticLibrary(String staticClass) {
    staticLibrary.put(staticClass, staticClass);
  }
  protected void putToStaticLibrary(String staticClass, String replaceClass) {
    staticLibrary.put(staticClass, replaceClass);
  }

  protected String getLibraryLocation() {
    return UMLP_RTE_LIBRARY_POINTER;
  }
}
