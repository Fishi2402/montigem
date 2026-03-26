package gui;

import mydomain.MydomainManager;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class Start extends StartTOP {
  public Consumer<Void> createPerson () {
    return $ -> {
      MydomainManager.personBuilder().age(20).adult(true).name("John Doe").build();
    };
  }
}
