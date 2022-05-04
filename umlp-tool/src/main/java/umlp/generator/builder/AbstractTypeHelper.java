/* (c) https://github.com/MontiCore/monticore */


package umlp.generator.builder;

import com.google.common.base.Defaults;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.basicsymbols._ast.ASTType;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.se_rwth.commons.Names;
import umlp.UMLPMill;
import umlp._auxiliary.MCArrayTypesMillForUMLP;

import java.util.Optional;

/**
 * TypeHelper: lots of funtions to examine CD'types. The typeHelper is
 * stateless: only a single copy is needed and therefore instantiated at
 * DexConfigure
 */

public abstract class AbstractTypeHelper {

  public static final String OPTIONAL = "Optional";

  public static final String LIST = "List";

  /**
   * is the attribute of type Boolean?
   *
   * @param type
   * @return
   */
  public boolean isBoolean(CDTypeSymbol type) {
    return CDTypes.isBoolean(type.getName());
  }

  public boolean isBoolean(String typeName) {
    return CDTypes.isBoolean(typeName);
  }

  public boolean isBoolean(ASTType typeName) {
    return isBoolean(typeName.getName());
  }

  /**
   * is the attribute of type Integer?
   *
   * @param type
   * @return
   */
  public boolean isInt(CDTypeSymbol type) {
    return CDTypes.isInteger(type.getName());
  }

  public boolean isInt(String typeName) {
    return CDTypes.isInteger(typeName);
  }

  public boolean isInt(ASTType typeName) {
    return isInt(typeName.getName());
  }

  public boolean isByte(CDTypeSymbol type) {
    return CDTypes.isByte(type.getName());
  }

  public boolean isByte(String typeName) {
    return CDTypes.isByte(typeName);
  }

  public boolean isByte(ASTType typeName) {
    return isByte(typeName.getName());
  }

  public boolean isShort(CDTypeSymbol type) {
    return CDTypes.isShort(type.getName());
  }

  public boolean isShort(String typeName) {
    return CDTypes.isShort(typeName);
  }

  public boolean isShort(ASTType typeName) {
    return isShort(typeName.getName());
  }

  public boolean isLong(CDTypeSymbol type) {
    return CDTypes.isLong(type.getName());
  }

  public boolean isLong(String typeName) {
    return CDTypes.isLong(typeName);
  }

  public boolean isLong(ASTType typeName) {
    return isLong(typeName.getName());
  }

  public boolean isDouble(CDTypeSymbol type) {
    return CDTypes.isDouble(type.getName());
  }

  public boolean isDouble(String typeName) {
    return CDTypes.isDouble(typeName);
  }

  public boolean isDouble(ASTType typeName) {
    return isDouble(typeName.getName());
  }

  public boolean isFloat(CDTypeSymbol type) {
    return CDTypes.isFloat(type.getName());
  }

  public boolean isFloat(String typeName) {
    return CDTypes.isFloat(typeName);
  }

  public boolean isFloat(ASTType typeName) {
    return isFloat(typeName.getName());
  }

  public boolean isChar(CDTypeSymbol type) {
    return CDTypes.isCharacter(type.getName());
  }

  /**
   * @param typeName the interfaceName of a type
   * @return whether the given type interfaceName denotes the java character type
   */
  public boolean isChar(String typeName) {
    return CDTypes.isCharacter(typeName);
  }

  public boolean isChar(ASTType typeName) {
    return isChar(typeName.getName());
  }

  /**
   * is the attribute a Wrapper (of a primitive type?)
   *
   * @param type
   * @return
   */
  public boolean isWrapperType(CDTypeSymbol type) {
    return CDTypes.isWrapperType(type.getName());
  }

  public boolean isWrapperType(String typeName) {
    return CDTypes.isWrapperType(typeName);
  }

  public boolean isWrapperType(ASTType typeName) {
    return isWrapperType(typeName.getName());
  }

  /**
   * is the type of the given field a numeric wrapper type
   *
   * @param type entry
   * @return
   */
  public boolean isWrapperNumeric(CDTypeSymbol type) {
    return CDTypes.isWrapperType(type.getName()) && !isBoolean(type) && !isChar(type);
  }

  public boolean isWrapperNumeric(String name) {
    return CDTypes.isWrapperType(name) && !isBoolean(name) && !isChar(name);
  }

  public boolean isWrapperNumeric(ASTType typeName) {
    return isWrapperNumeric(typeName.getName());
  }

  /**
   * is the attribute of a primitive type?
   *
   * @param type
   * @return
   */
  public boolean isPrimitive(ASTType type) {
    return type instanceof ASTMCPrimitiveType;
  }

  /**
   * is the attribute of a primitive type?
   */
  public boolean isPrimitive(CDTypeSymbol type) {
    return CDTypes.isPrimitiveType(type.getName());
  }

  public boolean isPrimitive(String typeName) {
    return CDTypes.isPrimitiveType(typeName);
  }

  public boolean isOptional(CDTypeSymbol type) {
    return isOptional(type.getName());
  }

  public boolean isOptional(String typeName) {
    return "java.util.Optional".equals(typeName)
        || (typeName.indexOf('.') == -1 && OPTIONAL.equals(typeName));
  }

  public boolean isOptional(ASTType typeName) {
    return isOptional(typeName.getName());
  }

  /**
   * is the type a String type
   *
   * @param type entry
   * @return
   */
  public boolean isString(CDTypeSymbol type) {
    return "String".equals(type.getName())
        || "java.lang.String".equals(type.getName());
  }

  public boolean isString(String type) {
    return "String".equals(type)
        || "java.lang.String".equals(type);
  }

  public boolean isString(ASTType typeName) {
    return isString(typeName.getName());
  }

  /**
   * is the attribute a reference to another type (no wrapper types, no String,
   * no primitive type)?
   *
   * @param type
   * @return
   */
  public boolean isReference(CDTypeSymbol type) {
    return (!isPrimitive(type) && !isWrapperType(type) && !isString(type));
  }

  /**
   * is the attribute a reference to another type (no wrapper types, no String,
   * no primitive type)?
   *
   * @param type
   * @return
   */
  public boolean isReference(String type) {
    return (!isPrimitive(type) && !isWrapperType(type) && !isString(type));
  }

  public boolean isReference(ASTType typeName) {
    return isBoolean(typeName.getName());
  }

  /**
   * is the attribute of an array type?
   *
   * @param ast attribute
   * @return
   */
//  public boolean isArray(ASTCDAttribute ast) {
//    return ast.getType() instanceof MCArrayType;
//  }

  /**
   * is the attribute of type Boolean?
   *
   * @param entry attribute from symtab
   * @return
   */
  public boolean isBoolean(FieldSymbol entry) {
    return CDTypes.isBoolean(entry.getType().getTypeInfo().getName());
  }

  /**
   * is the type a java.util.List type
   *
   * @param type type entry
   * @return
   */
  public boolean isList(CDTypeSymbol type) {
    return (type.getName().indexOf('.') == -1 && "List".equals(type.getName()))
        || "java.util.List".equals(type.getFullName());
  }

  public boolean isList(ASTType type) {
    String typeName = type.getName();
    return typeName.length() > 0 && ("java.util.List".equals(typeName)
        || (typeName.indexOf('.') == -1 && ("List".equals(typeName) || (typeName.startsWith("List<")
        && typeName.endsWith(">")))));
  }

  public boolean isList(String type) {
    return "java.util.List".equals(type) || (type.indexOf('.') == -1 && "List".equals(type));
  }

  /**
   * is the type a java.util.Set type
   *
   * @param type type entry
   * @return
   */
  public boolean isSet(CDTypeSymbol type) {
    return "java.util.Set".equals(type.getFullName())
        || (type.getName().indexOf('.') == -1 && "Set".equals(type.getName()));
  }

  public boolean isSet(String type) {
    return "java.util.Set".equals(type) || (type.indexOf('.') == -1 && "Set".equals(type));
  }

  public boolean isMap(CDTypeSymbol type) {
    return "java.util.Map".equals(type.getFullName());
  }

  public boolean isMap(String type) {
    return "java.util.Map".equals(type) || (type.indexOf('.') == -1 && "Map".equals(type));
  }

  public boolean isEnum(CDTypeSymbol type) {
    return type.isIsEnum();
  }

  /**
   * is the attribute of a primitive and numeric type?
   *
   * @param field attribute
   * @return
   */
  public boolean isIntegral(CDTypeSymbol field) {
    return isByte(field) || isShort(field) || isInt(field) || isLong(field);
  }

  /**
   * is the attribute of a primitive and numeric type?
   *
   * @param field attribute
   * @return
   */
  public boolean isPrimitiveNumeric(CDTypeSymbol field) {
    return isIntegral(field) || isDouble(field)
        || isFloat(field);
  }

  public boolean isPrimitiveNumeric(String typeName) {
    boolean result = false;
    result = isByte(typeName) || isShort(typeName) || isInt(typeName) || isLong(typeName) || isDouble(typeName) || isFloat(typeName);
    return result;
  }

  public boolean isNumeric(String typeName) {
    boolean result = false;
    result = isByte(typeName) || isShort(typeName) || isInt(typeName) || isLong(typeName) || isDouble(typeName) || isFloat(typeName);
    return result;
  }

  /**
   * derives the Wrapper to a given primitive type, Integer for integer. If the
   * param s no primitive type, the result is "java.lang.String" (but should
   * better be an exception) // TODO see issue #1562
   *
   * @param type
   * @return the wrapped version
   */
  public String getWrapperType(String type) {
    if (isPrimitive(type)) {
      return CDTypes.primitiveToQualifiedWrapperOrDefault(type, "java.lang.String");
    }
    else {
      return type;
    }
  }

  public String getWrapperTypeWithoutPackage(String type) {
    String str = getWrapperType(type);
    str = str.replace("java.lang.", "");
    return str;
  }

  public String getUnqualifiedWrapperType(String primitiveType) {
    String result = getWrapperType(primitiveType);
    int index = result.lastIndexOf(".") + 1;
    result = result.substring(index);
    return result;
  }

  public String getDefaultValue(String type) {
    if (CDTypes.isInteger(type)) {
      return String.valueOf(Defaults.defaultValue(int.class));
    }
    else if (CDTypes.isDouble(type)) {
      return String.valueOf(Defaults.defaultValue(double.class));
    }
    else if (CDTypes.isCharacter(type)) {
      return "\'\\" + "u0000\'";
    }
    else if (CDTypes.isShort(type)) {
      return String.valueOf(Defaults.defaultValue(short.class));
    }
    else if (CDTypes.isFloat(type)) {
      return String.valueOf(Defaults.defaultValue(float.class)) + "f";
    }
    else if (CDTypes.isLong(type)) {
      return String.valueOf(Defaults.defaultValue(long.class)) + "L";
    }
    else if (CDTypes.isBoolean(type)) {
      return String.valueOf(Defaults.defaultValue(boolean.class));
    }
    else if (CDTypes.isByte(type)) {
      return String.valueOf(Defaults.defaultValue(byte.class));
    }
    else if ("Date".equals(type)) {
      return "new java.util.Date()";
    }
    else if ("String".equals(type)) {
      return "\"\" ";
    }

    return "null";
  }

  public String getDefaultValue(ASTType typeName) {
    return getDefaultValue(typeName.getName());
  }

  public boolean isCollection(CDTypeSymbol type) {
    return isList(type) || isSet(type) || isMap(type);
  }

  public boolean isCollection(String type) {
    return isList(type) || isSet(type) || isMap(type);
  }

}
