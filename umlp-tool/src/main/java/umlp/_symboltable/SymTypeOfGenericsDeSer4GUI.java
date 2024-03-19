package umlp._symboltable;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfGenericsDeSer;
import de.se_rwth.commons.logging.Log;

import java.util.List;

public class SymTypeOfGenericsDeSer4GUI extends SymTypeOfGenericsDeSer {

  @Override
  public SymTypeOfGenerics deserialize(JsonObject serialized) {
    if (serialized.hasStringMember(SERIALIZED_TYPE_CONSTRUCTOR) &&
        serialized.hasArrayMember(SERIALIZED_ARGUMENTS)) {
      String typeConstructorFullName = serialized.getStringMember(SERIALIZED_TYPE_CONSTRUCTOR);
      // adapted part start
      if (typeConstructorFullName.equals("Optional")
          || typeConstructorFullName.equals("List")
          || typeConstructorFullName.equals("Map")
          || typeConstructorFullName.equals("Set")) {
        typeConstructorFullName = "java.util." + typeConstructorFullName;
      }
      // adapted part end
      IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

      List<SymTypeExpression> arguments = SymTypeExpressionDeSer
          .deserializeListMember(SERIALIZED_ARGUMENTS, serialized);

      return SymTypeExpressionFactory
          .createGenerics(typeConstructorFullName, gs, arguments);
    }
    Log.error(
        "0x823F6 Internal error: Loading ill-structured SymTab: missing typeConstructorFullName of SymTypeOfGenerics "
            + serialized);
    return null;
  }

}
