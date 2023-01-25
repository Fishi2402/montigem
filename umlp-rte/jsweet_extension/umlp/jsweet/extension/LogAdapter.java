package umlp.jsweet.extension;/* (c) https://github.com/MontiCore/monticore */

import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public class LogAdapter extends org.jsweet.transpiler.extension.PrinterAdapter {

  public static final String COMMONS_LOGGING_LOG = "de.se_rwth.commons.logging.Log";

  public static final String UMLP_RTE_PACKAGE = "umlp";

  public static final String UMLP_RTE_POINTER = "@umlp-rte";

  public LogAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  public String needsImport(ImportElement importElement, String qualifiedName) {
    if (importElement.getImportedType() != null && qualifiedName.equals(COMMONS_LOGGING_LOG)) {
      return getRootRelativeName(importElement.getImportedType());
    } else if (importElement.getImportedType() != null && qualifiedName.startsWith(UMLP_RTE_PACKAGE)) {
      return getRootRelativeName(importElement.getImportedType());
    }
    return super.needsImport(importElement, qualifiedName);
  }

  @Override
  public ModuleImportDescriptor getModuleImportDescriptor(
      CompilationUnitElement currentCompilationUnit, String importedName,
      TypeElement importedClass) {
    String fqn = importedClass.getQualifiedName().toString();
    if (fqn.equals(COMMONS_LOGGING_LOG)) {
      return new ModuleImportDescriptor(false, (PackageElement) importedClass.getEnclosingElement(), importedName,
          UMLP_RTE_POINTER + "/logging/Log", importedClass);
    } else if (fqn.startsWith(UMLP_RTE_PACKAGE + ".")) {
      return new ModuleImportDescriptor(false, (PackageElement) importedClass.getEnclosingElement(), importedName,
          UMLP_RTE_POINTER + "/" + fqn.replaceAll("\\.", "/"), importedClass);
    }
    return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
  }
}
