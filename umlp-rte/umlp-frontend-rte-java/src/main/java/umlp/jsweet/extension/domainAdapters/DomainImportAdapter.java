/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import com.google.common.collect.FluentIterable;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;
import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

public abstract class DomainImportAdapter extends PrinterAdapter {
  private static final String DOMAIN_LOCATION = "@src";

  private Set<String> domainNames = new HashSet<>();

  public DomainImportAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    this.setDomainName();
  }

  @Override
  public String needsImport(ImportElement importElement, String qualifiedName) {
    if (importElement.getImportedType() != null && startsWithDomainName(qualifiedName)) {
      return getRootRelativeName(importElement.getImportedType());
    }
    return super.needsImport(importElement, qualifiedName);
  }

  @Override
  public ModuleImportDescriptor getModuleImportDescriptor(
      CompilationUnitElement currentCompilationUnit, String importedName,
      TypeElement importedClass) {
    String fqn = importedClass.getQualifiedName().toString();
    if (fqn != null && startsWithDomainName(fqn)) {
      if (super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass) == null) {
        return null;
      }
      return new ModuleImportDescriptor(false,
          (PackageElement) importedClass.getEnclosingElement(), importedName,
          getDomainLocation(importedClass), importedClass);
    }
    return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
  }

  private boolean startsWithDomainName(String fqn) {
    return domainNames.contains(FluentIterable.from(Splitters.DOT.split(fqn)).first().get());
  }

  private String getDomainLocation(TypeElement importedClass) {
    String loc = importedClass.getQualifiedName().toString().replace(".", "/");
    return DOMAIN_LOCATION + "/" + loc;
  }

  protected final void addDomainName(String... domainName) {
    for (String s : domainName) {
      this.domainNames.add(s);
    }
  }

  protected abstract void setDomainName();
}
