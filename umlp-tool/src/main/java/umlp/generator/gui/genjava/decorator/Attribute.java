package umlp.generator.gui.genjava.decorator;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Attribute {
  protected final ASTCDAttribute cdAttr;
  protected final ASTMCType baseType;
  protected final List<VariableSymbol> iterateArgs;

  protected final Collection<String> importList;

  public Attribute(
      ASTCDAttribute cdAttr, ASTMCType baseType,
      List<VariableSymbol> iterateArgs, Collection<String> importList) {
    this.cdAttr = cdAttr;
    this.iterateArgs = iterateArgs;
    this.baseType = baseType;
    this.importList = importList;
  }

  public ASTCDAttribute getCdAttr() {
    return cdAttr;
  }

  public ASTMCType getBaseType() {
    return baseType;
  }

  public List<VariableSymbol> getIterateArgs() {
    return iterateArgs;
  }

  public Collection<String> getImportList() {
    return importList;
  }

}