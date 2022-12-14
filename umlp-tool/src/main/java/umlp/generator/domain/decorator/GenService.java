package umlp.generator.domain.decorator;

import com.google.common.base.Defaults;
import de.monticore.cd4code.typescalculator.FullSynthesizeFromCD4Code;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpressionBuilder;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPFullPrettyPrinter;
import umlp.UMLPMill;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.types.check.SymTypePrimitive.box;

public class GenService {

  public static final String UMLP_ID_NAME = "umlpId";
  
  public static TypeCheck typeCheck = new TypeCheck();

  public static ASTExpression createInitialValue(SymTypeExpression type) {
    ASTNameExpressionBuilder exprBuilder = UMLPMill.nameExpressionBuilder();
    if (isOptional(type)) {
      exprBuilder.setName("Optional.empty()");
    } else if (isList(type)) {
      exprBuilder.setName("new LinkedList<>()");
    } else if (isSet(type)) {
      exprBuilder.setName("new HashSet<>()");
    } else if (typeCheck.isInt(type)) {
      exprBuilder.setName(String.valueOf(Defaults.defaultValue(int.class)));
    } else if (typeCheck.isDouble(type)) {
      exprBuilder.setName(String.valueOf(Defaults.defaultValue(double.class)));
    } else if (typeCheck.isChar(type)) {
      exprBuilder.setName("\'\\" + "u0000\'");
    } else if (typeCheck.isShort(type)) {
      exprBuilder.setName(String.valueOf(Defaults.defaultValue(short.class)));
    } else if (typeCheck.isFloat(type)) {
      exprBuilder.setName(String.valueOf(Defaults.defaultValue(float.class)) + "f");
    } else if (typeCheck.isLong(type)) {
      exprBuilder.setName(String.valueOf(Defaults.defaultValue(long.class)) + "L");
    } else if (typeCheck.isBoolean(type)) {
      exprBuilder.setName(String.valueOf(Defaults.defaultValue(boolean.class)));
    } else if (typeCheck.isByte(type)) {
      exprBuilder.setName(String.valueOf(Defaults.defaultValue(byte.class)));
    } else if ("String".equals(type.print())) {
      exprBuilder.setName("\"\" ");
    }
    return exprBuilder.build();
  }

  public static String calculateJSon(ASTCDAttribute attr) {
    String json = "";
    SymTypeExpression type = attr.getSymbol().getType();
    if (TypeCheck.isBoolean(type)) {
      json = "getAsJsonBoolean().getValue()";
    } else if (TypeCheck.isInt(type)) {
      json = "getAsJsonNumber().getNumberAsInteger()";
    } else if (TypeCheck.isFloat(type)) {
      json = "getAsJsonNumber().getNumberAsFloat()";
    } else if (TypeCheck.isLong(type)) {
      json = "getAsJsonNumber().getNumberAsLong()";
    } else if (TypeCheck.isDouble(type)) {
      json = "getAsJsonNumber().getNumberAsDouble()";
    } else if (TypeCheck.isString(type)) {
      json = "getAsJsonString().getValue()";
    } else {
      // TODO
    }
    return json;
  }

  public static ASTExpression createInitialValue(ASTMCType type) {
    FullSynthesizeFromCD4Code synthesize = new FullSynthesizeFromCD4Code();
    TypeCheckResult result = synthesize.synthesizeType(type);
    if (result.isPresentResult()) {
      return createInitialValue(result.getResult());
    }
    Log.error("");
    return null;
  }
  public static ASTMCType createType(ASTCDAttribute attr) {
    if (attr.getSymbol().getType().isGenericType()) {
      SymTypeOfGenerics genericType = (SymTypeOfGenerics) attr.getSymbol().getType();
      List<String> arguments = genericType.getArgumentList().stream().
              map(a -> box(a.print())).collect(Collectors.toList());
      return MCTypeFacade.getInstance().createBasicGenericTypeOf(genericType.getBaseName(), arguments.toArray(new String[0]));
    } else {
      return MCTypeFacade.getInstance().createQualifiedType(box(getType(attr.getSymbol().getType())));
    }
  }

  public static String getType(SymTypeExpression symType) {
    if (symType.isGenericType()) {
      return ((SymTypeOfGenerics) symType).getBaseName();
    }
    if (isCDType(symType)) {
      return symType.print();
    }
    return symType.getTypeInfo().getFullName();
  }

  public static boolean isVisible(ASTCDAttribute attr) {
    return !attr.getModifier().isFinal()
            && !attr.getModifier().isStatic();
  }

  public static boolean isVisibleNotDerived(ASTCDAttribute attr) {
    return !attr.getModifier().isDerived()
            && !attr.getModifier().isFinal()
            && !attr.getModifier().isStatic();
  }

  public static boolean isDerived(ASTCDAttribute attr) {
    return attr.getModifier().isDerived();
  }

  public static boolean isGem(ASTCDAttribute attr) {
    if (attr.getMCType() instanceof ASTMCBasicGenericType) {
      List<String> nameList = ((ASTMCBasicGenericType) attr.getMCType()).getNameList();
      return "Gem".equals(nameList.get(nameList.size()-1)) || "GemList".equals(nameList.get(nameList.size()-1));
    }
    return false;
  }

  public static String getGetterName(ASTCDAttribute attr) {
    return (TypeCheck.isBoolean(attr.getSymbol().getType())?"is":"get")
            + StringTransformations.capitalize(attr.getName());
  }

  public static boolean isGemList(ASTCDAttribute attr) {
    if (attr.getMCType() instanceof ASTMCBasicGenericType) {
      List<String> nameList = ((ASTMCBasicGenericType) attr.getMCType()).getNameList();
      return "GemList".equals(nameList.get(nameList.size()-1));
    }
    return false;
  }

  public static boolean isList(ASTCDAttribute attr) {
    ASTMCType mcType = attr.getMCType();
    return isList(mcType);
  }

  public static boolean isList(ASTMCType mcType) {
    if(mcType instanceof ASTMCListType){
      return true;
    }
    if (mcType instanceof ASTMCBasicGenericType) {
      List<String> nameList = ((ASTMCBasicGenericType) mcType).getNameList();
      return "List".equals(nameList.get(nameList.size() - 1));
    }
    if(mcType instanceof ASTMCQualifiedType){
      List<String> nameList = ((ASTMCQualifiedType) mcType).getNameList();
      return "List".equals(nameList.get(nameList.size() - 1));
    }
    return false;
  }

  public static boolean isOptional(ASTCDAttribute attr) {
    ASTMCType mcType = attr.getMCType();
    return isOptional(mcType);
  }

  private static boolean isOptional(ASTMCType mcType) {
    if(mcType instanceof ASTMCOptionalType){
      return true;
    }

    if (mcType instanceof ASTMCBasicGenericType) {
      List<String> nameList = ((ASTMCBasicGenericType) mcType).getNameList();
      return "Optional".equals(nameList.get(nameList.size() - 1));
    }
    return false;
  }


  public static boolean isVisible(ASTCDAssocSide attr) {
    return !attr.getModifier().isFinal()
            && !attr.getModifier().isStatic();
  }

  public static boolean isVisibleNotDerived(ASTCDAssocSide attr) {
    return !attr.getModifier().isDerived()
            && !attr.getModifier().isFinal()
            && !attr.getModifier().isStatic();
  }

  public static boolean isDerived(ASTCDAssocSide attr) {
    return attr.getModifier().isDerived();
  }

  public static boolean isList(ASTCDAssocSide assoc) {
    return assoc.isPresentCDCardinality()?assoc.getCDCardinality().isMult():false;
  }

  public static String getInnerType(ASTMCType mcType){
    UMLPFullPrettyPrinter prettyPrinter = new UMLPFullPrettyPrinter();
    if(!isList(mcType) && !isOptional(mcType)) {
      Log.error("Not a list or optional type: " + prettyPrinter.prettyprint(mcType));
      return null;
    }

    if (mcType instanceof ASTMCBasicGenericType) {
      ASTMCBasicGenericType genType = (ASTMCBasicGenericType) mcType;
      return prettyPrinter.prettyprint(genType.getMCTypeArgument(0).getMCTypeOpt().get());
    }

    if(mcType instanceof ASTMCListType){
      ASTMCListType lt = (ASTMCListType) mcType;
      if(!lt.getMCTypeArgumentList().isEmpty()) {
        return prettyPrinter.prettyprint(lt.getMCTypeArgumentList().get(0).getMCTypeOpt().get());
      }
    }

    if(mcType instanceof ASTMCOptionalType){
      ASTMCOptionalType ot = (ASTMCOptionalType) mcType;
      if(!ot.getMCTypeArgumentList().isEmpty()){
        return prettyPrinter.prettyprint(ot.getMCTypeArgumentList().get(0).getMCTypeOpt().get());
      }
    }

    return "java.lang.Object";
  }

  /***************************************************************************/
  /* TODO: Nach TypeCheck verschieben?                                       */
  /***************************************************************************/

  public static boolean isOptional(SymTypeExpression type) {
    if (type instanceof SymTypeOfGenerics) {
      return ((SymTypeOfGenerics) type).getBaseName().equals("Optional");
    }
    return false;
  }

  public static boolean isList(SymTypeExpression type) {
    if (type instanceof SymTypeOfGenerics) {
      return ((SymTypeOfGenerics) type).getBaseName().equals("List");
    }
    return false;
  }

  public static boolean isSet(SymTypeExpression type) {
    if (type instanceof SymTypeOfGenerics) {
      return ((SymTypeOfGenerics) type).getBaseName().equals("Set");
    }
    return false;
  }

  public static boolean isCDType(SymTypeExpression type) {
    return type.getTypeInfo() instanceof CDTypeSymbol;
  }
}
