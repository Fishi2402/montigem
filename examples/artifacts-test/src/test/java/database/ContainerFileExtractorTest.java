package database;/* (c) https://github.com/MontiCore/monticore */

import baseartifactstest.BaseArtifactsTestManagerImpl;
import de.se_rwth.artifacts.static_extractors.ContainerFileExtractor;
import org.junit.jupiter.api.Test;
import umlp.backendrte.common.GemIdentifiable;

import java.io.File;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContainerFileExtractorTest extends GenAbstractNeo4jDBTest {

  // TODO neo4j Test Infrastruktur muss aufgebaut werden
  @Test
  public void testContainerFileExtractor() {

    BaseArtifactsTestManagerImpl.init();

    // Gets resolved to the Java directory of artifacts-test at runtime
    String path = "src/main/java";

    File file = new File(path);

    ContainerFileExtractor extractor = new ContainerFileExtractor();

    Set<GemIdentifiable> artifacts = extractor.extractGivenRoot(List.of(file));

    assertEquals( 12, artifacts.size());
  }
}
