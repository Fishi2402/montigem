package umlp_with_gui._lsp;

import de.mclsg.lsp.modelpath.multiproject.MultiProjectLayout;
import de.mclsg.lsp.modelpath.multiproject.SubprojectLayout;
import de.monticore.io.paths.MCPath;
import org.apache.commons.io.FileUtils;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializedParams;
import org.eclipse.lsp4j.SemanticTokensParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextDocumentItem;
import org.junit.jupiter.api.Test;
import umlp_with_gui._lsp.language_aggregation.multiproject.UMLPWithGUILanguageAggrMultiProjectServer;
import umlp_with_gui._lsp.mock.MockLanguageClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultiProjectSetupTest {

  @Test
  public void testMultiProjectServerWorksWithoutLayout() throws ExecutionException, InterruptedException, IOException {
    Path path = Path.of("src/test/resources/lsp/no_manifest_multiproject");
    var server = new UMLPWithGUILanguageAggrMultiProjectServer(
        new MCPath(path)
    );

    assertTrue(server.getSubprojectServerContexts().isEmpty());

    // Connect client, initialize server
    server.connect(new MockLanguageClient());
    server.initialize(new InitializeParams());
    server.initialized(new InitializedParams());

    // Does not crash
    Path model = path.resolve("Unknown.umlp");
    String url = model.toUri().toString();
    server.getTextDocumentService().didOpen(new DidOpenTextDocumentParams(new TextDocumentItem(
        url,
        "UMLP",
        1,
        FileUtils.readFileToString(model.toFile(), StandardCharsets.UTF_8)
    )));

    // Provides highlighting
    var semanticTokens = server.getTextDocumentService().semanticTokensFull(new SemanticTokensParams(
        new TextDocumentIdentifier(url)
    )).get();
    assertFalse(semanticTokens.getData().isEmpty());
  }

  @Test
  public void testMultiProjectLayoutIsReplaceable() throws ExecutionException, InterruptedException, IOException {
    Path path = Path.of("src/test/resources/lsp/no_manifest_multiproject").toAbsolutePath();
    var server = new UMLPWithGUILanguageAggrMultiProjectServer(
        new MCPath(path)
    ){
      @Override
      public void updateBasedOnConfig(MultiProjectLayout projects) {
        super.updateBasedOnConfig(projects);
      }
    };

    assertTrue(server.getSubprojectServerContexts().isEmpty());

    // Connect client, initialize server
    server.connect(new MockLanguageClient());
    server.initialize(new InitializeParams());
    server.initialized(new InitializedParams());

    // Replace MultiProjectLayout
    Path subprojectPath = path.resolve("subproject1");

    MultiProjectLayout multiProjectLayout = new MultiProjectLayout();
    SubprojectLayout subprojectLayout = new SubprojectLayout();
    subprojectLayout.projectpath = subprojectPath.toString();
    subprojectLayout.modelDirs = new String[]{path.toString()};
    subprojectLayout.name = "subproject1";

    multiProjectLayout.subprojects = new SubprojectLayout[]{ subprojectLayout };
    server.updateBasedOnConfig(multiProjectLayout);

    // Now has subproject servers
    assertEquals(1, server.getSubprojectServerContexts().size());

    Path model = subprojectPath.resolve("Domain.umlp");
    String url = model.toUri().toString();
    server.getTextDocumentService().didOpen(new DidOpenTextDocumentParams(new TextDocumentItem(
        url,
        "UMLP",
        1,
        FileUtils.readFileToString(model.toFile(), StandardCharsets.UTF_8)
    )));

    // Provides highlighting
    var semanticTokens = server.getTextDocumentService().semanticTokensFull(new SemanticTokensParams(
        new TextDocumentIdentifier(url)
    )).get();
    assertFalse(semanticTokens.getData().isEmpty());
  }
}
