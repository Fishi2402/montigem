package umlp_with_gui._lsp.language_aggregation.multiproject;

import de.mclsg.lsp.FileExtensionAlreadyRegisteredException;
import de.mclsg.lsp.modelpath.multiproject.SubprojectLayout;
import de.monticore.guidsl._lsp.GUIDSLLanguageServer;
import de.monticore.guidsl._lsp.GUIDSLLanguageServerBuilder;
import de.monticore.guidsl._lsp.multiproject.GUIDSLSubprojectIndexingManager;
import de.monticore.io.paths.MCPath;
import umlp_with_gui._lsp.language_aggregation.language_access.GUIDSLScopeManager4Aggr;

import java.util.List;

public class UMLPWithGUILanguageAggrServerForMultiProject extends UMLPWithGUILanguageAggrServerForMultiProjectTOP {
  public UMLPWithGUILanguageAggrServerForMultiProject(SubprojectLayout layout, MCPath rootPath, List<String> ignoredDirs) throws FileExtensionAlreadyRegisteredException {
    super(layout, rootPath, ignoredDirs);
  }

  @Override
  protected GUIDSLLanguageServer initGUIDSLLanguageServer(MCPath path){
    GUIDSLScopeManager4Aggr scopeManager = new GUIDSLScopeManager4Aggr(this.uMLPLanguageServer.getContext());

    GUIDSLLanguageServer server = new GUIDSLLanguageServerBuilder()
        .documentManager(documentManager)
        .symbolUsageResolutionProvider(symbolUsageResolutionProvider)
        .scopeManager(scopeManager)
        .build();
    server.setIndexingManager(new GUIDSLSubprojectIndexingManager(
        layout,
        documentManager,
        server.getTextDocumentService().getDocumentInformationProvider(),
        rootPath,
        ignoredDirs,
        server
    ));
    server.getContext().noLockClose(); // initializing a subserver leaves its context open, so close
    return server;
  }

  @Override
  protected boolean automaticallyRunIndexing() {
    return false;
  }
}
