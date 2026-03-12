package umlp_with_gui._lsp.language_aggregation.language_access;

import de.mclsg.lsp.util.LanguageServerContext;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.guidsl.GUIDSLMill;
import de.monticore.guidsl._ast.ASTGUICompilationUnit;
import de.monticore.guidsl._lsp.language_access.GUIDSLArtifactScopeWithFindings;
import de.monticore.guidsl._symboltable.BuiltInTypes;
import de.monticore.guidsl._symboltable.GUIDSLScopesGenitorP3;
import de.monticore.guidsl._symboltable.IGUIDSLArtifactScope;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.ITypeSymbolResolver;
import de.monticore.symbols.basicsymbols._symboltable.IVariableSymbolResolver;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UMLPWithGUILanguageAggrScopeManager4GUIDSL extends UMLPWithGUILanguageAggrScopeManager4GUIDSLTOP {
  protected final ITypeSymbolResolver cd4aTypeResolver;
  protected final IVariableSymbolResolver cd4aFieldResolver;
  protected final Role2VariableSymbolResolver role2VariableSymbolResolver;
  protected GUIDSLScopesGenitorP3 p3;


  public UMLPWithGUILanguageAggrScopeManager4GUIDSL(LanguageServerContext uMLPContext, LanguageServerContext gUIDSLContext) {
    super(uMLPContext, gUIDSLContext);

    p3 = new GUIDSLScopesGenitorP3();

    cd4aTypeResolver = (foundSymbols, name, modifier, predicate) -> {
      try (var context = this.uMLPContext.open()) {
        if (BasicSymbolsMill.PRIMITIVE_LIST.contains(name)) {
          return List.of();
        } else {
          //noinspect globalScopeAccess
          return OOTypeAsTypeAdapter.from(CD4AnalysisMill.globalScope().resolveOOTypeMany(foundSymbols, name, modifier, cdts -> predicate.test(new OOTypeAsTypeAdapter(cdts))));
        }
      } catch (Throwable t) {
        t.printStackTrace();
        return List.of();
      }
    };
    cd4aFieldResolver = (foundSymbols, name, modifier, predicate) -> {
      try (var context = this.uMLPContext.open()) {
        //noinspect globalScopeAccess
        return CD4AnalysisMill.globalScope().resolveFieldMany(foundSymbols, name, modifier).stream().filter(predicate).collect(Collectors.toList());
      }
    };
    // methodSignatureResolver = (foundSymbols, name, modifier, predicate) -> CD4CodeMill.globalScope().resolveCDMethodSignatureMany(foundSymbols, name, modifier).stream().filter(predicate).collect(Collectors.toList());
    role2VariableSymbolResolver = new Role2VariableSymbolResolver(uMLPContext);
  }

  @Override
  protected void adaptGlobalScope() {
    syncAccessGlobalScope(gs -> {
      // Difference to super: no class2mc resolver
      gs.setAutoLoadGUIModels(false);

      String navigate = "navigate";
      if (gs.resolveFunction(navigate).isEmpty()) {
        FunctionSymbol navigateFunction = GUIDSLMill.functionSymbolBuilder()
            .setName(navigate)
            .setEnclosingScope(gs)
            .setSpannedScope(GUIDSLMill.scope())
            .setType(SymTypeExpressionFactory.createTypeObject(BuiltInTypes.EVENT.getName(), gs))
            .build();
        navigateFunction.getSpannedScope().add(
            GUIDSLMill.variableSymbolBuilder()
                .setName("arg")
                .setType(SymTypeExpressionFactory.createTypeObject(BuiltInTypes.GUI_VIEW_ELEMENT.getName(), gs))
                .setEnclosingScope(navigateFunction.getSpannedScope())
                .build()
        );
        gs.add(navigateFunction);
      }

      if (gs.resolveTypeLocally("int").isEmpty()) {
        BasicSymbolsMill.initializePrimitives();
      }

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

  @Override
  public GUIDSLArtifactScopeWithFindings createArtifactScope(ASTGUICompilationUnit ast, IGUIDSLArtifactScope oldArtifactScope) {
    GUIDSLArtifactScopeWithFindings res = super.createArtifactScope(ast, oldArtifactScope);
    Log.getFindings().clear();
    syncAccessGlobalScope(gs -> {
      p3.createFromAST(res.originalAst);
      res.findings.addAll(Log.getFindings());
    });

    return res;
  }

  @Override
  public Map<ASTGUICompilationUnit, GUIDSLArtifactScopeWithFindings> createAllArtifactScopes(Collection<ASTGUICompilationUnit> astNodes) {
    Map<ASTGUICompilationUnit, GUIDSLArtifactScopeWithFindings> resMap = super.createAllArtifactScopes(astNodes);
    for (GUIDSLArtifactScopeWithFindings res : resMap.values()) {
      Log.getFindings().clear();
      syncAccessGlobalScope(gs -> {
        p3.createFromAST(res.originalAst);
        res.findings.addAll(Log.getFindings());
      });
    }

    return resMap;
  }
}
