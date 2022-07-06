package umlp.generator.decorator;

import com.google.common.base.Defaults;
import de.monticore.cd4code.typescalculator.FullSynthesizeFromCD4Code;
import de.monticore.cdassociation._ast.ASTCDAssocSide;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpressionBuilder;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPMill;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.types.check.SymTypePrimitive.box;

public class GenService {

  public static final String UMLP_ID_NAME = "umlpId";

  public static ASTExpression createInitialValue(SymTypeExpression type) {
    ASTNameExpressionBuilder exprBuilder = UMLPMill.nameExpressionBuilder();
    if (isOptional(type)) {
      exprBuilder.setName("Optional.empty()");
    } else if (isList(type)) {
      exprBuilder.setName("new LinkedList<>()");
    } else if (isSet(type)) {
      exprBuilder.setName("new HashSet<>()");
    } else if (TypeCheck.isInt(type)) {
      exprBuilder.setName(String.valueOf(Defaults.defaultValue(int.class)));
    } else if (TypeCheck.isDouble(type)) {
      exprBuilder.setName(String.valueOf(Defaults.defaultValue(double.class)));
    } else if (TypeCheck.isChar(type)) {
      exprBuilder.setName("\'\\" + "u0000\'");
    } else if (TypeCheck.isShort(type)) {
      exprBuilder.setName(String.valueOf(Defaults.defaultValue(short.class)));
    } else if (TypeCheck.isFloat(type)) {
      exprBuilder.setName(String.valueOf(Defaults.defaultValue(float.class)) + "f");
    } else if (TypeCheck.isLong(type)) {
      exprBuilder.setName(String.valueOf(Defaults.defaultValue(long.class)) + "L");
    } else if (TypeCheck.isBoolean(type)) {
      exprBuilder.setName(String.valueOf(Defaults.defaultValue(boolean.class)));
    } else if (TypeCheck.isByte(type)) {
      exprBuilder.setName(String.valueOf(Defaults.defaultValue(byte.class)));
    } else if ("String".equals(type.print())) {
      exprBuilder.setName("\"\" ");
    }
    return exprBuilder.build();
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
      return MCTypeFacade.getInstance().createQualifiedType(box(attr.getSymbol().getType().print()));
    }
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
}
