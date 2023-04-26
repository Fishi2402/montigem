/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.printer;

import java.util.List;

public enum TypeScriptTypes {
  STRING("String", "string"),
  EVENT("Event", "EventEmitter"),
  TEMPLATE_REF("GUIViewElement", "TemplateRef<any>"),
  LIST("List", "Array"),
  INT("int", "number"),
  INT_BOXED("Integer", "number"),
  DOUBLE("double", "number"),
  DOUBLE_BOXED("Double", "number"),
  LONG("long", "number"),
  LONG_BOXED("Long", "number"),
  FLOAT("float", "number"),
  FLOAT_BOXED("Float", "number");

  public final static String ANY = "any";

  public final static String LIST_TYPE_SUFFIX = "[]";

  private final String tsType;
  private final String mcType;

  TypeScriptTypes(String mcType, String tsType) {
    this.mcType = mcType;
    this.tsType = tsType;
  }

  public static String toTSType(String mcType) {
    String res = mcType;

    for (TypeScriptTypes type : TypeScriptTypes.values()) {
      if (type.mcType.equals(res)) {
        res = type.tsType;
      }
    }

    return res;
  }

  public static boolean isList(String tsType) {
    return tsType.endsWith(LIST_TYPE_SUFFIX);
  }

  public static String toList(String tsType) {
    return tsType + LIST_TYPE_SUFFIX;
  }

  public static String fromList(String tsType) {
    if (isList(tsType)) {
      return tsType.substring(0, tsType.length() - 2);
    }
    return tsType;
  }

  public String getMCType() {
    return mcType;
  }

  public String getTSType() {
    return tsType;
  }

}

