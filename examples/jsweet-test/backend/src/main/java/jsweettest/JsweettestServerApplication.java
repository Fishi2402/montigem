/* (c) https://github.com/MontiCore/monticore */
package jsweettest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import javax.annotation.PostConstruct;
import java.io.IOException;

@SpringBootApplication(
    exclude={ DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
    scanBasePackages={"umlp.backendrte.service.websocket", "umlp.backendrte.service.rest", "jsweettest.service"}
)
public class JsweettestServerApplication extends JsweettestServerApplicationTOP {

  public  static  void main (String[] args){
    SpringApplication.run(JsweettestServerApplication.class, args);
  }

  @PostConstruct
  @Override
  public void init() throws IOException {
    super.init();
    JsweettestManager.aBuilder().value("Malte").build();
    JsweettestManager.aBuilder().value("Alex").build();
  }
}
