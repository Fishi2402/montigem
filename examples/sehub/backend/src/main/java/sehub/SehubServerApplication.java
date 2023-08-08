package sehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootApplication(
    exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages = {"umlp.backendrte.service.websocket", "umlp.backendrte.service.rest", "sehub.service"}
)
public class SehubServerApplication extends SehubServerApplicationTOP {

  public static void main(String[] args) {
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
          .registrationDate(LocalDateTime.now())
          .username("MyUser").username("MyUser")
          .email("MyEmail")
          .activated(ActivationStatus.AKTIVIERT)
          .build();
      if (newUser.isPresent()) {
        UserDeSer userDeSer = new UserDeSer();
        System.out.println(userDeSer.serialize(newUser.get()));
      }
    }
  }

}
