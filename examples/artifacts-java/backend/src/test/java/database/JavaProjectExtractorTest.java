/* (c) https://github.com/MontiCore/monticore */
package database;

import baseartifactstest.ArtifactView;
import baseartifactstest.BaseArtifactsTest4JavaProjectManagerImpl;
import baseartifactstest.BaseArtifactsTestManager;
import javaproject.JavaExtractor;
import javaproject.JavaProjectCommandObjectMapper;
import javaproject.JavaProjectManager;
import javaproject.JavaProjectManagerImpl;
import javaproject.JavaView;
import javaproject.JavaViewDAO;
import org.junit.jupiter.api.Test;
import umlp.backendrte.common.GemIdentifiable;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JavaProjectExtractorTest extends GenAbstractNeo4jDBTest {

  @Test
  public void testSimpleJavaExtractor() {
    // Gets resolved to the Java directory of artifacts-test at runtime
    String path = "src/main/java";

    File file = new File(path);

    JavaProjectManagerImpl.init();
    BaseArtifactsTest4JavaProjectManagerImpl.init();
    JavaProjectCommandObjectMapper.init();

    JavaExtractor extractor = new JavaExtractor();

    Set<GemIdentifiable> artifacts = extractor.extractGivenRoot(List.of(file));

    assertEquals(7, artifacts.size());

    JavaView view = JavaProjectManager.getJavaViewList().get(0);
    assertNotNull(view);

    JavaViewDAO dao = new JavaViewDAO();
    JavaView viewCopy = dao.selectJavaView(view.getGemId()).get();

    assertNotSame(viewCopy, view);

    assertNotNull(viewCopy.getArtifact());

    Optional<ArtifactView> viewCopyViaOtherManager = BaseArtifactsTestManager.artifactViewOpt(view.getGemId());
    assertTrue(viewCopyViaOtherManager.isPresent());

    // Ensure linking of artifact to its view
    BaseArtifactsTestManager.getFileArtifactList().stream().filter(f -> f.getNameExtension().equals(".java"))
        .forEach( f -> assertEquals(1, f.getArtifactViewList().size()));

  }
}
