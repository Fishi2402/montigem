package umlp.generator.gui.genjava.decorator;

import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Collection;
import java.util.List;

public class Attribute {
  protected final ASTCDAttribute cdAttr;
  protected final ASTMCType baseType;
  protected final List<ASTCDParameter> params;

  protected final Collection<String> importList;

  public Attribute(
      ASTCDAttribute cdAttr, ASTMCType baseType,
      List<ASTCDParameter> params, Collection<String> importList
  ) {
    this.cdAttr = cdAttr;
    this.params = params;
    this.baseType = baseType;
    this.importList = importList;
  }

  public ASTCDAttribute getCdAttr() {
    return cdAttr;
  }

  public ASTMCType getBaseType() {
    return baseType;
  }

  public List<ASTCDParameter> getParams() {
    return params;
  }

  public Collection<String> getImportList() {
    return importList;
  }

}