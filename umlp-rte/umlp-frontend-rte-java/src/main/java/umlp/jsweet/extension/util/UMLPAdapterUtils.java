/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.util;

import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.util.Util;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UMLPAdapterUtils {

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

  public static boolean isSubTypeOf(TypeMirror type,
      String superClass, Util util, Types types, JSweetContext context) {
    TypeElement typeElement = UMLPAdapterUtils.getTypeElement(type, util, types, context);
    return isSubTypeOf(typeElement, superClass, util, types);
  }

  public static boolean isSubTypeOf(TypeElement type,
      String superClass, Util util, Types types) {
    if (types.erasure(type.asType()).toString().equals(superClass)) {
      return true;
    }
    for (TypeElement anInterface : util.getInterfaces(type)) {
      if (isSubTypeOf(anInterface, superClass, util, types)) {
        return true;
      }
    }
    if (type.getSuperclass() != null && !type.getSuperclass().toString().equals("none")) {
      if (isSubTypeOf(util.getSuperclass(type), superClass, util, types)) {
        return true;
      }
    }
    return false;
  }

  public static Set<String> listSuperTypes(TypeElement type, Util util, Types types) {
    Set<String> result = new HashSet<>();
    result.add(type.getQualifiedName().toString());

    for (TypeElement anInterface : util.getInterfaces(type)) {
      result.addAll(listSuperTypes(anInterface, util, types));
    }
    if (type.getSuperclass() != null && !type.getSuperclass().toString().equals("none")) {
      result.addAll(listSuperTypes((TypeElement) types.asElement(type.getSuperclass()), util, types));
    }

    return result;
  }

  public static TypeElement getTypeElement(TypeMirror type, Util util, Types types,
      JSweetContext context) {
    TypeElement typeElementByName = util.getTypeElementByName(context,types.erasure(type).toString());
    return typeElementByName;
  }
}
