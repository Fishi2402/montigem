package umlp.generator.ocl.javagen;

import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import umlp.generator.ocl.TypeUtil;

import java.util.ArrayList;
import java.util.List;

public class SymTypeExpression2UMLPJavaConverter extends SymTypeExpression2JavaConverter {

  public static void init(){
    delegate = new SymTypeExpression2UMLPJavaConverter();
  }

  @Override
  protected SymTypeExpression _getAsJavaType(SymTypeExpression modelType) {
    SymTypeExpression tmp = super._getAsJavaType(modelType);
    if(TypeUtil.getInstance().isDomainType(tmp)){
      if(tmp.isObjectType()){
        TypeSymbol ti = tmp.getTypeInfo();
        String packagePrefix = "";
        String fn = ti.getFullName();
        if(fn.contains(".")){
          int lastPoint = fn.lastIndexOf('.');
          packagePrefix = fn.substring(0, lastPoint).toLowerCase() + ".";
        }

        tmp = SymTypeExpressionFactory.createTypeObject(new TypeSymbol(
            packagePrefix + ti.getName()
        ));
      } else if(tmp.isGenericType()){
        List<SymTypeExpression> newArgs = new ArrayList<>();
        // Recursively convert all generic arguments (e.g. the T in List<T>)
        for(SymTypeExpression arg : tmp.asGenericType().getArgumentList()){
          newArgs.add(_getAsJavaType(arg));
        }
        // Reconstruct the generic type with the newly converted arguments
        tmp = SymTypeExpressionFactory.createGenerics(tmp.getTypeInfo(), newArgs);
      }
    }

    return tmp;
  }
}
