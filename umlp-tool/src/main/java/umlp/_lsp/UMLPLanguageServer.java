package umlp._lsp;

import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cd4analysis._lsp.features.code_action.CollapseHierarchyCodeActionStrategy;
import de.monticore.cd4analysis._lsp.features.code_action.ExtractSuperClassCodeActionStrategy;
import de.monticore.cd4analysis._lsp.features.code_action.PullUpFieldCodeActionStrategy;
import de.monticore.cd4analysis._lsp.features.code_action.UnfoldByAttributesStrategy;
import de.monticore.io.paths.MCPath;
import umlp._lsp.features.code_action.UMLPCodeActionProvider;
import umlp._lsp.language_access.UMLPScopeManager;

public class UMLPLanguageServer extends UMLPLanguageServerTOP{
  public UMLPLanguageServer(DocumentManager documentManager, MCPath modelPath, UMLPScopeManager scopeManager, ISymbolUsageResolutionProvider symbolUsageResolutionProvider) {
    super(documentManager, modelPath, scopeManager, symbolUsageResolutionProvider);

    UMLPCodeActionProvider codeActionProvider = new UMLPCodeActionProvider(documentManager, languageAccess.getPrettyPrinter().orElseThrow());
    codeActionProvider.addCodeActionStrategy(new PullUpFieldCodeActionStrategy(documentManager, symbolUsageResolutionProvider, languageAccess.getPrettyPrinter().orElseThrow()));
    codeActionProvider.addCodeActionStrategy(new ExtractSuperClassCodeActionStrategy(documentManager, languageAccess.getPrettyPrinter().orElseThrow()));
    codeActionProvider.addCodeActionStrategy(new CollapseHierarchyCodeActionStrategy(documentManager, languageAccess.getPrettyPrinter().orElseThrow(), symbolUsageResolutionProvider));
    codeActionProvider.addCodeActionStrategy(new UnfoldByAttributesStrategy(documentManager, languageAccess.getPrettyPrinter().orElseThrow()));
    textDocumentService.register(codeActionProvider);
  }
}
