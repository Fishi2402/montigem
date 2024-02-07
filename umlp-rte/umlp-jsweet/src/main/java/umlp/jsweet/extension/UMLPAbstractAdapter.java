/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;
import umlp.jsweet.extension.util.UMLPAdapterUtils;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.HashMap;

/**
 * Combines common elements for all Adapters
 *
 * This class uses a "static library" to remap types and import from the rte
 */
public abstract class UMLPAbstractAdapter extends PrinterAdapter {
  private static final String UMLP_COMMON_RTE_POINTER = "@umlp/commonj2ts";

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
      return new ModuleImportDescriptor(false, (PackageElement) importedClass.getEnclosingElement(),
          UMLPAdapterUtils.getSimpleName(staticClass), getLibraryLocation(), importedClass);
    }
    return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
  }

  protected void addToStaticLibrary(String staticClass) {
    staticLibrary.put(staticClass, staticClass);
  }
  protected void putToStaticLibrary(String staticClass, String replaceClass) {
    staticLibrary.put(staticClass, replaceClass);
  }

  protected boolean containedInStaticLibrary(String type) {
    return this.staticLibrary.containsKey(type);
  }

  protected HashMap<String, String> getStaticLibrary() {
    return this.staticLibrary;
  }

  protected String getLibraryLocation() {
    return UMLP_COMMON_RTE_POINTER;
  }
}
