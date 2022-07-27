/* (c) https://github.com/MontiCore/monticore */
package hardy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class UMLPApplication {
  public static void main(String[] args) {
    SpringApplication.run(UMLPApplication.class, args);
    FlensburgMock.init();
    (new FlensburgDummies()).inventDummiesGenerated(5,5,5);
  }
}
