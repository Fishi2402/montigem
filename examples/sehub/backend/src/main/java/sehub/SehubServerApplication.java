package sehub;

import dummy.ContainerFileExtractor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
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
          .username("MyUser").username("MyUser")
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
        .title("")
        .text("Post 1")
        .responses(Collections.emptySet())
        .build();

    Optional<Post> p2 = SehubManager.postBuilder()
        .title("")
        .text("Post 2")
        .responses(Collections.emptySet())
        .build();

    Set<Post> responses = new java.util.HashSet<>(Collections.emptySet());
    responses.add(p1.get());
    responses.add(p2.get());

    Optional<Post> p3 = SehubManager.postBuilder()
        .title("")
        .text("Post 3")
        .responses(responses)
        .build();

    IssueBuilder issueBuilder = new IssueBuilder(42000);
        issueBuilder
        .title("MyNewIssue")
        .description("Something")
        .state(IssueState.IN_PROGRESS)
        .posts(Set.of(p3.get()))
        .build();


    createDummyData();
  }

  private void createDummyData() {
    String path = "src/main/java";
    File file = new File(path);
    ContainerFileExtractor extractor = new ContainerFileExtractor();
    extractor.extractGivenRoot(List.of(file));
  }
}
