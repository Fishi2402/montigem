package umlp_with_gui._lsp.language_aggregation;

import de.mclsg.lsp.FileExtensionAlreadyRegisteredException;
import de.monticore.guidsl._lsp.GUIDSLLanguageServer;
import de.monticore.io.paths.MCPath;
import umlp_with_gui._lsp.language_aggregation.language_access.GUIDSLScopeManager4Aggr;


public class UMLPWithGUILanguageAggrServer extends UMLPWithGUILanguageAggrServerTOP {
  public UMLPWithGUILanguageAggrServer(MCPath path) throws FileExtensionAlreadyRegisteredException {
    super(path);
  }

  @Override
  protected GUIDSLLanguageServer initGUIDSLLanguageServer(MCPath path){
    GUIDSLLanguageServer server = new GUIDSLLanguageServer(documentManager, path, new GUIDSLScopeManager4Aggr(this.uMLPLanguageServer.getContext()), symbolUsageResolutionProvider);
    server.getContext().noLockClose(); // initializing a subserver leaves its context open, so close
    return server;
  }
}
