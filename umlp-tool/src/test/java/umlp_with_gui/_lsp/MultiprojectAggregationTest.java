package umlp_with_gui._lsp;

import de.mclsg.lsp.CommonLanguageServer;
import de.mclsg.lsp.FileExtensionAlreadyRegisteredException;
import de.mclsg.lsp.MultiLangLanguageServerContext;
import de.mclsg.lsp.util.AsyncUtilWithSyncExec;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import umlp_with_gui._lsp.language_aggregation.UMLPWithGUILanguageAggrServer;
import umlp_with_gui._lsp.language_aggregation.multiproject.UMLPWithGUILanguageAggrMultiProjectServer;
import umlp_with_gui._lsp.language_aggregation.multiproject.UMLPWithGUILanguageAggrServerForMultiProject;
import umlp_with_gui._lsp.mock.MockLanguageClient;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultiprojectAggregationTest {

  @BeforeEach
  public void init(){
    LogStub.init();
    LogStub.clearPrints();
    Log.clearFindings();
  }


  public Hover hoverRegularAggregation1(Position position) {
    UMLPWithGUILanguageAggrServer languageServer;
    try {
      Path p1 = Path.of("src/test/resources/lsp/multiproject/subproject1");
      languageServer = new UMLPWithGUILanguageAggrServer(new MCPath(p1));

      // (1)
      AsyncUtilWithSyncExec.init(); // Run everything synchronous for test
      languageServer.initialize(new InitializeParams());
      languageServer.connect(new MockLanguageClient());

      var tds = languageServer.getTextDocumentService();

      var hoverResult = tds.hover(new HoverParams(
          new TextDocumentIdentifier(p1.resolve("simple/Card.gui").toUri().toString()),
          position));

      return hoverResult.get();

    } catch (FileExtensionAlreadyRegisteredException e) {
      throw new RuntimeException(e);
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public Hover hoverMpSubproject1(Position position) {
    UMLPWithGUILanguageAggrMultiProjectServer languageServer;


    try {
      Path p1 = Path.of("src/test/resources/lsp/multiproject/");
      languageServer = new UMLPWithGUILanguageAggrMultiProjectServer(new MCPath(p1));

      // (1)
      AsyncUtilWithSyncExec.init(); // Run everything synchronous for test
      languageServer.initialize(new InitializeParams());
      languageServer.connect(new MockLanguageClient());

      var tds = languageServer.getTextDocumentService();

      var hoverResult = tds.hover(new HoverParams(
          new TextDocumentIdentifier(p1.resolve("subproject1/simple/Card.gui").toUri().toString()),
          position));

      return hoverResult.get();

    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }

  }

  public Either<List<? extends Location>, List<? extends LocationLink>> definitionRegularAggregation1(Position position) {
    UMLPWithGUILanguageAggrServer languageServer;


    try {
      Path p1 = Path.of("src/test/resources/lsp/multiproject/subproject1");
      languageServer = new UMLPWithGUILanguageAggrServer(new MCPath(p1));

      // (1)
      AsyncUtilWithSyncExec.init(); // Run everything synchronous for test
      languageServer.initialize(new InitializeParams());
      languageServer.connect(new MockLanguageClient());

      var tds = languageServer.getTextDocumentService();

      var definitionResult = tds.definition(new DefinitionParams(
          new TextDocumentIdentifier(p1.resolve("complex/RoomPersonDetails.gui").toUri().toString()),
          position));

      return definitionResult.get();

    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    } catch (FileExtensionAlreadyRegisteredException e) {
      throw new RuntimeException(e);
    }
  }

  public Either<List<? extends Location>, List<? extends LocationLink>> definitionMpSubproject1(Position position) {
    UMLPWithGUILanguageAggrMultiProjectServer languageServer;


    try {
      Path p1 = Path.of("src/test/resources/lsp/multiproject/");
      languageServer = new UMLPWithGUILanguageAggrMultiProjectServer(new MCPath(p1));

      // (1)
      AsyncUtilWithSyncExec.init(); // Run everything synchronous for test
      languageServer.initialize(new InitializeParams());
      languageServer.connect(new MockLanguageClient());

      var tds = languageServer.getTextDocumentService();

      var definitionResult = tds.definition(new DefinitionParams(
          new TextDocumentIdentifier(p1.resolve("subproject1/complex/RoomPersonDetails.gui").toUri().toString()),
          position));

      return definitionResult.get();

    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void compareMultiProjectWithRegular(){
    var hPosition = new Position(11, 12);
    Hover regular_h = hoverRegularAggregation1(hPosition);
    Hover multiproject_h = hoverMpSubproject1(hPosition);
    assertNotNull(regular_h);
    assertNotNull(regular_h.getContents());
    assertNotNull(regular_h.getContents().getRight());
    assertEquals("markdown", regular_h.getContents().getRight().getKind());
    assertEquals(regular_h, multiproject_h);

    var inPosition = new Position(10, 5);
    var regular_d_inLanguage = definitionRegularAggregation1(inPosition);
    var multiproject_d_inLanguage = definitionMpSubproject1(inPosition);
    assertNotNull(regular_d_inLanguage);
    assertNotNull(regular_d_inLanguage.getLeft());
    assertEquals(1, regular_d_inLanguage.getLeft().size());
    assertTrue(regular_d_inLanguage.getLeft().get(0).getUri().endsWith("/src/test/resources/lsp/multiproject/subproject1/simple/Card.gui"));
    assertEquals(regular_d_inLanguage, multiproject_d_inLanguage);

    var crossPosition = new Position(9, 25);
    var regular_d_crossLanguage = definitionRegularAggregation1(crossPosition);
    var multiproject_d_crossLanguage = definitionMpSubproject1(crossPosition);
    assertNotNull(regular_d_crossLanguage);
    assertNotNull(regular_d_crossLanguage.getLeft());
    assertEquals(1, regular_d_crossLanguage.getLeft().size());
    assertTrue(regular_d_crossLanguage.getLeft().get(0).getUri().endsWith("/src/test/resources/lsp/multiproject/subproject1/Domain.umlp"));
    assertEquals(regular_d_crossLanguage, multiproject_d_crossLanguage);
  }

  @Test
  public void testStructureInMultiproject(){
    Path path = Path.of("src/test/resources/lsp/multiproject/");
    MCPath rootpath = new MCPath(path);
    var languageServer = new UMLPWithGUILanguageAggrMultiProjectServer(rootpath);

    // (1)
    AsyncUtilWithSyncExec.init(); // Run everything synchronous for test
    languageServer.initialize(new InitializeParams());
    languageServer.connect(new MockLanguageClient());

    var subContexts = languageServer.getSubprojectServerContexts();

    // 2 subprojects == 2 subservers (aggregation level) == 2 document managers
    assertEquals(subContexts.size(), 2);

    // Indexing
    for (MultiLangLanguageServerContext subContext : subContexts) {
      try(var c = subContext.open()){
        CommonLanguageServer ls = (CommonLanguageServer) c.getLanguageServer();
        ls.getIndexingManager().indexAllFilesInPath();
      }
    }

    // MULTIPROJECT SUBSERVER 1 TEST
    // first subserver belongs to project "subproject1"
    String subproject1Name = ((UMLPWithGUILanguageAggrServerForMultiProject)subContexts.get(0)
        .getLanguageServer()).getSubprojectName();
    assertEquals(subproject1Name, "subproject1");

    // it knows (among others) the files
    // "Domain.umlp" and "simple/Card.gui".

    var sub1DocManager = ((UMLPWithGUILanguageAggrServerForMultiProject)subContexts.get(0)
        .getLanguageServer()).getDocumentManager();
    assertTrue(sub1DocManager.hasDocument(createTextDocumentIdentifier(
        path.resolve("subproject1/Domain.umlp"))));

    assertTrue(sub1DocManager.hasDocument(createTextDocumentIdentifier(
        path.resolve("subproject1/simple/Card.gui"))));

    // however it doesn't know files which belong to other subprojects
    assertFalse(sub1DocManager.hasDocument(createTextDocumentIdentifier(
        path.resolve("subproject2/Domain.umlp"))));
  }

  private TextDocumentIdentifier createTextDocumentIdentifier(Path p) {
    return new TextDocumentIdentifier(p.toUri().toString());
  }


}
