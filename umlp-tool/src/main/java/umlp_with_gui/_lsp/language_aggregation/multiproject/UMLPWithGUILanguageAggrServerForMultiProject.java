package umlp_with_gui._lsp.language_aggregation.multiproject;

import de.mclsg.lsp.FileExtensionAlreadyRegisteredException;
import de.mclsg.lsp.modelpath.multiproject.SubprojectLayout;
import de.monticore.guidsl._lsp.GUIDSLLanguageServer;
import de.monticore.guidsl._lsp.GUIDSLWorkspaceService;
import de.monticore.guidsl._lsp.multiproject.GUIDSLSubprojectIndexingManager;
import de.monticore.io.paths.MCPath;
import org.eclipse.lsp4j.ApplyWorkspaceEditParams;
import org.eclipse.lsp4j.CreateFilesParams;
import org.eclipse.lsp4j.FileCreate;
import org.eclipse.lsp4j.FileOperationFilter;
import org.eclipse.lsp4j.FileOperationOptions;
import org.eclipse.lsp4j.FileOperationPattern;
import org.eclipse.lsp4j.FileOperationsServerCapabilities;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.WorkspaceEdit;
import umlp_with_gui._lsp.language_aggregation.language_access.GUIDSLScopeManager4Aggr;

import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UMLPWithGUILanguageAggrServerForMultiProject extends UMLPWithGUILanguageAggrServerForMultiProjectTOP {
  public UMLPWithGUILanguageAggrServerForMultiProject(SubprojectLayout layout, MCPath rootPath, List<String> ignoredDirs) throws FileExtensionAlreadyRegisteredException {
    super(layout, rootPath, ignoredDirs);
  }

  @Override
  protected GUIDSLLanguageServer initGUIDSLLanguageServer(MCPath path) {
    GUIDSLScopeManager4Aggr scopeManager = new GUIDSLScopeManager4Aggr(this.uMLPLanguageServer.getContext());

    GUIDSLLanguageServer server = new GUIDSLLanguageServer(
        documentManager, new MCPath(), scopeManager, symbolUsageResolutionProvider) {
      @Override
      protected GUIDSLWorkspaceService initWorkspaceService() {
        return new GUIDSLWorkspaceService(this, indexingManager) {
          @Override
          public void didCreateFiles(CreateFilesParams params) {
            Map<String, List<TextEdit>> edits = new HashMap<>();
            for (FileCreate file : params.getFiles()) {
              if (file.getUri().endsWith(".gui")) {
                Range minimalRange = new Range(new Position(0, 0), new Position(0, 0));
                Path basePath = Path.of(layout.projectpath);
                Path filePath = Path.of(URI.create(file.getUri()));

                Path relativePath = basePath.relativize(filePath);

                String packageStatement = "package ";
                for (int i = 1; i < relativePath.getNameCount() - 1; i++) {
                  if(i > 1){
                    packageStatement += ".";
                  }
                  packageStatement += relativePath.getName(i);
                }
                packageStatement += ";";

                String name = relativePath.getFileName().toString().replaceAll(".gui$", "");

                edits.put(file.getUri(), List.of(new TextEdit(minimalRange,
                    packageStatement + "\n\n" +
                        "component " + name + "(){\n\n}"
                )));
              }
            }

            if (!edits.isEmpty()) {
              languageClient.applyEdit(new ApplyWorkspaceEditParams(
                  new WorkspaceEdit(edits)
              ));
            }
          }
        };
      }
    };

    server.setIndexingManager(new GUIDSLSubprojectIndexingManager(
        layout,
        documentManager,
        server.getTextDocumentService().getDocumentInformationProvider(),
        rootPath,
        ignoredDirs,
        server
    ));

    FileOperationsServerCapabilities fileCapabilities = new FileOperationsServerCapabilities();
    String glob = layout.projectpath.replace('\\', '/') + "/**/*.gui";
    fileCapabilities.setDidRename(new FileOperationOptions(List.of(new FileOperationFilter(new FileOperationPattern(glob)))));
    fileCapabilities.setDidCreate(new FileOperationOptions(List.of(new FileOperationFilter(new FileOperationPattern(glob)))));
    fileCapabilities.setDidDelete(new FileOperationOptions(List.of(new FileOperationFilter(new FileOperationPattern(glob)))));
    server.getWorkspaceService().setFileOperationsServerCapabilities(fileCapabilities);

    server.getContext().noLockClose(); // initializing a subserver leaves its context open, so close
    return server;
  }

  @Override
  protected boolean automaticallyRunIndexing() {
    return false;
  }
}
