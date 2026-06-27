package umlp.generator.ocl.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.Names;
import umlp.generator.ocl.TypeUtil;

import java.util.ArrayList;
import java.util.List;

public class SymTypeExpression2UMLPJavaConverter extends SymTypeExpression2JavaConverter {

  public static void init(){
    delegate = new SymTypeExpression2UMLPJavaConverter();
  }

  @Override
  protected String _getJavaTypePrint(SymTypeExpression modelType) {
    return super._getJavaTypePrint(toUMLPJavaType(modelType));
  }

  @Override
  protected String _getBoxedJavaTypePrint(SymTypeExpression modelType) {
    return super._getBoxedJavaTypePrint(toUMLPJavaType(modelType));
  }

  @Override
  protected String _getTypeErasedJavaTypePrint(SymTypeExpression modelType) {
    return super._getTypeErasedJavaTypePrint(toUMLPJavaType(modelType));
  }

  @Override
  protected String _getJavaTypeQName(SymTypeExpression modelType) {
    return super._getJavaTypeQName(toUMLPJavaType(modelType));
  }

  protected SymTypeExpression toUMLPJavaType(SymTypeExpression modelType) {
    Preconditions.checkNotNull(modelType);

    if(TypeUtil.getInstance().isDomainType(modelType) && modelType.isObjectType()){
      String fn = modelType.getTypeInfo().getFullName();
      if(fn.contains(".")){
        fn = Names.getQualifier(fn).toLowerCase() + "." + Names.getSimpleName(fn);
      }

      return SymTypeExpressionFactory.createTypeObject(new TypeSymbol(fn));
    }
    else if(modelType.isGenericType()){
      List<SymTypeExpression> newArgs = new ArrayList<>();
      for(SymTypeExpression arg : modelType.asGenericType().getArgumentList()){
        newArgs.add(toUMLPJavaType(arg));
      }
      return SymTypeExpressionFactory.createGenerics(modelType.getTypeInfo(), newArgs);
    }
    else if(modelType.isArrayType()){
      return SymTypeExpressionFactory.createTypeArray(
          toUMLPJavaType(modelType.asArrayType().getArgument()),
          modelType.asArrayType().getDim()
      );
    }
    else if(modelType.isWildcard() && modelType.asWildcard().hasBound()){
      return SymTypeExpressionFactory.createWildcard(
          modelType.asWildcard().isUpper(),
          toUMLPJavaType(modelType.asWildcard().getBound())
      );
    }

    return modelType;
  }
}
