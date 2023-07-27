/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension;

import org.jetbrains.annotations.Nullable;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.model.MethodInvocationElement;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class UMLPAdapterUtils {

  public static Function<TypeMirror, String> computeJavaOptionalTypeMapping(PrinterAdapter adapter, Types types) {
    return (TypeMirror type) -> {
      TypeMirror mappedType = mapOptionalType(adapter, type);
      if (mappedType != null) {
        return adapter.getMappedType(mappedType);
      }
      return null;
    };
  }

  @Nullable
  public static TypeMirror mapOptionalType(PrinterAdapter adapter, TypeMirror type) {
    if (adapter.types().isSubtype(adapter.types().erasure(type), adapter.types().erasure(
        adapter.util().getType(Optional.class)))
        && type instanceof DeclaredType) {
      List<? extends TypeMirror> typeArgs = ((DeclaredType) type).getTypeArguments();
      if (typeArgs.size() > 0) {
        TypeMirror backingType = typeArgs.get(0);
        if (backingType.getKind() == TypeKind.DECLARED) {
          return backingType;
        }
      }
    }
    return null;
  }

  public static Function<TypeMirror, String> computeMCJSonTypeMapping(PrinterAdapter adapter, Types types) {
    return (TypeMirror type) -> {
      if (type.toString().equals("de.monticore.symboltable.serialization.json.JsonObject")) {
        return "any";
      }
      if (type.toString().equals("de.monticore.symboltable.serialization.json.JsonElement")) {
        return "any";
      }
      return null;
    };
  }

  public static Function<TypeMirror, String> computeDateTypeMapping(PrinterAdapter adapter, Types types) {
    return (TypeMirror type) -> {
      switch (type.toString()) {
        case "java.time.LocalDate":
        case "java.util.Date":
          return "Date";
      }
      return null;
    };
  }

  public static boolean substituteMethodInvocation_MCJson(PrinterAdapter adapter,
      MethodInvocationElement invocation) {
    if (invocation.getTargetType().toString().equals("de.monticore.symboltable.serialization.JsonParser")) {
      if (invocation.getMethodName().equals("parseJsonObject") || invocation.getMethodName().equals("parse")) {
        adapter.print("JSON.parse(").printArgList(invocation.getArguments()).print(")");
        return true;
      }
    }
    return false;
  }

  public static boolean substituteMethodInvocation_JavaLangString(PrinterAdapter adapter,
      MethodInvocationElement invocation) {
    if (invocation.getTargetType().toString().equals("java.lang.String")) {
      if (invocation.getMethodName().equals("format")) {
        adapter.print("StringHelper.format(").printArgList(invocation.getArguments()).print(")");
        return true;
      }
    }
    return false;
  }

  public static boolean substituteMethodInvocation_JavaUtilObjects(PrinterAdapter adapter,
      MethodInvocationElement invocation) {
    // java.util.Objects
    if (invocation.getTargetType().toString().equals("java.util.Objects")) {
      // java.util.Objects.requireNonNull
      if (invocation.getMethodName().equals("requireNonNull")) {
        String msg = invocation.getArgumentCount() > 1 ? invocation.getArgument(1).toString() : "";
        adapter.print("/* requireNonNull */")
          .print(String.format(
            "(obj => {if (obj == undefined || obj == null) { throw Object.defineProperty(new Error(%s), '__classes', { configurable: true, value: ['java.lang.Throwable','java.lang.Object','java.lang.RuntimeException','java.lang.NullPointerException','java.lang.Exception'] }) } else { return obj}}) (%s)",
            msg, invocation.getArgument(0).toString()));
        return true;
      }
      // java.util.Objects.equals
      if (invocation.getMethodName().equals("equals")) {
        adapter.print("/* equals */")
          .print(
            String.format("((a, b) => { return a === b; }) (%s, %s)",
              invocation.getArgument(0), invocation.getArgument(1)));
        return true;
      }
    }
    return false;
  }

  public static void printMacroName(PrinterAdapter adapter, String macroName) {
    adapter.print("/* " + macroName + " */");
  }

  public static String getSimpleName(String importedName) {
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

  public static boolean isJ4TSSupported(String qualifiedName) {
    return !isJ4TSExcluded(qualifiedName) && isJ4TSPackage(qualifiedName);
  }

  private static List<String> j4tsExcluded = Arrays.asList(
      "java.lang.Boolean",
      "java.lang.Byte",
      "java.lang.Character",
      "java.lang.Double",
      "java.lang.Float",
      "java.lang.Integer",
      "java.lang.Long",
      "java.lang.Short",
      "java.lang.String",
      "java.lang.Override"
      );
  private static boolean isJ4TSExcluded(String qualifiedName) {
    return j4tsExcluded.stream().anyMatch(e -> e.equals(qualifiedName));
  }

  private static List<String> j4tsPackages = Arrays.asList(
      "java.beans",
      "java.io",
      "java.lang",
      "java.net",
      "java.nio",
      "java.security",
      "java.sql",
      "java.text",
      "java.util"
  );

  private static boolean isJ4TSPackage(String qualifiedName) {
    return j4tsPackages.stream().anyMatch(p -> qualifiedName.startsWith(p + "."));
  }
}
