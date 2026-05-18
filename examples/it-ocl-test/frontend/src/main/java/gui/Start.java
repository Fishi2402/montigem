package gui;

import mydomain.MydomainManager;
import umlp.jsweet.extension.annotation.Component;

import java.util.Set;
import java.util.function.Consumer;

@Component
public class Start extends StartTOP {
  public Consumer<Void> createErrorMessageSubject () {
    return $ -> {
      MydomainManager.errorMessageSubjectBuilder().age(20).build();
    };
  }

  public Consumer<Void> createLinkedAccessRoot () {
    return $ -> {
      MydomainManager.linkedAccessRootBuilder()
          .name("linked access root")
          .targetAdd(MydomainManager.linkedAccessTargetBuilder().checkedValue(101))
          .build();
    };
  }

  public Consumer<Void> createLinkedAccessRootWithTooManyTargets () {
    return $ -> {
      MydomainManager.linkedAccessRootBuilder()
          .name("linked access root with too many targets")
          .targetAdd(MydomainManager.linkedAccessTargetBuilder().checkedValue(101))
          .targetAdd(MydomainManager.linkedAccessTargetBuilder().checkedValue(102))
          .targetAdd(MydomainManager.linkedAccessTargetBuilder().checkedValue(103))
          .build();
    };
  }
}
