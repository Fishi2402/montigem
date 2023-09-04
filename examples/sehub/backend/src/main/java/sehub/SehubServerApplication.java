package sehub;

import de.se_rwth.commons.logging.Log;
import dummy.DummyDataCreator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

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

    // Only create DummyData if database is empty
    if(SehubManager.getUserList().isEmpty()){ // Is there a better way to check if dummy-data was already created?
      Log.println("Creating Dummy Data");
      createDummyData();
    } else {
      Log.println("_NOT_ Creating New Dummy Data. Data is already present.");
    }
  }

  public void createDummyData(){
    for (User user : SehubManager.getUserList()) {
      System.err.println("User " + user.getUsername());
    }

    Set<BingoEntry> entrySet = new java.util.HashSet<>(Collections.emptySet());
    for (int i = 0; i <= 24; i++) {
      entrySet.add(SehubManager.bingoEntryBuilder().word("").build().get());
    }
    Optional<BingoBoard> bingoBoard = SehubManager.bingoBoardBuilder()
        .bingoBoardEntries(entrySet)
        .build();

    Set<BoardMapper> mapping = new java.util.HashSet<>(Collections.emptySet());
    int [] permutation = getBingoBoardPermutation();

    for (int i = 0; i <= 24; i++) {
      mapping.add(SehubManager.boardMapperBuilder().bingoBoardEntry(bingoBoard.get().getBingoBoardEntries(permutation[i])).isChecked(false).build().get());
    }

    if (SehubManager.getUserList().isEmpty()) {
      Optional<User> newUser = SehubManager.userBuilder()
          .username("MyUser")
          .password("MyPasswort")
          .registrationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
          .email("MyEmail")
          .gridEntry(mapping)
      //    .activated(ActivationStatus.AKTIVIERT)
          .role(SehubRole.OWNER)
          .build();


      if (newUser.isPresent()) {
        UserDeSer userDeSer = new UserDeSer();
        System.out.println(userDeSer.serialize(newUser.get()));
      }
    }



    Set<BoardMapper> mapping2 = new java.util.HashSet<>(Collections.emptySet());
    int [] permutation2 = getBingoBoardPermutation();

    for (int i = 0; i <= 24; i++) {
      mapping2.add(SehubManager.boardMapperBuilder().bingoBoardEntry(bingoBoard.get().getBingoBoardEntries(permutation2[i])).isChecked(false).build().get());
    }

    Optional<User> newUser2 = SehubManager.userBuilder()
        .username("admin")
        .password("pwd")
        .registrationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
        .email("admin@sehub.de")
        .gridEntry(mapping2)
        //    .activated(ActivationStatus.AKTIVIERT)
        .role(SehubRole.OWNER)
        .build();

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

    IssueBuilder issueBuilder = SehubManager.issueBuilder();
        issueBuilder
        .title("Die Sache mit dem Wasserkocher")
        .description("Hier kann eine Beschreibung stehen...")
        .state(IssueState.IN_PROGRESS)
        .posts(Set.of(p3.get()))
        .build();



    SehubManager.issueBuilder()
        .title("Verfügbare Konten sollen beim Bearbeiten von Dienstreisen auswählbar sein")
        .description("Wenn eine bestehende Dienstreise bearbeitet wird, kann kein neues Konto ausgewählt werden. " +
            "Zuerst muss die zu bearbeitende Dienstreise gelöscht, gespeichert und erneut bearbeitet werden. " +
            "Erst dann werden die Kontooptionen angezeigt." +
            "Es sollte möglich sein, direkt ein neues Konto auszuwählen.")
        .state(IssueState.DONE)
        .build();

    SehubManager.issueBuilder()
        .title("Änderungsdatum Erweiterung")
        .description("Das Änderungsdatum für Buchungen, Mittelabrufe, Rechnungen, Mittelzuweisungen und Stellenzuweisungen " +
            "ist aktuell nur auf der Seite Finazen/Zahlungen sichtbar. Das Änderungsdatum soll aber auch für die " +
            "einzelnen Elemente auf den Kontenseiten in einem Konto selbst sichtbar sein.")
        .state(IssueState.IN_PROGRESS)
        .build();


    SehubManager.issueBuilder()
        .title("Finanzen: Umfang einer Zuweisung zu Organigrammstufe im Konto beschränken")
        .description("Bei der Zuweisung einer Organigrammstufe in einem Konto kann ein Umfang hinterlegt werden. " +
            "Standardmäßig ist der Umfang mit 100% vorausgefüllt. Der Umfang kann allerdings bearbeitet werden und " +
            "auf mehr als 100% angepasst werden. Eine Zuweisung von mehr als 100% ergibt jedoch keinen Sinn, weshalb " +
            "der maximal zulässige Umfang auf 100% beschränkt werden sollte.")
        .state(IssueState.UNASSIGNED)
        .build();


    new DummyDataCreator().createDummyData();
  }

  public static int[] getBingoBoardPermutation() {
    int[] arr = IntStream.rangeClosed(0, 24).toArray();
    Random random = new Random();

    for (int i = arr.length - 1; i > 0; i--) {
      int j = random.nextInt(i + 1); // random index from 0 to i
      int temp = arr[i];
      arr[i] = arr[j];
      arr[j] = temp; // Swap elements
    }

    return arr;
  }


}
