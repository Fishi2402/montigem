package umlp_with_gui._lsp.language_aggregation;

import de.mclsg.lsp.CommonLanguageServer;
import de.mclsg.lsp.IIndexingManager;
import de.mclsg.lsp.modelpath.multiproject.ProjectLayout;
import org.eclipse.lsp4j.*;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class UMLPWithGUILanguageAggrWorkspaceService extends UMLPWithGUILanguageAggrWorkspaceServiceTOP {

  protected ProjectLayout layout;

  public UMLPWithGUILanguageAggrWorkspaceService(CommonLanguageServer languageServer, IIndexingManager indexingManager) {
    super(languageServer, indexingManager);
    this.indexingManager = indexingManager;
    registerFileOperationsServerCapabilities();
  }

  public UMLPWithGUILanguageAggrWorkspaceService(CommonLanguageServer languageServer, IIndexingManager indexingManager, ProjectLayout layout) {
    this(languageServer, indexingManager);
    this.layout = layout;
  }

  protected WorkspaceServerCapabilities capabilities = new WorkspaceServerCapabilities();

  protected void registerFileOperationsServerCapabilities() {
    List<FileOperationFilter> filters = Arrays.asList(
        new FileOperationFilter(new FileOperationPattern("**/*.umlp")),
        new FileOperationFilter(new FileOperationPattern("**/*.gui"))
    );

    FileOperationsServerCapabilities fileCapabilities = new FileOperationsServerCapabilities();
    fileCapabilities.setDidCreate(new FileOperationOptions(filters));
    fileCapabilities.setDidRename(new FileOperationOptions(filters));
    fileCapabilities.setDidDelete(new FileOperationOptions(filters));
    capabilities.setFileOperations(fileCapabilities);
  }

  @Override
  public WorkspaceServerCapabilities getCapabilities() {
    return capabilities;
  }

  @Override
  public void didCreateFiles(CreateFilesParams params) {
    if (languageServer == null || languageServer.getLanguageClient() == null) {
        return;
    }

    Map<String, List<TextEdit>> changes = new HashMap<>();

    for (FileCreate fileCreate : params.getFiles()) {
      String uriStr = fileCreate.getUri();
      if (uriStr.endsWith(".gui")) {
        String content = generateMinimalGuiModel(uriStr);
        if (content != null) {
          TextEdit edit = new TextEdit(new Range(new Position(0, 0), new Position(0, 0)), content);
          changes.put(uriStr, List.of(edit));
        }
      }
    }

    if (!changes.isEmpty()) {
      languageServer.getLanguageClient().applyEdit(new ApplyWorkspaceEditParams(new WorkspaceEdit(changes)));
    }

    try {
        if (this.indexingManager != null) {
            super.didCreateFiles(params);
        }
    } catch (Exception e) {
        // Log or handle error in super delegation
    }
  }

  protected String generateMinimalGuiModel(String uriStr) {
    try {
      URI uri = new URI(uriStr);
      Path path = Paths.get(uri).toAbsolutePath().normalize();
      String fileName = path.getFileName().toString();
      String name = fileName.substring(0, fileName.lastIndexOf('.'));

      String packageName = calculatePackage(path);

      StringBuilder sb = new StringBuilder();
      if (packageName != null && !packageName.isEmpty()) {
        sb.append("package ").append(packageName).append(";\n\n");
      }
      sb.append("page ").append(name).append("() {\n\n}\n");
      return sb.toString();
    } catch (Exception e) {
      return null;
    }
  }

  protected String calculatePackage(Path path) {
    if (layout == null || layout.resources == null) {
      return "";
    }

    for (String resourcePathStr : layout.resources) {
      Path resourcePath = Paths.get(resourcePathStr).toAbsolutePath().normalize();
      if (path.startsWith(resourcePath)) {
        Path relativePath = resourcePath.relativize(path.getParent());
        if (relativePath.toString().isEmpty()) {
          return "";
        }
        return relativePath.toString().replace(java.io.File.separator, ".");
      }
    }
    return "";
  }
}
