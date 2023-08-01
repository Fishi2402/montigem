/* (c) https://github.com/MontiCore/monticore */
package umlp.generator.gui.gentypescript.printer;

import de.monticore.guidsl.types.check.FullDeriveFromGUIDSL;
import de.monticore.guidsl.types.check.FullSynthesizeFromGUIDSL;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCalculator;

public enum TypeScriptTypes {
  STRING("String", "string"),
  EVENT("Event", "EventEmitter<any>"),
  TEMPLATE_REF("GUIViewElement", "TemplateRef<any>"),
  LIST("List", "Array"),
  COLLECTION("Collection", "Array"),
  LOCAL_DATE("LocalDate", "Date"),
  LOCAL_DATE_TIME("LocalDateTime", "Date"),
  ITERATOR("Iterator", "Iterator"),
  VOID("void", "void"),
  VOID_BOXED("Void", "void"),
  BOOLEAN("boolean", "boolean"),
  BOOLEAN_BOXED("Boolean", "boolean"),
  INT("int", "number"),
  INT_BOXED("Integer", "number"),
  DOUBLE("double", "number"),
  DOUBLE_BOXED("Double", "number"),
  LONG("long", "number"),
  LONG_BOXED("Long", "number"),
  FLOAT("float", "number"),
  FLOAT_BOXED("Float", "number"),
  RUNNABLE("Runnable", "() => void");

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

  // TODO AGe: deprecated, unclean
  public static boolean isString(SymTypeExpression type) {
    if (toTSType(type.print()).equals(STRING.getTSType())) {
      return true;
    }
    TypeCalculator tc = new TypeCalculator(
        new FullSynthesizeFromGUIDSL(), new FullDeriveFromGUIDSL()
    );
    return tc.isString(type)
        || tc.isChar(type);
  }

  // TODO AGe: deprecated, unclean
  public static boolean isNumber(SymTypeExpression type) {
    if (toTSType(type.print()).equals(INT.getTSType())) {
      return true;
    }
    TypeCalculator tc = new TypeCalculator(
        new FullSynthesizeFromGUIDSL(), new FullDeriveFromGUIDSL()
    );
    return tc.isDouble(type)
        || tc.isInt(type)
        || tc.isFloat(type)
        || tc.isLong(type)
        || tc.isShort(type);
  }

  public String getMCType() {
    return mcType;
  }

  public String getTSType() {
    return tsType;
  }

}

