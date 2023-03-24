package umlp.jsweet.extension.internal;/* (c) https://github.com/MontiCore/monticore */

import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.CompilationUnitElement;
import org.jsweet.transpiler.model.ImportElement;
import org.jsweet.transpiler.model.MethodInvocationElement;
import org.jsweet.transpiler.model.NewClassElement;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UMLPInternalAdapter extends org.jsweet.transpiler.extension.PrinterAdapter {

  public static final String UMLP_RTE_PACKAGE = "umlp.backendrte";

  public static final String UMLP_RTE_HWC_POINTER = "@umlp-rte/hwc";

  private Map<String, String> staticLibrary = new HashMap<>();

  public UMLPInternalAdapter(PrinterAdapter parentAdapter) {
    super(parentAdapter);
    initStaticLibary();
    initTypeMappings();
  }

  private void initStaticLibary() {
    staticLibrary.put("de.se_rwth.commons.logging.Log",  "logging/Log");
    staticLibrary.put("java.util.concurrent.atomic.AtomicReference", "javautil/AtomicReference");
    staticLibrary.put("java.util.Random", "javautil/Random");
    staticLibrary.put("de.monticore.symboltable.serialization.JsonPrinter", "mcjson/JsonPrinter");
    staticLibrary.put("java.lang.String", "javautil/StringHelper");
    staticLibrary.put(backendRTE("command.CommandResultDeSer"), "command/CommandResultDeSer");
    staticLibrary.put(backendRTE("common.Gem"), "common/Gem");
    staticLibrary.put(backendRTE("common.GemList"), "common/GemList");
    staticLibrary.put(backendRTE("common.GemListBuilder"), "common/GemListBuilder");
  }

  private static String backendRTE(String furtherPath) {
    return String.join(".", UMLP_RTE_PACKAGE, furtherPath);
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
        return new ModuleImportDescriptor(false, (PackageElement) importedClass.getEnclosingElement(), getName(staticLibrary.get(staticClass)),
          UMLP_RTE_HWC_POINTER + "/" + staticLibrary.get(staticClass), importedClass);
      }
    }

    return super.getModuleImportDescriptor(currentCompilationUnit, importedName, importedClass);
  }

  private String getName(String importedName) {
    int index = 0;
    if (importedName.lastIndexOf("/") > 0) {
      index = importedName.lastIndexOf("/");
    }
    if (importedName.lastIndexOf("\\") > 0) {
      index = importedName.lastIndexOf("\\");
    }
    if (importedName.lastIndexOf(".") > 0) {
      index = importedName.lastIndexOf(".");
    }
    if (index > 0) {
      return importedName.substring(index + 1);
    }
    return importedName;
  }

  private void initTypeMappings() {
    getTypeMirrorMappings().add(0, (TypeMirror type) -> {
      if(type.toString().equals(String.format("java.util.Optional<%s>", backendRTE("common.GemList<E>")))) {
        return "GemList<E>";
      }
      if (types().isSubtype(types().erasure(type), types().erasure(util().getType(Optional.class)))
          && type instanceof DeclaredType) {
        List<? extends TypeMirror> typeArgs = ((DeclaredType) type).getTypeArguments();
        if (typeArgs.size() > 0) {
          TypeMirror backingType = typeArgs.get(0);
          if (backingType.getKind() == TypeKind.DECLARED) {
            return getMappedType(backingType);
          }
        }
      }
      if(type.toString().equals("de.monticore.symboltable.serialization.json.JsonObject")) {
        return "any";
      }
      if(type.toString().equals("de.monticore.symboltable.serialization.json.JsonElement")) {
        return "any";
      }
      return null;
    });
  }

  @Override
  public boolean substituteMethodInvocation(MethodInvocationElement invocation) {
    if (invocation.getTargetType() == null) {
      return super.substituteMethodInvocation(invocation);
    }
    if (substituteMethodInvocation_JavaUtilObjects(invocation)) {
      return true;
    } else if (substituteMethodInvocation_JavaLangString(invocation)) {
      return true;
    } else if (substituteMethodInvocation_MCJson(invocation)) {
      return true;
    }
    return super.substituteMethodInvocation(invocation);
  }

  private boolean substituteMethodInvocation_MCJson(MethodInvocationElement invocation) {
    if (invocation.getTargetType().toString().equals("de.monticore.symboltable.serialization.JsonParser")) {
      if (invocation.getMethodName().equals("parseJsonObject") || invocation.getMethodName().equals("parse")) {
        print("JSON.parse(").printArgList(invocation.getArguments()).print(")");
        return true;
      }
    }
    return false;
  }

  private boolean substituteMethodInvocation_JavaLangString(MethodInvocationElement invocation) {
    if (invocation.getTargetType().toString().equals("java.lang.String")) {
      if (invocation.getMethodName().equals("format")) {
        print("StringHelper.format(").printArgList(invocation.getArguments()).print(")");
        return true;
      }
    }
    return false;
  }

  private boolean substituteMethodInvocation_JavaUtilObjects(MethodInvocationElement invocation) {
    if (invocation.getTargetType().toString().equals("java.util.Objects")) {
      if (invocation.getMethodName().equals("requireNonNull")) {
        String msg = invocation.getArgumentCount() > 1 ? invocation.getArgument(1).toString() : "";
        print("/* requireNonNull */")
          .print(String.format(
            "(obj => {if (obj == undefined || obj == null) { throw Object.defineProperty(new Error(%s), '__classes', { configurable: true, value: ['java.lang.Throwable','java.lang.Object','java.lang.RuntimeException','java.lang.NullPointerException','java.lang.Exception'] }) } else { return obj}}) (%s)",
            msg, invocation.getArgument(0).toString()));
        return true;
      }
      if (invocation.getMethodName().equals("equals")) {
        print("/* equals */")
          .print(
            String.format("((a, b) => { return a === b; }) (%s, %s)",
              invocation.getArgument(0), invocation.getArgument(1)));
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean substituteNewClass(NewClassElement newClass) {
    return super.substituteNewClass(newClass);
  }
}
