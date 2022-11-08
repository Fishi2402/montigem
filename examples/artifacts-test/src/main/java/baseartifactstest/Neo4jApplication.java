package baseartifactstest;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import umlp.rte.GemList;
import umlp.rte.LoadResult;
import umlp.rte.LoadingState;

@SpringBootApplication
@EnableNeo4jRepositories
//@ComponentScan(basePackages = "baseartifactstest")
public class Neo4jApplication {

  private final static Logger log = LoggerFactory.getLogger(Neo4jApplication.class);

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Neo4jApplication.class, args);
    System.exit(0);
  }

  @Bean
  CommandLineRunner demo(DirectoryRepository directoryRepository,
                         ArtifactRepository artifactRepository,
                         ArtifactInternalRepository artifactInternalRepository,
                         ArtifactViewRepository viewRepository,
                         FileArtifactRepository fileArtifactRepository) {
    return args -> {
      directoryRepository.deleteAll();
      fileArtifactRepository.deleteAll();

      // Create all objects
      Directory d1 = new Directory();
      d1.setFullName("einNeuesDirectory");
      d1.gemId = 1;
      d1.setName("Directory");
      d1.setNameExtension("gibbetNicht");

      FileArtifact f = new FileArtifact();
      f.setFullName("einNeuesFile");
      f.gemId = 2;
      f.setModified("geradeEben");
      f.setName("File");

      // Link all objects
      d1.views =  new GemList<ArtifactView>(new ArrayList<>(), l -> l.stream().map(viewRepository::findById)
          .map(opt -> opt.map(LoadResult::new).orElse(new LoadResult<>(LoadingState.FAILED_TO_LOAD))).collect(Collectors.toList()));
      d1.contains = new GemList<Artifact>(new ArrayList<>(), l -> l.stream().map(artifactRepository::findById)
          .map(opt -> opt.map(LoadResult::new).orElse(new LoadResult<>(LoadingState.FAILED_TO_LOAD))).collect(Collectors.toList()));


      d1.addContains(0, f);



      // Save Objects
      fileArtifactRepository.save(f);
      directoryRepository.save(d1);

      // TODO das Laden von Datenbank nach GemList funktioniert noch nicht
      //List<Directory> directories = directoryRepository.findAll();

      //directories.isEmpty();


     /* Directory loadedDir = directories.get(0);
      loadedDir.setContainsGemList(new GemList<>(loadedDir.contains.getIDs(), l -> l.stream().map(id -> fileArtifactRepository.findById(id))
          .map(opt -> opt.map(LoadResult::new).orElse(new LoadResult<>(LoadingState.FAILED_TO_LOAD))).collect(Collectors.toList())));

      loadedDir.contains.get(0);*/
    };
  }
}


