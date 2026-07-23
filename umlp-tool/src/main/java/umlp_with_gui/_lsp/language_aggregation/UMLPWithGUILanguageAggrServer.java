package umlp_with_gui._lsp.language_aggregation;

import de.mclsg.lsp.FileExtensionAlreadyRegisteredException;
import de.mclsg.lsp.LazySymbolUsageResolutionProvider;
import de.mclsg.lsp.modelpath.multiproject.ProjectLayout;
import de.mclsg.lsp.util.LazyLanguageServerContext;
import de.monticore.guidsl._lsp.GUIDSLLanguageServer;
import de.monticore.guidsl._lsp.GUIDSLTextDocumentService;
import de.monticore.guidsl._lsp.language_access.GUIDSLLanguageAccess;
import de.monticore.guidsl._lsp.language_access.GUIDSLLspCoCoRunner;
import org.eclipse.lsp4j.ExecuteCommandOptions;
import org.eclipse.lsp4j.ServerCapabilities;
import umlp._lsp.features.code_lens.EmptyFileCodeLensSupport;
import umlp_with_gui._lsp.features.code_lens.GUIDSLEmptyFileCodeLensProvider;
import umlp_with_gui._lsp.language_aggregation.language_access.UMLPWithGUILanguageAggrScopeManager4GUIDSL;
import de.mclsg.lsp.MultiLangWorkspaceService;

import java.util.ArrayList;


public class UMLPWithGUILanguageAggrServer extends UMLPWithGUILanguageAggrServerTOP {
  public UMLPWithGUILanguageAggrServer(ProjectLayout layout) throws FileExtensionAlreadyRegisteredException {
    super(layout);
  }

  @Override
  protected MultiLangWorkspaceService initWorkspaceService(){
    MultiLangWorkspaceService res = new UMLPWithGUILanguageAggrWorkspaceService(this, indexingManager, layout, documentManager);

    res.add(uMLPLanguageServer.getWorkspaceService());
    res.add(gUIDSLLanguageServer.getWorkspaceService());

    return res;
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

      @Override
      protected GUIDSLTextDocumentService initTextDocumentService() {
        return new GUIDSLTextDocumentService(
            this,
            documentManager,
            languageClient,
            languageAccess,
            symbolUsageResolutionProvider
        ) {
          @Override
          protected void registerDefaultCodeLensProvider() {
            register(new GUIDSLEmptyFileCodeLensProvider(documentManager));
          }
        };
      }
    };
    return server;
  }

  @Override
  public ServerCapabilities getCapabilities() {
    ServerCapabilities capabilities = super.getCapabilities();
    ExecuteCommandOptions executeCommandProvider = capabilities.getExecuteCommandProvider();
    ArrayList<String> commands = executeCommandProvider == null || executeCommandProvider.getCommands() == null
        ? new ArrayList<>()
        : new ArrayList<>(executeCommandProvider.getCommands());
    if (!commands.contains(EmptyFileCodeLensSupport.FILL_EMPTY_FILE_COMMAND)) {
      commands.add(EmptyFileCodeLensSupport.FILL_EMPTY_FILE_COMMAND);
    }
    capabilities.setExecuteCommandProvider(new ExecuteCommandOptions(commands));
    return capabilities;
  }
}
