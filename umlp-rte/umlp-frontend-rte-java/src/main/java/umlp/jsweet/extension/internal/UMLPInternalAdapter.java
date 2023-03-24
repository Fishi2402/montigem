package umlp.jsweet.extension.internal;/* (c) https://github.com/MontiCore/monticore */

import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import umlp.jsweet.extension.RTEPathUtil;
import umlp.jsweet.extension.UMLPAdapterUtils;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

public class UMLPInternalAdapter extends org.jsweet.transpiler.extension.PrinterAdapter {

  public static final String UMLP_RTE_HWC_POINTER = "@umlp-rte/hwc";

  private Map<String, String> staticLibrary = new HashMap<>();

  public UMLPInternalAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    initStaticLibary();
    initTypeMappings();
  }

  private void initStaticLibary() {
    staticLibrary.put("de.se_rwth.commons.logging.Log",  "logging/Log");
    staticLibrary.put("de.monticore.symboltable.serialization.JsonPrinter", "mcjson/JsonPrinter");
    staticLibrary.put("java.util.concurrent.atomic.AtomicReference", "javautil/AtomicReference");
    staticLibrary.put("java.util.Random", "javautil/Random");
    staticLibrary.put("java.lang.String", "javautil/StringHelper");
    staticLibrary.put(RTEPathUtil.getBackendRTECommandPath("CommandResultDeSer"), "command/CommandResultDeSer");
    staticLibrary.put(RTEPathUtil.getBackendRTECommonPath("Gem"), "common/Gem");
    staticLibrary.put(RTEPathUtil.getBackendRTECommonPath("GemList"), "common/GemList");
    staticLibrary.put(RTEPathUtil.getBackendRTECommonPath("GemListBuilder"), "common/GemListBuilder");
  }

  @Override
  public String needsImport(ImportElement importElement, String qualifiedName) {
    if (importElement.getImportedType() != null && staticLibrary.keySet().contains(qualifiedName)) {
      return getRootRelativeName(importElement.getImportedType());
    }
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
    for (String staticClass : staticLibrary.keySet()) {
      if (fqn.equals(staticClass)) {
        return new ModuleImportDescriptor(false,
            (PackageElement) importedClass.getEnclosingElement(),
            UMLPAdapterUtils.getSimpleName(staticLibrary.get(staticClass)),
            UMLP_RTE_HWC_POINTER + "/" + staticLibrary.get(staticClass), importedClass);
      }
    }

    return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
  }

  private void initTypeMappings() {
    getTypeMirrorMappings().add(0, UMLPAdapterUtils.computeOptionalTypeMapping(this, types()));
    getTypeMirrorMappings().add(0, UMLPAdapterUtils.computeMCJSonTypeMapping(this, types()));
  }

  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    if (invocation.getTargetType() == null) {
      return super.substituteMethodInvocation(invocation);
    }
    if (UMLPAdapterUtils.substituteMethodInvocation_JavaUtilObjects(this, invocation)) {
      return true;
    } else if (UMLPAdapterUtils.substituteMethodInvocation_JavaLangString(this, invocation)) {
      return true;
    } else if (UMLPAdapterUtils.substituteMethodInvocation_MCJson(this, invocation)) {
      return true;
    }
    return super.substituteMethodInvocation(invocation);
  }
}
