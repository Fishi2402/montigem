package dtcockpit.test_infrastructure;

import dtcockpit.DtcockpitManager;
import dtcockpit.DtcockpitTestContext;
import umlp.test_rte.ClientSetup;

public abstract class DtcockpitDBTestSetup extends DtcockpitDBTestSetupTOP {

  public DtcockpitDBTestSetup(ClientSetup clientSetup) {
    super(clientSetup);
  }

  public DtcockpitDBTestSetup(int numberOfClients) {
    super(numberOfClients);
  }

  public void registerAdmin(String userName) {
    DtcockpitManager
        .userBuilder()
        .username(userName)
        .password(userName)
        .email(userName + "@rwth-aachen.de")
        .role(dtcockpit.DtcockpitRole.ADMIN)
        .registrationDate(java.time.LocalDateTime.now())
        .build();
  }
}
