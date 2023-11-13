/* (c) https://github.com/MontiCore/monticore */

package umlp._lsp.features.completion;

import de.mclsg.lsp.CommonLanguageServer;
import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentManager;
import de.monticore.cd4analysis._lsp.features.completion.strategy.CD4AnalysisAssociationCardinalityCompletionStrategy;
import de.monticore.cd4analysis._lsp.features.completion.strategy.CD4AnalysisAssociationCompletionStrategy;
import de.monticore.cd4analysis._lsp.features.completion.strategy.CD4AnalysisAssociationNavigationCompletionStrategy;
import umlp._lsp.language_access.UMLPLanguageAccess;

public class UMLPCompletionProvider extends UMLPCompletionProviderTOP {
  public UMLPCompletionProvider(CommonLanguageServer languageServer, DocumentManager documentManager, UMLPLanguageAccess languageAccess, ISymbolUsageResolutionProvider symbolUsageResolutionProvider) {
    super(languageServer, documentManager, languageAccess, symbolUsageResolutionProvider);

    completionStrategyManager.registerCompletionStrategy(new CD4AnalysisAssociationCompletionStrategy(symbolUsageResolutionProvider, documentManager));
    completionStrategyManager.registerCompletionStrategy(new CD4AnalysisAssociationCardinalityCompletionStrategy(symbolUsageResolutionProvider, documentManager));
    completionStrategyManager.registerCompletionStrategy(new CD4AnalysisAssociationNavigationCompletionStrategy(symbolUsageResolutionProvider, documentManager));
  }
}
