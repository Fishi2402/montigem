package umlp._lsp.language_access;

import de.monticore.cd4analysis._symboltable.CD4AnalysisSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import umlp.UMLPMill;
import umlp._symboltable.IUMLPArtifactScope;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UMLPScopeManager extends UMLPScopeManagerTOP {

  @Override
  public void initGlobalScope(MCPath modelPath) {
    UMLPMill.init();
    super.initGlobalScope(modelPath);
    syncAccessGlobalScope(gs -> BasicSymbolsMill.initializePrimitives());
  }

  @Override
  public UMLPArtifactScopeWithFindings createArtifactScope(ASTCDCompilationUnit ast, IUMLPArtifactScope oldArtifactScope) {
    UMLPArtifactScopeWithFindings res = super.createArtifactScope(ast, oldArtifactScope);
    ast.accept(new CD4AnalysisSymbolTableCompleter(ast).getTraverser());
    res.findings.addAll(Log.getFindings());
    return res;
  }

  @Override
  public Map<ASTCDCompilationUnit, UMLPArtifactScopeWithFindings> createAllArtifactScopes(Collection<ASTCDCompilationUnit> astNodes) {
    final Map<ASTCDCompilationUnit, UMLPArtifactScopeWithFindings> res = new HashMap<>();
    syncAccessGlobalScope(gs -> {
      clearGlobalScope();
      if(supportsIterativeScopeAppending()){
        for(ASTCDCompilationUnit node : astNodes){
          Log.getFindings().clear();
          // Use super.createArtifactScope, so that the completer is not used
          res.put(node, super.createArtifactScope(node, null));
        }

        // Phase 2: complete symbol table when base structure is finished
        for(ASTCDCompilationUnit ast : astNodes){
          Log.getFindings().clear();
          ast.accept(new CD4AnalysisSymbolTableCompleter(ast).getTraverser());
          res.get(ast).findings.addAll(Log.getFindings());
        }
      }
    });
    return res;
  }
}
