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

  public static void resetTestData() {
    JsweettestManager.getC1List().clear();
    JsweettestManager.getC2List().clear();
    JsweettestManager.getC3List().clear();

    C1 c1 = JsweettestManager.c1Builder()
        // strings
        .string("C1#1")
        // ints
        .int1(1)
        .int2(2)
        .hwGemId(-1L)
        .c2Req(randomC2(1))
        .build().get();
    c1.setHwGemId(c1.getGemId());

    c1.getC2Req().addC3mult(randomC3(1));
    c1.getC2Req().addC3mult(randomC3(2));

    c1.setC2opt(randomC2(2));

    c1.addC2mult(c1.getC2Req());
    c1.addC2mult(randomC2(3));
    c1.addC2mult(randomC2(4));
    c1.addC2mult(randomC2(5));
  }

  protected static C3 randomC3(int i) {
    return JsweettestManager.c3Builder()
        // strings
        .string("C3#" + i)
        // ints
        .int1(100 + i)
        .int2(200 + i)
        .build().get();
  }

  protected static C2 randomC2(int i) {
    return JsweettestManager.c2Builder()
        // strings
        .string("C2#" + i)
        // ints
        .int1(10 + i)
        .int2(20 + i)
        .build().get();
  }
}
