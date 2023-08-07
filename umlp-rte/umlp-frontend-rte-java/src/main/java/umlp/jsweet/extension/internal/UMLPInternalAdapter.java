package umlp.jsweet.extension.internal;/* (c) https://github.com/MontiCore/monticore */

import org.jsweet.transpiler.AsyncAwaitPropagationScanner;
import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import umlp.jsweet.extension.backendAdapters.UMLPCollectionsAdapter;
import umlp.jsweet.extension.backendAdapters.UMLPAsyncAwaitPropagationScanner;
import umlp.jsweet.extension.util.RTEPathUtil;
import umlp.jsweet.extension.UMLPAdapterUtils;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

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
    staticLibrary.put(RTEPathUtil.getBackendRTECommonPath("LocalDateDeSer"), "deser/LocalDateDeSer");
    staticLibrary.put(RTEPathUtil.getBackendRTECommonPath("BaseClassObjectMapper"), "common/BaseClassObjectMapper");
    staticLibrary.put(RTEPathUtil.getBackendRTECommonPath("GemBuildable"), "common/GemBuildable");

    // List
    Map<String, String> listTypes = new HashMap<>();
    listTypes.put(List.class.getName(), UMLPCollectionsAdapter.CustomUMLPListInterfaceName);
    listTypes.put(AbstractList.class.getName(), UMLPCollectionsAdapter.CustomUMLPListInterfaceName);
    listTypes.put(ArrayList.class.getName(), UMLPCollectionsAdapter.CustomUMLPListImplementationName);
    listTypes.put(CopyOnWriteArrayList.class.getName(), UMLPCollectionsAdapter.CustomUMLPListImplementationName);
    listTypes.put(LinkedList.class.getName(), UMLPCollectionsAdapter.CustomUMLPListImplementationName);
    listTypes.put(Stack.class.getName(), UMLPCollectionsAdapter.CustomUMLPListImplementationName);
    listTypes.put(Vector.class.getName(), UMLPCollectionsAdapter.CustomUMLPListImplementationName);
    addTypeMappings(listTypes);
    staticLibrary.putAll(listTypes);
  }

  @Override
  public String needsImport(ImportElement importElement, String qualifiedName) {
    if (importElement.getImportedType() != null && staticLibrary.containsKey(qualifiedName)) {
      return getRootRelativeName(importElement.getImportedType());
    }
//    if (UMLPAdapterUtils.isJ4TSSupported(qualifiedName)) {
    //      return qualifiedName;
    //    }
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
    if (staticLibrary.containsKey(fqn)) {
      String staticClass = staticLibrary.get(fqn);
      return new ModuleImportDescriptor(false,
          (PackageElement) importedClass.getEnclosingElement(),
          UMLPAdapterUtils.getSimpleName(staticClass),
          UMLP_RTE_HWC_POINTER + "/" + staticClass, importedClass);
    }

    //    if (UMLPAdapterUtils.isJ4TSSupported(fqn)) {
    //      return new ModuleImportDescriptor(true,
    //          (PackageElement) importedClass.getEnclosingElement(),
    //          importedName, fqn);
    //    }

    return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
  }

  private void initTypeMappings() {
    getTypeMirrorMappings().add(0, UMLPAdapterUtils.computeJavaOptionalTypeMapping(this, types()));
    getTypeMirrorMappings().add(0, UMLPAdapterUtils.computeMCJSonTypeMapping(this, types()));
    getTypeMirrorMappings().add(0, UMLPAdapterUtils.computeDateTypeMapping(this, types()));
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

  @Override
  public void onTranspilationStarted() {
    new UMLPAsyncAwaitPropagationScanner(context).process(getContext().compilationUnits, getContext().trees);
    new AsyncAwaitPropagationScanner(context).process(getContext().compilationUnits, getContext().trees);

    super.onTranspilationStarted();
  }
}
