/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.guiAdapters;

import de.se_rwth.commons.logging.Log;
import org.jsweet.transpiler.Java2TypeScriptTranslator;
import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Optional;

public class BackendOnlyAdapter extends PrinterAdapter {

  public static final String BACKEND_ONLY_ANNOTATION = "umlp.jsweet.extension.annotation.BackendOnly";

  public BackendOnlyAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  public boolean substituteMethodBody(TypeElement parentTypeElement, ExecutableElement method) {
    if (isBackendOnly(method)) {
      String val = Optional.ofNullable(
              getAnnotationValue(method, BACKEND_ONLY_ANNOTATION, VariableElement.class, null)
          )
          .map(VariableElement::getSimpleName).map(Object::toString).orElse(null);
      if ("STATUS_OK".equals(val)) {
        print("return StatusResult.ok(this.getId());\n");
      } else if ("STATUS_ERROR".equals(val)) {
        print("return StatusResult.error(this.getId());\n");
      } else {
        print("throw new Error('This method can only be called in the backend!');\n");
      }
      return true;
    } else {
      return super.substituteMethodBody(parentTypeElement, method);
    }
  }

  @Override
  public String needsImport(ImportElement importElement, String qualifiedName) {
    ExecutableElement executableElement = getParentElement(ExecutableElement.class);
    if (executableElement != null && executableElement.getKind().name().equals("METHOD") && isBackendOnly(executableElement)) {
      return null;
    }
    return super.needsImport(importElement, qualifiedName);
  }

  @Override
  public ModuleImportDescriptor getModuleImportDescriptor(
      CompilationUnitElement currentCompilationUnit, String importedName,
      TypeElement importedClass) {
    try {
      ExecutableElement executableElement = getParentElement(ExecutableElement.class);
      if (executableElement != null && executableElement.getKind().name().equals("METHOD")
          && isBackendOnly(executableElement)) {
        return null;
      }
    } catch (NullPointerException e) {
    } finally {
      return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
    }
  }

  @Override
  public Java2TypeScriptTranslator getPrinter() {
    if (super.getPrinter() instanceof Java2TypeScriptTranslator) {
      return (Java2TypeScriptTranslator) super.getPrinter();
    } else {
      Log.error("0JSC001 Java2TypeScriptTranslater must be core Adapter");
      return null;
    }
  }


  private boolean isBackendOnly(ExecutableElement method) {
    return context.hasAnnotationType(method, BACKEND_ONLY_ANNOTATION);
  }
}
