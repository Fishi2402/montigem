package umlp.jsweet.extension.domainAdapters;/* (c) https://github.com/MontiCore/monticore */

import org.jsweet.transpiler.AsyncAwaitPropagationScanner;
import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;
import umlp.jsweet.extension.UMLPAbstractAdapter;
import umlp.jsweet.extension.guiAdapters.GUIComponentAdapter;
import umlp.jsweet.extension.util.UMLPAdapterUtils;
import umlp.jsweet.extension.util.RTEPathUtil;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.function.Function;

/**
 * This adapter is responsible for including the umlp RTE correctly.
 * It redirects the import of all RTE classes to the RTE ts library, adds implemetations
 *  for default methods implemented in the java RTE.
 * Also, it adds types for some default classes like {@link de.se_rwth.commons.logging.Log}
 */
public class UMLPRTEImportAdapter extends UMLPAbstractAdapter {

  private static final String ANGULAR_CORE_PATH = "@angular/core";

  public UMLPRTEImportAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
  }

  @Override
  public String needsImport(ImportElement importElement, String qualifiedName) {
    if (importElement.getImportedType() != null && qualifiedName.startsWith(
        RTEPathUtil.getBackendRTEPath())) {
      return getRootRelativeName(importElement.getImportedType());
    }
    return super.needsImport(importElement, qualifiedName);
  }

  @Override
  public ModuleImportDescriptor getModuleImportDescriptor(
      CompilationUnitElement currentCompilationUnit, String importedName,
      TypeElement importedClass) {
    String fqn = importedClass.getQualifiedName().toString();
    if (fqn != null) {
      if (fqn.equals(GUIComponentAdapter.COMPONENT_ANNOTATION)
          || fqn.equals(GUIComponentAdapter.DIRECTIVE_ANNOTATION)
          || fqn.equals(GUIComponentAdapter.INPUT_ANNOTATION)
          || fqn.equals(GUIComponentAdapter.OUTPUT_ANNOTATION)
      ) {
        return new ModuleImportDescriptor(false,
            (PackageElement) importedClass.getEnclosingElement(), importedName,
            ANGULAR_CORE_PATH, importedClass);
      }
      else if (fqn.startsWith(RTEPathUtil.getBackendRTEPath())) {
        return new ModuleImportDescriptor(false,
            (PackageElement) importedClass.getEnclosingElement(), importedName,
            getLibraryLocation(), importedClass);
      }
    }
    return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
  }

  @Override
  public void afterTypeBody(TypeElement type) {
    // Add default method implementation for interfaces defined in the RTE (JSweet does not find those)

    boolean buildFinishedAdded = false;
    if (UMLPAdapterUtils.isSubTypeOf(type, RTEPathUtil.getBackendRTECommonPath("GemBuildable"), util(), types())) {
      addIsBuildFinished(type);
      buildFinishedAdded = true;
    }
    if (!buildFinishedAdded && UMLPAdapterUtils.isSubTypeOf(type, RTEPathUtil.getBackendRTECommonPath("GemListBuilder"), util(), types())) {
      addIsBuildFinished(type);
      buildFinishedAdded = true;
    }
    super.afterTypeBody(type);
  }

  private void addIsBuildFinished(TypeElement type) {
    for (Element enclosedElement : type.getEnclosedElements()) {
      if (enclosedElement.getKind().name().equals("METHOD") && enclosedElement.getSimpleName().toString().equals("isBuildFinished")) {
        return;
      }
    }
    println().printIndent().print("public isBuildFinished(): boolean {").println().
      printIndent().printIndent().print("return true;").println().
      printIndent().print("}").println().println();
  }

  @Override
  public void onTranspilationStarted() {
    new UMLPAsyncAwaitPropagationScanner(context).process(getContext().compilationUnits, getContext().trees);
    new AsyncAwaitPropagationScanner(context).process(getContext().compilationUnits, getContext().trees);

    super.onTranspilationStarted();
  }
}
