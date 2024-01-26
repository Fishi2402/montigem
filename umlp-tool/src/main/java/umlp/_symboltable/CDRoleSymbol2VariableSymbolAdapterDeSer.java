package umlp._symboltable;

import de.monticore.cdassociation._symboltable.CDRoleSymbolDeSer;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolDeSer;

public class CDRoleSymbol2VariableSymbolAdapterDeSer extends VariableSymbolDeSer {

  @Override
  public  de.monticore.symbols.basicsymbols._symboltable.VariableSymbol deserialize (de.monticore.symboltable.serialization.json.JsonObject symbolJson) {
    CDRoleSymbolDeSer cdRoleSymbolDeSer = new CDRoleSymbolDeSer();
    return new CDRoleSymbol2VariableSymbolAdapter(cdRoleSymbolDeSer.deserialize(symbolJson));
  }
}
