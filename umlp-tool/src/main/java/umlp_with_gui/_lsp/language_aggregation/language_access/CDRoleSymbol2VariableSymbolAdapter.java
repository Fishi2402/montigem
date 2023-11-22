package umlp_with_gui._lsp.language_aggregation.language_access;

import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.cdassociation._symboltable.ICDAssociationScope;
import de.monticore.mcbasics._visitor.MCBasicsTraverser;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols._visitor.BasicSymbolsTraverser;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.visitor.ITraverser;

class CDRoleSymbol2VariableSymbolAdapter extends VariableSymbol {
  protected final CDRoleSymbol delegate;

  public CDRoleSymbol2VariableSymbolAdapter(CDRoleSymbol roleSymbol) {
    super(roleSymbol.getName());
    delegate = roleSymbol;
  }

  @Override
  public SymTypeExpression getType() {
    return delegate.getType();
  }

  @Override
  public void setType(SymTypeExpression type) {
    delegate.setType(type);
  }

  @Override
  public boolean isIsReadOnly() {
    return delegate.isIsReadOnly();
  }

  @Override
  public void setIsReadOnly(boolean isReadOnly) {
    delegate.setIsReadOnly(isReadOnly);
  }

  @Override
  public String getName() {
    return delegate.getName();
  }

  @Override
  public void setName(String name) {
    delegate.setName(name);
  }

  @Override
  public ICDAssociationScope getEnclosingScope() {
    return delegate.getEnclosingScope();
  }

  @Override
  public boolean isPresentAstNode() {
    return false;
  }

  @Override
  public AccessModifier getAccessModifier() {
    return delegate.getAccessModifier();
  }

  @Override
  public void setAccessModifier(AccessModifier accessModifier) {
    delegate.setAccessModifier(accessModifier);
  }

  @Override
  public void setFullName(String fullName) {
    delegate.setFullName(fullName);
  }

  @Override
  public String getFullName() {
    return delegate.getFullName();
  }

  @Override
  public void setPackageName(String packageName) {
    delegate.setPackageName(packageName);
  }

  @Override
  public String getPackageName() {
    return delegate.getPackageName();
  }

  @Override
  public void accept(MCBasicsTraverser visitor) {
    delegate.accept(visitor);
  }

  @Override
  public void accept(BasicSymbolsTraverser visitor) {
    delegate.accept(visitor);
  }

  @Override
  public void accept(ITraverser visitor) {
    delegate.accept(visitor);
  }
}
