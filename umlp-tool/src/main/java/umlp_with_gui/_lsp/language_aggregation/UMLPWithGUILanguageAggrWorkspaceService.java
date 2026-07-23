package umlp_with_gui._lsp.language_aggregation;

import com.google.gson.JsonPrimitive;
import de.mclsg.lsp.CommonLanguageServer;
import de.mclsg.lsp.IIndexingManager;
import de.mclsg.lsp.document_management.DocumentManager;
import de.mclsg.lsp.modelpath.multiproject.ProjectLayout;
import org.eclipse.lsp4j.*;
import umlp._lsp.features.code_lens.EmptyFileCodeLensSupport;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class UMLPWithGUILanguageAggrWorkspaceService extends UMLPWithGUILanguageAggrWorkspaceServiceTOP {

  protected ProjectLayout layout;
  protected DocumentManager documentManager;

  public UMLPWithGUILanguageAggrWorkspaceService(CommonLanguageServer languageServer, IIndexingManager indexingManager) {
    super(languageServer, indexingManager);
    this.indexingManager = indexingManager;
    registerFileOperationsServerCapabilities();
  }

  public UMLPWithGUILanguageAggrWorkspaceService(
      CommonLanguageServer languageServer,
      IIndexingManager indexingManager,
      ProjectLayout layout,
      DocumentManager documentManager
  ) {
    this(languageServer, indexingManager);
    this.layout = layout;
    this.documentManager = documentManager;
    registerCommand(EmptyFileCodeLensSupport.FILL_EMPTY_FILE_COMMAND, this::fillEmptyFile);
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

  protected Object fillEmptyFile(ExecuteCommandParams params) {
    String uri = getUriArgument(params);
    if (uri == null
        || documentManager == null
        || languageServer == null
        || languageServer.getLanguageClient() == null) {
      return null;
    }

    TextDocumentIdentifier identifier = new TextDocumentIdentifier(uri);
    if (!documentManager.hasDocument(identifier)) {
      return null;
    }

    TextDocumentItem document = documentManager.getDocument(identifier);
    if (document == null || document.getText() == null || !document.getText().isEmpty()) {
      return null;
    }

    String content;
    if (uri.endsWith(".gui")) {
      content = generateMinimalGuiModel(uri);
    } else if (uri.endsWith(".umlp")) {
      content = generateMinimalUmlpModel(uri);
    } else {
      return null;
    }

    if (content != null) {
      TextEdit edit = new TextEdit(new Range(new Position(0, 0), new Position(0, 0)), content);
      languageServer.getLanguageClient().applyEdit(
          new ApplyWorkspaceEditParams(new WorkspaceEdit(Map.of(uri, List.of(edit))))
      );
    }
    return null;
  }

  protected String getUriArgument(ExecuteCommandParams params) {
    if (params == null || params.getArguments() == null || params.getArguments().isEmpty()) {
      return null;
    }

    Object argument = params.getArguments().get(0);
    if (argument instanceof JsonPrimitive) {
      return ((JsonPrimitive) argument).getAsString();
    }
    return argument instanceof String ? (String) argument : null;
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

  protected String generateMinimalUmlpModel(String uriStr) {
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
      sb.append("classdiagram ").append(name).append(" {\n\n}\n");
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
