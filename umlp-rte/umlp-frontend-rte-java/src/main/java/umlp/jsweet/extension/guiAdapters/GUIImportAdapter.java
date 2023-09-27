/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.guiAdapters;

import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;
import umlp.jsweet.extension.UMLPAbstractAdapter;
import umlp.jsweet.extension.util.UMLPAdapterUtils;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import java.util.function.Function;

public class GUIImportAdapter extends UMLPAbstractAdapter {
  public GUIImportAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    initTypeMappings();
  }

  private void initTypeMappings() {
    getTypeMirrorMappings().add(0, computeGuiRTETypeMapping());
  }

  private Function<TypeMirror, String> computeGuiRTETypeMapping() {
    return (TypeMirror type) -> {
      for (TSImportMap value : TSImportMap.values()) {
        if (type.toString().startsWith(value.getJavaPackage())) {
          return UMLPAdapterUtils.getSimpleName(type.toString());
        }
      }
      return null;
    };
  }

  @Override
  public String needsImport(ImportElement importElement, String qualifiedName) {
    if (importElement.getImportedType() != null) {
      for (TSImportMap value : TSImportMap.values()) {
        if (qualifiedName.startsWith(value.getJavaPackage())) {
          return getRootRelativeName(importElement.getImportedType());
        }
      }
    }
    return super.needsImport(importElement, qualifiedName);
  }

  @Override
  public ModuleImportDescriptor getModuleImportDescriptor(
      CompilationUnitElement currentCompilationUnit, String importedName,
      TypeElement importedClass) {
    String fqn = importedClass.getQualifiedName().toString();
    if (fqn != null) {
      for (TSImportMap value : TSImportMap.values()) {
        if (fqn.startsWith(value.getJavaPackage())) {
          return new ModuleImportDescriptor(false,
              (PackageElement) importedClass.getEnclosingElement(), importedName,
              value.getTSPackage(), importedClass);
        }
      }

    }
    return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
  }
}
