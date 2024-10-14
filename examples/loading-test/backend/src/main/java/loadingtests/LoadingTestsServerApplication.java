package loadingtests;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Set;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class}, scanBasePackages = {"umlp.backendrte.service.websocket",
    "umlp.backendrte.service.rest"})
public class LoadingTestsServerApplication extends LoadingTestsServerApplicationTOP {

  public static void main(String[] args) {
    SpringApplication.run(LoadingTestsServerApplication.class, args);
  }

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();
  }
}
