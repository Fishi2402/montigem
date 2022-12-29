/* (c) https://github.com/MontiCore/monticore */
import baseartifactstest.BaseArtifactsTestManager;
import baseartifactstest.BaseArtifactsTestManagerImpl;
import de.se_rwth.artifacts.static_extractors.ContainerFileExtractor;
import de.se_rwth.artifacts.static_extractors.commons.BaseExtractor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import umlp.rte.GemIdentifiable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class TestContainerFileExtractor {

  // TODO neo4j Test Infrastruktur muss aufgebaut werden
  @Disabled
  @Test
  public void testContainerFileExtractor() {

    BaseArtifactsTestManagerImpl.init();

    // Gets resolved to the Java directory of artifacts-test at runtime
    String path = "src/main/java";

    File file = new File(path);

    ContainerFileExtractor extractor = new ContainerFileExtractor(new BaseArtifactsTestManagerImpl());

    Set<GemIdentifiable> artifacts = extractor.extractGivenRoot(List.of(file));

    artifacts.contains(null);
  }
}
