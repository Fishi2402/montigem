package sehub;

import de.se_rwth.commons.logging.Log;
import dummy.ContainerFileExtractor;
import dummy.DummyDataCreator;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

import java.io.File;
import java.util.List;
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
        .title(" | 15:57 ]: Können Wasserkocher Fliegen ?")
        .text("Nein")
        .responses(Collections.emptySet())
        .build();

    Optional<Post> p2 = SehubManager.postBuilder()
        .author(SehubManager.getUserList().get(0))
        .title(" | 16:34 ]: Können Wasserkocher Fliegen ?")
        .text("Ja, während eines FoWos")
        .responses(Collections.emptySet())
        .build();

    Set<Post> responses = new java.util.HashSet<>(Collections.emptySet());
    responses.add(p1.get());
    responses.add(p2.get());

    Optional<Post> p3 = SehubManager.postBuilder()
        .author(SehubManager.getUserList().get(0))
        .title("Können Wasserkocher Fliegen ?")
        .text("Wir sollten diese Frage vor dem FoWo klären! Ggf. Sollten Wasserkocher für eine Testreihe organisiert werden!")
        .responses(responses)
        .build();

    IssueBuilder issueBuilder = new IssueBuilder(42000);
        issueBuilder
        .title("Die Sache mit dem Wasserkocher")
        .description("Hier kann eine Beschreibung stehen...")
        .state(IssueState.IN_PROGRESS)
        .posts(Set.of(p3.get()))
        .build();

    Set<BingoEntry> entrySet = new java.util.HashSet<>(Collections.emptySet());
    for (int i = 0; i <= 24; i++) {
      entrySet.add(SehubManager.bingoEntryBuilder().word("").isChecked(false).build().get());
    }
    BingoBoardBuilder boardBuilder = new BingoBoardBuilder(42001);
      boardBuilder
          .entries(entrySet)
          .build();

    new DummyDataCreator().createDummyData();
  }

}
