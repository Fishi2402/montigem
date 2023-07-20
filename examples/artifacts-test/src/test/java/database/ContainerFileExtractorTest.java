package database;/* (c) https://github.com/MontiCore/monticore */

import baseartifactstest.BaseArtifactsTestManager;
import baseartifactstest.BaseArtifactsTestManagerImpl;
import baseartifactstest.BaseArtifactsTestMock;
import baseartifactstest.DirectoryDAO;
import baseartifactstest.FileArtifactDAO;
import baseartifactstest.RootArtifactDAO;
import com.google.common.base.Stopwatch;
import de.se_rwth.artifacts.static_extractors.ContainerFileExtractor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import umlp.backendrte.common.GemIdentifiable;

import java.io.File;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContainerFileExtractorTest extends GenAbstractNeo4jDBTest {

  // TODO neo4j Test Infrastruktur muss aufgebaut werden
  @Disabled
  @Test
  public void testContainerFileExtractor() {

    //Stopwatch watch = Stopwatch.createStarted();
    BaseArtifactsTestManagerImpl.init();

    // Gets resolved to the Java directory of artifacts-test at runtime
    String path = "src/main/java";

    File file = new File(path);

    ContainerFileExtractor extractor = new ContainerFileExtractor();

    Set<GemIdentifiable> artifacts = extractor.extractGivenRoot(List.of(file));

    // Phase 1 insert all objects
    RootArtifactDAO rootArtifactDAO = new RootArtifactDAO();
    rootArtifactDAO.insertRootArtifact(BaseArtifactsTestManager.getRootArtifactList());

    DirectoryDAO directoryDAO = new DirectoryDAO();
    directoryDAO.insertDirectory(BaseArtifactsTestManager.getDirectoryList());

    FileArtifactDAO fileArtifactDAO = new FileArtifactDAO();
    fileArtifactDAO.insertFileArtifact(BaseArtifactsTestManager.getFileArtifactList());

    //System.out.println(watch.elapsed());

    // Phase 2 insert all links
    /*
    rootArtifactDAO.bulkAddAssocsToBatchRootArtifact(BaseArtifactsTestManager.getRootArtifactList());
    //System.out.println(watch.elapsed());
    directoryDAO.bulkAddAssocsToBatchDirectory(BaseArtifactsTestManager.getDirectoryList());
    //System.out.println(watch.elapsed());
    fileArtifactDAO.bulkAddAssocsToBatchFileArtifact(BaseArtifactsTestManager.getFileArtifactList());


    rootArtifactDAO.executeBatchAddAssocsRootArtifact();
    directoryDAO.executeBatchAddAssocsDirectory();
    fileArtifactDAO.executeBatchAddAssocsFileArtifact();
    
     */

    //System.out.println(watch.elapsed());

    assertEquals( 12, artifacts.size());
  }


  @Test
  public void testDBPerfomance() {

    //Stopwatch watch = Stopwatch.createStarted();
    BaseArtifactsTestMock.init();

    // Gets resolved to the Java directory of artifacts-test at runtime
    String path = "src/main/java";

    File file = new File(path);

    ContainerFileExtractor extractor = new ContainerFileExtractor();

    Set<GemIdentifiable> artifacts = extractor.extractGivenRoot(List.of(file));

    // Phase 1 insert all objects
    RootArtifactDAO rootArtifactDAO = new RootArtifactDAO();
    rootArtifactDAO.insertRootArtifact(BaseArtifactsTestManager.getRootArtifactList());

    DirectoryDAO directoryDAO = new DirectoryDAO();
    directoryDAO.insertDirectory(BaseArtifactsTestManager.getDirectoryList());

    FileArtifactDAO fileArtifactDAO = new FileArtifactDAO();
    fileArtifactDAO.insertFileArtifact(BaseArtifactsTestManager.getFileArtifactList());

    //System.out.println(watch.elapsed());

    // Phase 2 insert all links
    rootArtifactDAO.bulkAddAssocsToBatchRootArtifact(BaseArtifactsTestManager.getRootArtifactList());
    //System.out.println(watch.elapsed());
    directoryDAO.bulkAddAssocsToBatchDirectory(BaseArtifactsTestManager.getDirectoryList());
    //System.out.println(watch.elapsed());
    fileArtifactDAO.bulkAddAssocsToBatchFileArtifact(BaseArtifactsTestManager.getFileArtifactList());


    rootArtifactDAO.executeBatchAddAssocsRootArtifact();
    directoryDAO.executeBatchAddAssocsDirectory();
    fileArtifactDAO.executeBatchAddAssocsFileArtifact();

    //System.out.println(watch.elapsed());

    assertEquals( 12, artifacts.size());
  }
}
