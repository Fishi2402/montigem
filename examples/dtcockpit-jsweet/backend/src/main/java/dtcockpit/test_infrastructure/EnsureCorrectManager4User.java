package dtcockpit.test_infrastructure;

import de.se_rwth.commons.logging.Log;
import dtcockpit.DtcockpitManager;
import dtcockpit.DtcockpitRole;
import dtcockpit.User;
import dtcockpit.UserObserver;

import java.time.LocalDateTime;

// TODO: generate
// TODO: other domain types
public class EnsureCorrectManager4User implements UserObserver {
  private final DtcockpitManager manager;

  public EnsureCorrectManager4User(DtcockpitManager manager) {
    this.manager = manager;
  }

  private void checkManager() {
    DtcockpitManager inst = DtcockpitManager.getSingleton();
    if(inst != manager){
      Log.error("Mutator called on object, but the manager changed. Please activate the correct context before calling the mutator. Manager responsible for this object: " + manager + ", current manager: " + inst);
    }
  }

  @Override
  public void notifySetUsername(User user, String oldValue, String o) {
    checkManager();
  }

  @Override
  public void notifySetPassword(User user, String oldValue, String o) {
    checkManager();
  }

  @Override
  public void notifySetRegistrationDate(User user, LocalDateTime oldValue, LocalDateTime o) {
    checkManager();
  }

  @Override
  public void notifySetEmail(User user, String oldValue, String o) {
    checkManager();
  }

  @Override
  public void notifySetRole(User user, DtcockpitRole oldValue, DtcockpitRole o) {
    checkManager();
  }
}
