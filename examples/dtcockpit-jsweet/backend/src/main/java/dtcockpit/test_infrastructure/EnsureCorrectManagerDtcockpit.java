package dtcockpit.test_infrastructure;

import dtcockpit.DtcockpitManager;
import dtcockpit.DtcockpitObserver;
import dtcockpit.User;
import umlp.backendrte.common.NotificationScope;

// TODO: generate
public class EnsureCorrectManagerDtcockpit implements DtcockpitObserver {
  private final DtcockpitManager manager;
  private final EnsureCorrectManager4User ensureCorrectManager4User;

  public EnsureCorrectManagerDtcockpit(DtcockpitManager manager) {
    this.manager = manager;
    ensureCorrectManager4User = new EnsureCorrectManager4User(this.manager);
  }

  @Override
  public void notifyUserAdded(User user) {
    if(!user.containsObserver(ensureCorrectManager4User)){
      user.addObserver(ensureCorrectManager4User);
    }
  }

  @Override
  public NotificationScope getScope() {
    return NotificationScope.MINIMAL;
  }

  // TODO: other domain types
}
