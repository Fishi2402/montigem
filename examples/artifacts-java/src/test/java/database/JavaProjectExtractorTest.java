/* (c) https://github.com/MontiCore/monticore */
package database;

import baseartifactstest.ArtifactView;
import baseartifactstest.BaseArtifactsTest4JavaProjectManagerImpl;
import baseartifactstest.BaseArtifactsTestManager;
import baseartifactstest.BaseArtifactsTestManagerImpl;
import javaproject.JavaExtractor;
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

import static org.junit.jupiter.api.Assertions.*;

public class JavaProjectExtractorTest extends GenAbstractNeo4jDBTest {

  // TODO neo4j Test Infrastruktur muss aufgebaut werden
  @Test
  public void testSimpleJavaExtractor() {
    // Gets resolved to the Java directory of artifacts-test at runtime
    String path = "src/main/java";

    File file = new File(path);

    JavaProjectManagerImpl.init();
    BaseArtifactsTest4JavaProjectManagerImpl.init();

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
  }
}
