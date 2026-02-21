package umlp_with_gui._lsp.language_aggregation;

import de.mclsg.lsp.FileExtensionAlreadyRegisteredException;
import de.mclsg.lsp.LazySymbolUsageResolutionProvider;
import de.mclsg.lsp.modelpath.multiproject.ProjectLayout;
import de.mclsg.lsp.util.LazyLanguageServerContext;
import de.monticore.guidsl._lsp.GUIDSLLanguageServer;
import de.monticore.guidsl._lsp.language_access.GUIDSLLanguageAccess;
import de.monticore.guidsl._lsp.language_access.GUIDSLLspCoCoRunner;
import umlp_with_gui._lsp.language_aggregation.language_access.UMLPWithGUILanguageAggrScopeManager4GUIDSL;


public class UMLPWithGUILanguageAggrServer extends UMLPWithGUILanguageAggrServerTOP {
  public UMLPWithGUILanguageAggrServer(ProjectLayout layout) throws FileExtensionAlreadyRegisteredException {
    super(layout);
  }

  protected GUIDSLLanguageServer initGUIDSLLanguageServer(ProjectLayout layout){
    GUIDSLLanguageServer server =  new GUIDSLLanguageServer(
        documentManager,
        layout,
        new UMLPWithGUILanguageAggrScopeManager4GUIDSL(
            new LazyLanguageServerContext(() -> uMLPLanguageServer.getContext()),
            new LazyLanguageServerContext(() -> gUIDSLLanguageServer.getContext())
        ),
        new LazySymbolUsageResolutionProvider(() -> symbolUsageResolutionProvider)
    ){
      @Override
      protected GUIDSLLanguageAccess initLanguageAccess() {
        return new GUIDSLLanguageAccess(documentManager, scopeManager){
          @Override
          protected GUIDSLLspCoCoRunner initializeLspCoCoRunner() {
            return new GUIDSLLspCoCo4UMLPRunner(documentManager);
          }
        };
      }
    };
    return server;
  }
}
