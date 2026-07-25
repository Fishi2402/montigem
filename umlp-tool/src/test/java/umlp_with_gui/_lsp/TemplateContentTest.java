package umlp_with_gui._lsp;

import de.mclsg.lsp.FileExtensionAlreadyRegisteredException;
import de.mclsg.lsp.modelpath.multiproject.ProjectLayoutBuilder;
import de.mclsg.lsp.util.AsyncUtilWithSyncExec;
import org.eclipse.lsp4j.ApplyWorkspaceEditParams;
import org.eclipse.lsp4j.ApplyWorkspaceEditResponse;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.CodeLensParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import umlp._lsp.features.code_lens.EmptyFileCodeLensSupport;
import umlp_with_gui._lsp.language_aggregation.UMLPWithGUILanguageAggrServer;
import umlp_with_gui._lsp.mock.MockLanguageClient;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TemplateContentTest {

  static class ApplyEditMockLanguageClient extends MockLanguageClient {
    ApplyWorkspaceEditParams applyWorkspaceEditParams;

    @Override
    public CompletableFuture<ApplyWorkspaceEditResponse> applyEdit(ApplyWorkspaceEditParams params) {
      this.applyWorkspaceEditParams = params;
      return CompletableFuture.completedFuture(new ApplyWorkspaceEditResponse(true));
    }
  }

  @BeforeEach
  void initAsyncExecution() {
    AsyncUtilWithSyncExec.init();
  }

  @Test
  public void testUmlpCodeLensFillsEmptyFile()
      throws FileExtensionAlreadyRegisteredException, ExecutionException, InterruptedException {
    TestServer testServer = createServer();
    String uri = testServer.resources.resolve("NewDomain.umlp").toUri().toString();
    openDocument(testServer.server, uri, "umlp", "");

    CodeLens codeLens = getOnlyCodeLens(testServer.server, uri);
    assertEquals("Add basic classdiagram", codeLens.getCommand().getTitle());
    assertEquals(EmptyFileCodeLensSupport.FILL_EMPTY_FILE_COMMAND, codeLens.getCommand().getCommand());

    execute(testServer.server, codeLens.getCommand());

    assertNotNull(testServer.client.applyWorkspaceEditParams);
    String content = testServer.client.applyWorkspaceEditParams.getEdit().getChanges().get(uri).get(0).getNewText();
    assertEquals("classdiagram NewDomain {\n\n}\n", content);
  }

  @Test
  public void testUmlpCodeLensDoesNotFillFileWithOnDiskContent()
      throws FileExtensionAlreadyRegisteredException, ExecutionException, InterruptedException {
    TestServer testServer = createServer();
    // Domain.umlp exists non-empty on disk; a stale empty snapshot must not trigger the stub.
    String uri = testServer.resources.resolve("Domain.umlp").toUri().toString();
    openDocument(testServer.server, uri, "umlp", "");

    CodeLens codeLens = getOnlyCodeLens(testServer.server, uri);
    execute(testServer.server, codeLens.getCommand());

    assertNull(testServer.client.applyWorkspaceEditParams);
  }

  @Test
  public void testGuiCodeLensFillsEmptyFile()
      throws FileExtensionAlreadyRegisteredException, ExecutionException, InterruptedException {
    TestServer testServer = createServer();
    String uri = testServer.resources.resolve("SomePage.gui").toUri().toString();
    openDocument(testServer.server, uri, "gui", "");

    CodeLens codeLens = getOnlyCodeLens(testServer.server, uri);
    assertEquals("Add basic GUI content", codeLens.getCommand().getTitle());
    assertEquals(EmptyFileCodeLensSupport.FILL_EMPTY_FILE_COMMAND, codeLens.getCommand().getCommand());

    execute(testServer.server, codeLens.getCommand());

    assertNotNull(testServer.client.applyWorkspaceEditParams);
    String content = testServer.client.applyWorkspaceEditParams.getEdit().getChanges().get(uri).get(0).getNewText();
    assertEquals("page SomePage() {\n\n}\n", content);
  }

  @Test
  public void testInitializationCommandIsAdvertised()
      throws FileExtensionAlreadyRegisteredException, ExecutionException, InterruptedException {
    TestServer testServer = createServer();

    assertNotNull(testServer.server.getCapabilities().getExecuteCommandProvider());
    assertTrue(testServer.server.getCapabilities().getExecuteCommandProvider().getCommands()
        .contains(EmptyFileCodeLensSupport.FILL_EMPTY_FILE_COMMAND));
  }

  protected TestServer createServer()
      throws FileExtensionAlreadyRegisteredException, ExecutionException, InterruptedException {
    Path resources = Path.of("src/test/resources/lsp/multiproject/subproject1").toAbsolutePath();
    UMLPWithGUILanguageAggrServer server =
        new UMLPWithGUILanguageAggrServer(new ProjectLayoutBuilder().resources(resources).build());
    server.initialize(new InitializeParams()).get();
    ApplyEditMockLanguageClient client = new ApplyEditMockLanguageClient();
    server.connect(client);
    return new TestServer(server, client, resources);
  }

  protected void openDocument(
      UMLPWithGUILanguageAggrServer server,
      String uri,
      String languageId,
      String content
  ) {
    server.getTextDocumentService().didOpen(
        new DidOpenTextDocumentParams(new TextDocumentItem(uri, languageId, 0, content))
    );
  }

  protected List<? extends CodeLens> getCodeLenses(UMLPWithGUILanguageAggrServer server, String uri)
      throws ExecutionException, InterruptedException {
    return server.getTextDocumentService()
        .codeLens(new CodeLensParams(new TextDocumentIdentifier(uri)))
        .get();
  }

  protected CodeLens getOnlyCodeLens(UMLPWithGUILanguageAggrServer server, String uri)
      throws ExecutionException, InterruptedException {
    List<? extends CodeLens> codeLenses = getCodeLenses(server, uri);
    assertEquals(1, codeLenses.size());
    return codeLenses.get(0);
  }

  protected void execute(UMLPWithGUILanguageAggrServer server, Command command)
      throws ExecutionException, InterruptedException {
    server.getWorkspaceService()
        .executeCommand(new ExecuteCommandParams(command.getCommand(), command.getArguments()))
        .get();
  }

  protected record TestServer(
      UMLPWithGUILanguageAggrServer server,
      ApplyEditMockLanguageClient client,
      Path resources
  ) {
  }
}
