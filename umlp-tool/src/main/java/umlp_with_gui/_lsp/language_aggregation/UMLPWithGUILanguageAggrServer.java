package umlp_with_gui._lsp.language_aggregation;

import de.mclsg.lsp.FileExtensionAlreadyRegisteredException;
import de.monticore.guidsl._lsp.GUIDSLLanguageServer;
import de.monticore.io.paths.MCPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umlp_with_gui._lsp.language_aggregation.language_access.GUIDSLScopeManager4Aggr;


public class UMLPWithGUILanguageAggrServer extends UMLPWithGUILanguageAggrServerTOP {
  private static final Logger logger = LoggerFactory.getLogger(UMLPWithGUILanguageAggrServer.class);
  public UMLPWithGUILanguageAggrServer(MCPath path) throws FileExtensionAlreadyRegisteredException {
    super(path);
    logger.trace("Initialized UMLPWithGUILanguageAggrServer");
  }

  @Override
  protected GUIDSLLanguageServer initGUIDSLLanguageServer(MCPath path){
    GUIDSLLanguageServer server = new GUIDSLLanguageServer(documentManager, path, new GUIDSLScopeManager4Aggr(this.uMLPLanguageServer.getContext()), symbolUsageResolutionProvider);
    server.getContext().noLockClose(); // initializing a subserver leaves its context open, so close
    return server;
  }
}
