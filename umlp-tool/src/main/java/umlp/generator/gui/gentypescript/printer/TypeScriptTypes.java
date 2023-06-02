/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.printer;

public enum TypeScriptTypes {
  STRING("String", "string"),
  EVENT("Event", "EventEmitter<any>"),
  TEMPLATE_REF("GUIViewElement", "TemplateRef<any>"),
  LIST("List", "Array"),
  COLLECTION("Collection", "Array"),
  LOCAL_DATE("LocalDate", "Date"),
  ITERATOR("Iterator", "IterableIterator"),
  VOID_BOXED("Void", "void"),
  BOOLEAN_BOXED("Boolean", "boolean"),
  INT("int", "number"),
  INT_BOXED("Integer", "number"),
  DOUBLE("double", "number"),
  DOUBLE_BOXED("Double", "number"),
  LONG("long", "number"),
  LONG_BOXED("Long", "number"),
  FLOAT("float", "number"),
  FLOAT_BOXED("Float", "number");

  public final static String ANY = "any";

  private final String tsType;
  private final String mcType;

  TypeScriptTypes(String mcType, String tsType) {
    this.mcType = mcType;
    this.tsType = tsType;
  }

  public static String toTSType(String mcType) {
    String res = mcType;

    /*
    int start = res.indexOf("<");
    int end = res.lastIndexOf(">");

    if (start >= 0 && end >= 0) {
      String main = res.substring(0, start);
      String generic = res.substring(start + 1, end);
      res = toTSType(main) + toTSType(generic);
    }
    /**/

    for (TypeScriptTypes type : TypeScriptTypes.values()) {
      if (type.mcType.equals(res)) {
        res = type.tsType;
      }
    }

    return res;
  }

  public static boolean isDefault(String mcType) {
    for (TypeScriptTypes type : TypeScriptTypes.values()) {
      if (type.mcType.equals(mcType)) {
        return true;
      }
    }
    return false;
  }

  public String getMCType() {
    return mcType;
  }

  public String getTSType() {
    return tsType;
  }

}

