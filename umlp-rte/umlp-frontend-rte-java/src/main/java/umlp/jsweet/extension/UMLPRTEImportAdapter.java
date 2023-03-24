package umlp.jsweet.extension;/* (c) https://github.com/MontiCore/monticore */

import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * This adapter is responsible for including the umlp RTE correctly.
 * It redirects the import of all RTE classes to the RTE ts library, adds implemetations
 *  for default methods implemented in the java RTE.
 * Also, it adds types for some default classes like {@link de.se_rwth.commons.logging.Log}
 */
public class UMLPRTEImportAdapter extends UMLPAbstractAdapter {

  public UMLPRTEImportAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  public String needsImport(ImportElement importElement, String qualifiedName) {
    if (importElement.getImportedType() != null && qualifiedName.startsWith(
        UMLPAdapterUtils.UMLP_RTE_PACKAGE)) {
      return getRootRelativeName(importElement.getImportedType());
    }
    return super.needsImport(importElement, qualifiedName);
  }

  @Override
  public ModuleImportDescriptor getModuleImportDescriptor(
      CompilationUnitElement currentCompilationUnit, String importedName,
      TypeElement importedClass) {
    String fqn = importedClass.getQualifiedName().toString();
    if (fqn != null && fqn.startsWith(UMLPAdapterUtils.UMLP_RTE_PACKAGE)) {
      return new ModuleImportDescriptor(false,
          (PackageElement) importedClass.getEnclosingElement(), importedName,
        getLibraryLocation(), importedClass);
    }
    return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
  }

  @Override
  public void afterTypeBody(TypeElement type) {
    // Add default method implementation for interfaces defined in the RTE (JSweet does not find those)
    for (TypeMirror anInterface : type.getInterfaces()) {
      if (anInterface.toString().equals("umlp.backendrte.common.GemBuildable")) {
        addIsBuildFinished(type);
      }
    }
    super.afterTypeBody(type);
  }

  private void addIsBuildFinished(TypeElement type) {
    for (Element enclosedElement : type.getEnclosedElements()) {
      if (enclosedElement.getKind().name().equals("METHOD") && enclosedElement.getSimpleName().equals("isBuildFinished")) {
        return;
      }
    }
    println().printIndent().print("public isBuildFinished(): boolean {").println().
      printIndent().printIndent().print("return true;").println().
      printIndent().print("}").println().println();
  }

  @Override
  protected void initStaticLibrary() {
  }

  @Override
  protected String getLibraryLocation() {
    return UMLPAdapterUtils.UMLP_RTE_LIBRARY_POINTER;
  }
}
