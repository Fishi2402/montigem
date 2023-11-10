package umlp.generator.validator.ocl.typecheck;

import de.monticore.ocl.types3.util.OCLNameExpressionTypeCalculator;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.mccollectiontypes.types3.util.MCCollectionSymTypeFactory;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CustomOCLNameExpressionTypeCalculator extends OCLNameExpressionTypeCalculator {
    private final Map<String, List<String>> enumMap;

    public CustomOCLNameExpressionTypeCalculator(Map<String, List<String>> enumMap) {
      super();
      this.enumMap = enumMap;
    }

  @Override
  public Optional<SymTypeExpression> resolveNameAsExpr(
          IBasicSymbolsScope enclosingScope, String name) {
    if(enumMap.containsKey(name)) {
      TypeSymbolSurrogate typeSymbolSurrogate = new TypeSymbolSurrogate(name);
      typeSymbolSurrogate.setEnclosingScope(enclosingScope);
      return Optional.of(new SymTypeOfObject(typeSymbolSurrogate));
    }
    // case "normal" expression
    Optional<SymTypeExpression> type = super.resolveNameAsExpr(enclosingScope, name);
    // case type id -> create Set of the same type
    if (type.isEmpty()) {
      Optional<SymTypeExpression> typeId = super.resolveType(enclosingScope, name);
      if (typeId.isPresent()) {
        type = Optional.of(MCCollectionSymTypeFactory.createSet(typeId.get().deepClone()));
      }
    }
    return type;
  }
}
