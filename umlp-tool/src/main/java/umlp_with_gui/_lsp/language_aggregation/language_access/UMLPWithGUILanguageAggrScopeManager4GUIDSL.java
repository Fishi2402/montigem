package umlp_with_gui._lsp.language_aggregation.language_access;

import de.monticore.symbols.basicsymbols._symboltable.ITypeSymbolResolver;
import de.monticore.symbols.basicsymbols._symboltable.IVariableSymbolResolver;

import java.util.List;
import java.util.stream.Collectors;
import de.mclsg.lsp.util.LanguageServerContext;
import de.monticore.cd4analysis.CD4AnalysisMill;

public class UMLPWithGUILanguageAggrScopeManager4GUIDSL extends UMLPWithGUILanguageAggrScopeManager4GUIDSLTOP {
  protected final ITypeSymbolResolver cd4aTypeResolver;
  protected final IVariableSymbolResolver cd4aFieldResolver;
  protected final Role2VariableSymbolResolver role2VariableSymbolResolver;
  protected final List<String> primitiveTypes = List.of("void", "boolean", "char", "byte", "short", "int", "long", "float", "double");


  public UMLPWithGUILanguageAggrScopeManager4GUIDSL(LanguageServerContext uMLPContext, LanguageServerContext gUIDSLContext) {
    super(uMLPContext, gUIDSLContext);

    cd4aTypeResolver = (foundSymbols, name, modifier, predicate) -> {
      try(var context = this.uMLPContext.open()) {
        //noinspect globalScopeAccess
        return primitiveTypes.contains(name) ? List.of() : CDTypeAsTypeAdapter.from(CD4AnalysisMill.globalScope().resolveCDTypeMany(foundSymbols, name, modifier, cdts -> predicate.test(new CDTypeAsTypeAdapter(cdts))));
      }
    };
    cd4aFieldResolver = (foundSymbols, name, modifier, predicate) -> {
      try(var context = this.uMLPContext.open()) {
        //noinspect globalScopeAccess
        return CD4AnalysisMill.globalScope().resolveFieldMany(foundSymbols, name, modifier).stream().filter(predicate).collect(Collectors.toList());
      }
    };
    // methodSignatureResolver = (foundSymbols, name, modifier, predicate) -> CD4CodeMill.globalScope().resolveCDMethodSignatureMany(foundSymbols, name, modifier).stream().filter(predicate).collect(Collectors.toList());
    role2VariableSymbolResolver = new Role2VariableSymbolResolver(uMLPContext);
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
