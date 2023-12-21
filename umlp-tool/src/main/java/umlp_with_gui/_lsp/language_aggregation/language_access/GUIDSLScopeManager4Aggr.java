package umlp_with_gui._lsp.language_aggregation.language_access;

import de.mclsg.lsp.util.LanguageServerContext;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.guidsl._lsp.language_access.GUIDSLScopeManager;
import de.monticore.symbols.basicsymbols._symboltable.ITypeSymbolResolver;
import de.monticore.symbols.basicsymbols._symboltable.IVariableSymbolResolver;

import java.util.List;
import java.util.stream.Collectors;

public class GUIDSLScopeManager4Aggr extends GUIDSLScopeManager {
  protected final ITypeSymbolResolver cd4aTypeResolver;
  protected final IVariableSymbolResolver cd4aFieldResolver;
  protected final Role2VariableSymbolResolver role2VariableSymbolResolver;
  protected final List<String> primitiveTypes = List.of("void", "boolean", "char", "byte", "short", "int", "long", "float", "double");

  protected final LanguageServerContext umlpLanguageServerContext;

  public GUIDSLScopeManager4Aggr(LanguageServerContext umlpLanguageServerContext) {
    this.umlpLanguageServerContext = umlpLanguageServerContext;
    cd4aTypeResolver = (foundSymbols, name, modifier, predicate) -> {
      try(var context = this.umlpLanguageServerContext.open()) {
        //noinspect globalScopeAccess
        return primitiveTypes.contains(name) ? List.of() : CD4AnalysisMill.globalScope().resolveTypeMany(foundSymbols, name, modifier, predicate);
      }
    };
    cd4aFieldResolver = (foundSymbols, name, modifier, predicate) -> {
      try(var context = this.umlpLanguageServerContext.open()) {
        //noinspect globalScopeAccess
        return CD4AnalysisMill.globalScope().resolveFieldMany(foundSymbols, name, modifier).stream().filter(predicate).collect(Collectors.toList());
      }
    };
    // methodSignatureResolver = (foundSymbols, name, modifier, predicate) -> CD4CodeMill.globalScope().resolveCDMethodSignatureMany(foundSymbols, name, modifier).stream().filter(predicate).collect(Collectors.toList());
    role2VariableSymbolResolver = new Role2VariableSymbolResolver(umlpLanguageServerContext);
  }

  @Override
  protected void adaptGlobalScope() {
    super.adaptGlobalScope();

    syncAccessGlobalScope(gs -> {
      if (!gs.containsAdaptedTypeSymbolResolver(cd4aTypeResolver)) {
        gs.addAdaptedTypeSymbolResolver(cd4aTypeResolver);
      }
      if (!gs.containsAdaptedVariableSymbolResolver(cd4aFieldResolver)) {
        gs.addAdaptedVariableSymbolResolver(cd4aFieldResolver);
      }
      if (!gs.containsAdaptedVariableSymbolResolver(role2VariableSymbolResolver)) {
        gs.addAdaptedVariableSymbolResolver(role2VariableSymbolResolver);
      }
    });
  }
}
