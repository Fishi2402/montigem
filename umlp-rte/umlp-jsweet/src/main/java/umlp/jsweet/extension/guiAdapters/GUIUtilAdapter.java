/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.guiAdapters;

import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import umlp.jsweet.extension.UMLPAbstractAdapter;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIUtilAdapter extends UMLPAbstractAdapter {

  Map<String, String> utilClasses = new HashMap();
  public GUIUtilAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
//    utilClasses.put("umlp.guiutil.Router", "@angular/router");
    utilClasses.put("umlp.guiutil.Location", "@angular/common");
  }

  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    if (invocation.getTargetType() == null) {
      return super.substituteMethodInvocation(invocation);
    }

    String targetType = invocation.getTargetType().toString();
    String methodName = invocation.getMethodName();

    switch (targetType) {
      case "umlp.guiutil.Router":
        switch (methodName) {
          case "navigate":
            this.print("this._router.navigate([").printArgList(invocation.getArguments())
                .print("]).then(() => { window.location.reload(); });");
            return true;
        }
      case "umlp.guiutil.Location":
        switch (methodName) {
          case "back":
            this.print("this.location.back()");
            return true;
        }
    }

    return super.substituteMethodInvocation(invocation);
  }



  @Override
  public String needsImport(ImportElement importElement, String qualifiedName) {
    if (importElement.getImportedType() != null) {
      for (String utilClass : utilClasses.keySet()) {
        if (qualifiedName.equals(utilClass)) {
          return getRootRelativeName(importElement.getImportedType());
        }
      }
    }
    return super.needsImport(importElement, qualifiedName);
  }

  @Override
  public ModuleImportDescriptor getModuleImportDescriptor(
      CompilationUnitElement currentCompilationUnit, String importedName, TypeElement importedClass) {
    String fqn = importedClass.getQualifiedName().toString();
    if (fqn != null) {
      for (String utilClass : utilClasses.keySet()) {
        if (fqn.equals(utilClass)) {
          return new ModuleImportDescriptor(false,
              (PackageElement) importedClass.getEnclosingElement(), importedName,
              utilClasses.get(utilClass), importedClass);
        }
      }
    }
    return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
  }
}
