package umlp._symboltable;

import de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;

public class FieldSymbolDeSer4GUI extends FieldSymbolDeSer {
  @Override
  protected boolean deserializeIsPublic(JsonObject symbolJson) {
    // GUI model doesn't care if a property has a private modifier
    return true;
  }

  @Override
  public SymTypeExpression deserializeType(JsonObject symbolJson) {
    return SymTypeExpressionDeSer4GUI.deserializeMember("type", symbolJson);
  }

}
