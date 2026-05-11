/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.umlp;

import com.google.common.collect.FluentIterable;
import de.se_rwth.commons.Splitters;
import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;
import umlp.jsweet.extension.util.GUIAdapterUtils;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class UMLPDomainImportAdapter extends PrinterAdapter {
  private static final String DOMAIN_LOCATION = "";

  private Set<String> domainNames = new LinkedHashSet<>();

  public UMLPDomainImportAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    this.setDomainName();
  }

  // Overrides the needsImport method of PrinterAdapter to customize the required imports for domain names.
  @Override
  public String needsImport(ImportElement importElement, String qualifiedName) {
    if (importElement.getImportedType() != null && startsWithDomainName(qualifiedName)) {
      return getRootRelativeName(importElement.getImportedType());
    }
    return super.needsImport(importElement, qualifiedName);
  }

  private java.util.Set<String> processedImports = new java.util.HashSet<>();

  @Override
  public void beforeCompilationUnit() {
    processedImports.clear();
    super.beforeCompilationUnit();
  }

  // Retrieves the module import descriptor. It provides information about modules that need to be imported for domain names.
  @Override
  public ModuleImportDescriptor getModuleImportDescriptor(
      CompilationUnitElement currentCompilationUnit, String importedName,
      TypeElement importedClass) {
    String fqn = importedClass.getQualifiedName().toString();
    if (fqn != null && startsWithDomainName(fqn)) {
      org.jsweet.transpiler.Java2TypeScriptTranslator printer = (org.jsweet.transpiler.Java2TypeScriptTranslator) getPrinter();

      // If it's an erased class, we don't handle it here to avoid unnecessary imports
      if (printer.getContext().hasAnnotationType(importedClass, "jsweet.lang.Erased")) {
        return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
      }

      String path = getDomainLocation(importedClass);

      // Check for self-import
      String currentSourceFilePath = currentCompilationUnit.getSourceFilePath();
      if (currentSourceFilePath != null) {
        String fileName = new java.io.File(currentSourceFilePath).getName();
        if (fileName.equals(importedName + ".java")) {
          return null;
        }
      }


      // Fix for duplicate imports and circular dependencies
      String key = importedName + "@" + path;
      if (processedImports.contains(key)) {
        return null;
      }
      processedImports.add(key);

      // If it's a strong dependency (inheritance or static initializer), it must be at the top.
      // Otherwise, we move it to the footer to avoid circular dependencies.
      if (printer.getContext().referenceAnalyzer != null
          && printer.getContext().referenceAnalyzer.isDependent(printer.getCompilationUnit(), importedClass)) {
        return new ModuleImportDescriptor(false,
            (PackageElement) importedClass.getEnclosingElement(), importedName,
            path, importedClass);
      } else {
        printer.getContext().addTopFooterStatement("import." + importedName,
            "import { " + importedName + " } from '" + path + "';\n");
        printer.getContext().registerImportedName(printer.getCompilationUnit().getSourceFile().getName(), importedClass, importedName);
        return null;
      }
    }
    return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
  }
  // Checks if the fully qualified name (fqn) starts with one of the domain names.
  private boolean startsWithDomainName(String fqn) {
    for (String domainName : domainNames) {
      if (fqn.equals(domainName) || fqn.startsWith(domainName + ".")) {
        return true;
      }
    }
    return false;
  }

  private String getDomainLocation(TypeElement importedClass) {
    String loc = importedClass.getQualifiedName().toString().replace(".", "/");
    return DOMAIN_LOCATION + loc;
  }

  // Allows adding domain names to the domainNames set.
  protected final void addDomainName(String... domainName) {
    this.domainNames.addAll(Arrays.asList(domainName));
  }

  // Abstract method to set the domain name. This method must be implemented by all subclasses of UMLPDomainImportAdapter.
  protected abstract void setDomainName();
}
