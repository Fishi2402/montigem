/* (c) https://github.com/MontiCore/monticore */
package umlp.jsweet.extension.util;

import de.se_rwth.commons.logging.Log;
import jsweet.lang.Erased;
import org.jsweet.transpiler.JSweetContext;
import org.jsweet.transpiler.Java2TypeScriptTranslator;
import org.jsweet.transpiler.ModuleImportDescriptor;
import org.jsweet.transpiler.extension.PrinterAdapter;
import org.jsweet.transpiler.util.Util;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UMLPAdapterUtils {

  protected final PrinterAdapter adapter;

  protected final Types types;

  protected final JSweetContext context;

  protected final Util util;


  public UMLPAdapterUtils(PrinterAdapter adapter, Types types, JSweetContext context, Util util) {
    this.adapter = adapter;
    this.types = types;
    this.context = context;
    this.util = util;
  }

  public void printMacroName(String macroName) {
    adapter.print("/* " + macroName + " */");
  }

  public static String getSimpleNameOfFQN(String importedName) {
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

  private List<String> j4tsExcluded = Arrays.asList(
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
  private boolean isJ4TSExcluded(String qualifiedName) {
    return j4tsExcluded.stream().anyMatch(e -> e.equals(qualifiedName));
  }

  private List<String> j4tsPackages = Arrays.asList(
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

  private boolean isJ4TSPackage(String qualifiedName) {
    return j4tsPackages.stream().anyMatch(p -> qualifiedName.startsWith(p + "."));
  }

  public boolean isSubTypeOfTypeMirror(TypeMirror type,
      String superClass) {
    TypeElement typeElement = getTypeElement(type);
    return isSubTypeOf(typeElement, superClass);
  }

  public boolean isSubTypeOf(TypeElement type,
      String superClass) {
    if (types.erasure(type.asType()).toString().equals(superClass)) {
      return true;
    }
    for (TypeElement anInterface : util.getInterfaces(type)) {
      if (isSubTypeOf(anInterface, superClass)) {
        return true;
      }
    }
    if (type.getSuperclass() != null && !type.getSuperclass().toString().equals("none")) {
      if (isSubTypeOf(util.getSuperclass(type), superClass)) {
        return true;
      }
    }
    return false;
  }

  public Set<String> listSuperTypes(TypeElement type) {
    Set<String> result = new HashSet<>();
    if (type.getAnnotationsByType(Erased.class).length > 0) {
      // Do not inspect @Erased classes (because they might be async)
      return result;
    }
    result.add(type.getQualifiedName().toString());

    for (TypeElement anInterface : util.getInterfaces(type)) {
      result.addAll(listSuperTypes(anInterface));
    }
    if (type.getSuperclass() != null && !type.getSuperclass().toString().equals("none")) {
      result.addAll(listSuperTypes((TypeElement) types.asElement(type.getSuperclass())));
    }

    return result;
  }

  public TypeElement getTypeElement(TypeMirror type) {
    TypeElement typeElementByName = util.getTypeElementByName(context,types.erasure(type).toString());
    return typeElementByName;
  }

  public String getOverloadMethodName(ExecutableElement method) {
    if (method.getKind() == ElementKind.CONSTRUCTOR) {
      return "constructor";
    }
    StringBuilder nameBuilder = new StringBuilder(method.getSimpleName().toString());
    nameBuilder.append("$");
    for (VariableElement paramElement : method.getParameters()) {
      nameBuilder.append(
          types.erasure(paramElement.asType()).toString().replace('.', '_').replace("[]", "_A"));
      nameBuilder.append("$");
    }
    if (!method.getParameters().isEmpty()) {
      nameBuilder.deleteCharAt(nameBuilder.length() - 1);
    }
    return nameBuilder.toString();
  }

  public String getTypeNameWithoutBuilder(TypeElement type) {
    if (type.getSimpleName().toString().endsWith("Builder")) {
      boolean hasNormalSuperClass = types.asElement(type.getSuperclass()).getSimpleName().toString().equals(
          type.getSimpleName().toString()
              .substring(0, type.getSimpleName().toString().lastIndexOf("Builder")));
      if (hasNormalSuperClass) {
        return type.getSimpleName().toString().substring(0, type.getSimpleName().toString().lastIndexOf("Builder"));
      }
    }
    return type.getSimpleName().toString();
  }

  public Java2TypeScriptTranslator getPrinter() {
    if (adapter.getPrinter() instanceof Java2TypeScriptTranslator) {
      return (Java2TypeScriptTranslator) adapter.getPrinter();
    }
    else {
      Log.error("0JSC001 Java2TypeScriptTranslater must be core Adapter");
      return null;
    }
  }

  public void importFromWithModule(String importedName, String pathToImportedClass) {
    getPrinter().useModule(
        new ModuleImportDescriptor(importedName, pathToImportedClass));
  }

  public void importFromWithoutModule(String importedName, String pathToImportedClass) {
    Set<String> importedNames = context.getImportedNames(context.currentCompilationUnit.get().getSourceFile().getName());
    if (!importedNames.contains(importedName)) {
      context.addHeader("import." + importedName, "import " + importedName + " from '" + pathToImportedClass + "';");
    }
  }
}
