package umlp_with_gui._lsp;

import de.mclsg.lsp.FileExtensionAlreadyRegisteredException;
import de.mclsg.lsp.modelpath.multiproject.ProjectLayoutBuilder;
import de.mclsg.lsp.util.AsyncUtilWithSyncExec;
import org.eclipse.lsp4j.*;
import org.junit.jupiter.api.Test;
import umlp_with_gui._lsp.language_aggregation.UMLPWithGUILanguageAggrServer;
import umlp_with_gui._lsp.mock.MockLanguageClient;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

public class DidCreateFileTest {

  static class ApplyEditMockLanguageClient extends MockLanguageClient {
    ApplyWorkspaceEditParams applyWorkspaceEditParams;

    @Override
    public CompletableFuture<ApplyWorkspaceEditResponse> applyEdit(ApplyWorkspaceEditParams params) {
      this.applyWorkspaceEditParams = params;
      return CompletableFuture.completedFuture(new ApplyWorkspaceEditResponse(true));
    }
  }

  @Test
  public void testDidCreateGuiFile() throws FileExtensionAlreadyRegisteredException, ExecutionException, InterruptedException {
    Path p1 = Path.of("src/test/resources/lsp/multiproject/subproject1").toAbsolutePath();
    UMLPWithGUILanguageAggrServer languageServer = new UMLPWithGUILanguageAggrServer(new ProjectLayoutBuilder().resources(p1).build());

    AsyncUtilWithSyncExec.init();
    languageServer.initialize(new InitializeParams());
    ApplyEditMockLanguageClient client = new ApplyEditMockLanguageClient();
    languageServer.connect(client);

    String uri = p1.resolve("a/b/C.gui").toUri().toString();
    CreateFilesParams params = new CreateFilesParams(Collections.singletonList(new FileCreate(uri)));
    languageServer.getWorkspaceService().didCreateFiles(params);

    assertNotNull(client.applyWorkspaceEditParams, "applyEdit should have been called");
    WorkspaceEdit edit = client.applyWorkspaceEditParams.getEdit();
    assertNotNull(edit);
    assertNotNull(edit.getChanges());
    assertTrue(edit.getChanges().containsKey(uri));
    List<TextEdit> textEdits = edit.getChanges().get(uri);
    assertEquals(1, textEdits.size());
    String content = textEdits.get(0).getNewText();

    assertTrue(content.contains("package a.b;"), "Should contain package statement. Content: " + content);
    assertTrue(content.contains("page C()"), "Should contain page name. Content: " + content);

    // Test root package
    client.applyWorkspaceEditParams = null;
    String uriRoot = p1.resolve("Root.gui").toUri().toString();
    languageServer.getWorkspaceService().didCreateFiles(new CreateFilesParams(Collections.singletonList(new FileCreate(uriRoot))));
    assertNotNull(client.applyWorkspaceEditParams);
    String contentRoot = client.applyWorkspaceEditParams.getEdit().getChanges().get(uriRoot).get(0).getNewText();
    assertFalse(contentRoot.contains("package"), "Should not contain package statement. Content: " + contentRoot);
    assertTrue(contentRoot.contains("page Root()"), "Should contain page name. Content: " + contentRoot);

    // Test deep package
    client.applyWorkspaceEditParams = null;
    String uriDeep = p1.resolve("x/y/z/Deep.gui").toUri().toString();
    languageServer.getWorkspaceService().didCreateFiles(new CreateFilesParams(Collections.singletonList(new FileCreate(uriDeep))));
    assertNotNull(client.applyWorkspaceEditParams);
    String contentDeep = client.applyWorkspaceEditParams.getEdit().getChanges().get(uriDeep).get(0).getNewText();
    assertTrue(contentDeep.contains("package x.y.z;"), "Should contain package statement. Content: " + contentDeep);
    assertTrue(contentDeep.contains("page Deep()"), "Should contain page name. Content: " + contentDeep);
  }

  @Test
  public void testDidCreateUmlpFile() throws FileExtensionAlreadyRegisteredException, ExecutionException, InterruptedException {
    Path p1 = Path.of("src/test/resources/lsp/multiproject/subproject1").toAbsolutePath();
    UMLPWithGUILanguageAggrServer languageServer = new UMLPWithGUILanguageAggrServer(new ProjectLayoutBuilder().resources(p1).build());

    AsyncUtilWithSyncExec.init();
    languageServer.initialize(new InitializeParams());
    ApplyEditMockLanguageClient client = new ApplyEditMockLanguageClient();
    languageServer.connect(client);

    String uri = p1.resolve("a/b/Domain.umlp").toUri().toString();
    CreateFilesParams params = new CreateFilesParams(Collections.singletonList(new FileCreate(uri)));
    languageServer.getWorkspaceService().didCreateFiles(params);

    assertNotNull(client.applyWorkspaceEditParams, "applyEdit should have been called");
    WorkspaceEdit edit = client.applyWorkspaceEditParams.getEdit();
    assertNotNull(edit);
    assertNotNull(edit.getChanges());
    assertTrue(edit.getChanges().containsKey(uri));
    List<TextEdit> textEdits = edit.getChanges().get(uri);
    assertEquals(1, textEdits.size());
    String content = textEdits.get(0).getNewText();

    assertTrue(content.contains("package a.b;"), "Should contain package statement. Content: " + content);
    assertTrue(content.contains("classdiagram Domain {"), "Should contain classdiagram model name. Content: " + content);

    // Test root package
    client.applyWorkspaceEditParams = null;
    String uriRoot = p1.resolve("Root.umlp").toUri().toString();
    languageServer.getWorkspaceService().didCreateFiles(new CreateFilesParams(Collections.singletonList(new FileCreate(uriRoot))));
    assertNotNull(client.applyWorkspaceEditParams);
    String contentRoot = client.applyWorkspaceEditParams.getEdit().getChanges().get(uriRoot).get(0).getNewText();
    assertFalse(contentRoot.contains("package"), "Should not contain package statement. Content: " + contentRoot);
    assertTrue(contentRoot.contains("classdiagram Root {"), "Should contain classdiagram model name. Content: " + contentRoot);

    // Test deep package
    client.applyWorkspaceEditParams = null;
    String uriDeep = p1.resolve("x/y/z/Deep.umlp").toUri().toString();
    languageServer.getWorkspaceService().didCreateFiles(new CreateFilesParams(Collections.singletonList(new FileCreate(uriDeep))));
    assertNotNull(client.applyWorkspaceEditParams);
    String contentDeep = client.applyWorkspaceEditParams.getEdit().getChanges().get(uriDeep).get(0).getNewText();
    assertTrue(contentDeep.contains("package x.y.z;"), "Should contain package statement. Content: " + contentDeep);
    assertTrue(contentDeep.contains("classdiagram Deep {"), "Should contain classdiagram model name. Content: " + contentDeep);

    // Test non-related file
    client.applyWorkspaceEditParams = null;
    String uriTxt = p1.resolve("readme.txt").toUri().toString();
    languageServer.getWorkspaceService().didCreateFiles(new CreateFilesParams(Collections.singletonList(new FileCreate(uriTxt))));
    assertNull(client.applyWorkspaceEditParams, "applyEdit should not have been called for non-umlp/gui file");
  }
}
