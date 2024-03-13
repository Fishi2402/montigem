package umlp._symboltable;

import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mccollectiontypes.types3.util.MCCollectionSymTypeFactory;

public class CDRoleSymbol2VariableSymbolAdapter extends VariableSymbol {

  protected CDRoleSymbol original;

  public CDRoleSymbol2VariableSymbolAdapter(String name) {
    super(name);
  }

  public CDRoleSymbol2VariableSymbolAdapter(CDRoleSymbol original) {
    super(original.getName());
    this.original = original;
  }

  @Override
  public SymTypeExpression getType() {
    if(original.isPresentCardinality() && original.getCardinality().isMult()) {
      return MCCollectionSymTypeFactory.createList(original.getType());
    } else if (original.getCardinality().isOpt()) {
      return MCCollectionSymTypeFactory.createOptional(original.getType());
    }
    return original.getType();
  }
}
