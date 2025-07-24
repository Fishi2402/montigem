package test_update_after_delete;

import loadingtests.LoadingTestsManager;
import loadingtests.Person;

import java.util.function.Consumer;

public class PersonsDeleteButton extends PersonsDeleteButtonTOP {

  public Consumer<Void> deletePerson(Person p) {
    return $ -> {
      LoadingTestsManager.deletePerson(p.getGemId());
    };
  }
}
