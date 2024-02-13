package umlp._symboltable;

import de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer;
import de.monticore.symboltable.serialization.json.JsonObject;

public class FieldSymbolDeSer4GUI extends FieldSymbolDeSer {
  @Override
  protected boolean deserializeIsPublic(JsonObject symbolJson) {
    // GUI model doesn't care if a property has a private modifier
    return true;
  }

}
