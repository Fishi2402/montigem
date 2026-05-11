package gui;

import mydomain.MydomainManager;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class Start extends StartTOP {
  public Consumer<Void> createErrorMessageSubject () {
    return $ -> {
      MydomainManager.errorMessageSubjectBuilder().age(20).build();
    };
  }
}
