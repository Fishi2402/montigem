/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.domainAdapters;

import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;

import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ImportExcludeAdapter extends PrinterAdapter {
  public ImportExcludeAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  public String needsImport(ImportElement importElement, String qualifiedName) {
    for (Pattern exclude : excludes) {
      if (exclude.matcher(qualifiedName).matches()) {
        return null;
      }
    }
    return super.needsImport(importElement, qualifiedName);
  }

  @Override
  public ModuleImportDescriptor getModuleImportDescriptor(
      CompilationUnitElement currentCompilationUnit, String importedName,
      TypeElement importedClass) {
    for (Pattern exclude : excludes) {
      if (exclude.matcher(importedName).matches()) {
        return null;
      }
    }
    return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
  }

  private static final List<Pattern> excludes = Arrays.asList(
      Pattern.compile("security\\.MailService")
  );
}
