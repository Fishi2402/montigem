package sehub;

import de.se_rwth.commons.logging.Log;
import dummy.ContainerFileExtractor;
import dummy.GitRepoLoader;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

import umlp.backendrte.common.GemIdentifiable;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SpringBootApplication(
    exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages={"umlp.backendrte.service.websocket", "umlp.backendrte.service.rest", "sehub.service"}
)
public class SehubServerApplication extends sehub.SehubServerApplicationTOP {

  public  static  void main (String[] args){
    SpringApplication.run(SehubServerApplication.class, args);
  }

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();

    for (User user : SehubManager.getUserList()) {
      System.err.println("User " + user.getUsername());
    }

    if (SehubManager.getUserList().isEmpty()) {
      Optional<User> newUser = SehubManager.userBuilder()
          .username("MyUser")
          .password("MyPasswort")
          .registrationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
          .email("MyEmail")
      //    .activated(ActivationStatus.AKTIVIERT)
          .role(SehubRole.OWNER)
          .build();
      if (newUser.isPresent()) {
        UserDeSer userDeSer = new UserDeSer();
        System.out.println(userDeSer.serialize(newUser.get()));
      }
    }

    Optional<Post> p1 = SehubManager.postBuilder()
        .author(SehubManager.getUserList().get(0))
        .title("")
        .text("Post 1")
        .responses(Collections.emptySet())
        .build();

    Optional<Post> p2 = SehubManager.postBuilder()
        .author(SehubManager.getUserList().get(0))
        .title("")
        .text("Post 2")
        .responses(Collections.emptySet())
        .build();

    Set<Post> responses = new java.util.HashSet<>(Collections.emptySet());
    responses.add(p1.get());
    responses.add(p2.get());

    Optional<Post> p3 = SehubManager.postBuilder()
        .author(SehubManager.getUserList().get(0))
        .title("")
        .text("Post 3")
        .responses(responses)
        .build();

    IssueBuilder issueBuilder = new IssueBuilder(42000);
        issueBuilder
        .title("MyIssue")
        .description("Post 4")
        .state(IssueState.IN_PROGRESS)
        .posts(Set.of(p3.get()))
        .build();

    createDummyData();
  }

  private void createDummyData() {
    ContainerFileExtractor extractor = new ContainerFileExtractor();
    String token = System.getenv("gitlab-token");
    if(token != null) {
      String repoPath = "monticore/monticore";
      File f = GitRepoLoader.gitClone("git.rwth-aachen.de",repoPath, token);
      extractor.extractGivenRoot(List.of(f));

      // Free disk space from pulled git repo
      try {
        // Git files are readOnly thus cannot be easily deleted after cloning
        for (Iterator<File> i = FileUtils.iterateFiles(f, new String[]{"idx", "pack"}, true); i.hasNext();) {
          File item = i.next();
          item.setWritable(true);
        }
        FileUtils.deleteDirectory(f);
      } catch (IOException e) {
        Log.warn("Could not free disk space of extracted git repo.", e);
      }
    } else {
      Log.warn("No environment variable set with name "+"gitlab-token");
    }
    String path = "src/main/java";
    File file = new File(path);
    extractor.extractGivenRoot(List.of(file));
  }
}
