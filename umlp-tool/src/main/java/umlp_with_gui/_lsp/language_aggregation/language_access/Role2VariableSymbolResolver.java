package umlp_with_gui._lsp.language_aggregation.language_access;

import de.mclsg.lsp.util.LanguageServerContext;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdassociation._symboltable.CDRoleSymbol;
import de.monticore.symbols.basicsymbols._symboltable.IVariableSymbolResolver;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

class Role2VariableSymbolResolver implements IVariableSymbolResolver {
  protected final LanguageServerContext umlpLanguageServerContext;

  public Role2VariableSymbolResolver(LanguageServerContext umlpLanguageServerContext) {
    this.umlpLanguageServerContext = umlpLanguageServerContext;
  }

  @Override
  public List<VariableSymbol> resolveAdaptedVariableSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<VariableSymbol> predicate) {
    List<VariableSymbol> res = new ArrayList<>();
    for (CDRoleSymbol roleSymbol : CD4AnalysisMill.globalScope().resolveCDRoleMany(foundSymbols, name, modifier)) {
      VariableSymbol variableSymbol = new CDRoleSymbol2VariableSymbolAdapter(roleSymbol);

      if(predicate.test(variableSymbol)) {
        res.add(variableSymbol);
      }
    }
    return res;
  }
}
