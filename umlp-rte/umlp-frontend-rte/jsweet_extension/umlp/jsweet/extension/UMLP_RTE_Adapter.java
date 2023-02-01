package umlp.jsweet.extension;/* (c) https://github.com/MontiCore/monticore */

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UMLP_RTE_Adapter extends org.jsweet.transpiler.extension.PrinterAdapter {

  public static void main(String[] args) {

  }

  public static final String UMLP_RTE_PACKAGE = "umlp";

  public static final String UMLP_RTE_HWC_POINTER = "@umlp-rte/hwc";

  private Map<String, String> staticLibrary = new HashMap<>();

  public UMLP_RTE_Adapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    initStaticLibary();
    initTypeMappings();
  }

  private void initStaticLibary() {
    staticLibrary.put("de.se_rwth.commons.logging.Log", "logging/Log");
    staticLibrary.put("umlp.command.CommandObjectMapper", "command/CommandObjectMapper");
    staticLibrary.put("java.util.concurrent.atomic.AtomicReference", "javautil/AtomicReference");
    staticLibrary.put("java.util.Random", "javautil/Random");
  }

  @Override
  public String needsImport(ImportElement importElement, String qualifiedName) {
    if (importElement.getImportedType() != null) {
      for (String staticClass : staticLibrary.keySet()) {
        if (qualifiedName.equals(staticClass)) {
          return getRootRelativeName(importElement.getImportedType());
        }
      }
    }
    if (importElement.getImportedType() != null && qualifiedName.startsWith(UMLP_RTE_PACKAGE)) {
      return getRootRelativeName(importElement.getImportedType());
    }
    return super.needsImport(importElement, qualifiedName);
  }

  @Override
  public ModuleImportDescriptor getModuleImportDescriptor(
      CompilationUnitElement currentCompilationUnit, String importedName,
      TypeElement importedClass) {
    String fqn = importedClass.getQualifiedName().toString();
    for (String staticClass : staticLibrary.keySet()) {
      if (fqn.equals(staticClass)) {
        return new ModuleImportDescriptor(false, (PackageElement) importedClass.getEnclosingElement(), importedName,
          UMLP_RTE_HWC_POINTER + "/" + staticLibrary.get(staticClass), importedClass);
      }
    }

    return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
  }

  private void initTypeMappings() {
    getTypeMirrorMappings().add(0, (TypeMirror type) -> {
      if(type.toString().equals("java.util.Optional<umlp.rte.GemList<E>>")) {
        return "GemList<E>";
      }
      return null;
    });
  }
}
