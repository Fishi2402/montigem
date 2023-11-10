package umlp._lsp;

import de.mclsg.lsp.CommonLanguageServer;
import de.mclsg.lsp.ISymbolUsageResolutionProvider;
import de.mclsg.lsp.document_management.DocumentManager;
import org.eclipse.lsp4j.services.LanguageClient;
import umlp._lsp.features.code_lens.UMLPCodeLensProvider;
import umlp._lsp.language_access.UMLPLanguageAccess;

public class UMLPTextDocumentService extends UMLPTextDocumentServiceTOP {
  public UMLPTextDocumentService(CommonLanguageServer languageServer, DocumentManager documentManager, LanguageClient languageClient, UMLPLanguageAccess languageAccess, ISymbolUsageResolutionProvider symbolUsageResolutionProvider) {
    super(languageServer, documentManager, languageClient, languageAccess, symbolUsageResolutionProvider);
  }

  @Override
  protected void registerDefaultCodeLensProvider() {
    register(new UMLPCodeLensProvider(documentManager, referencesProvider, symbolUsageResolutionProvider));
  }
}
