package umlp.generator.gui.genjava.decorator;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.Collection;

public class Attribute {
  protected final ASTCDAttribute cdAttr;
  protected final String baseType;
  protected final Collection<VariableSymbol> iterateArgs;

  public Attribute(ASTCDAttribute cdAttr, String baseType, Collection<VariableSymbol> iterateArgs) {
    this.cdAttr = cdAttr;
    this.iterateArgs = iterateArgs;
    this.baseType = baseType;
  }

  public ASTCDAttribute getCdAttr() {
    return cdAttr;
  }

  public String getBaseType() {
    return baseType;
  }

  public Collection<VariableSymbol> getIterateArgs() {
    return iterateArgs;
  }
}