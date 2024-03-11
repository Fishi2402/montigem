package dtcockpit.test_infrastructure;

import dtcockpit.DtcockpitManager;
import dtcockpit.DtcockpitTestContext;

import java.util.List;

public class DtcockpitDBTestSetup extends DtcockpitDBTestSetupTOP {
  public DtcockpitDBTestSetup(int numberOfClients) {
    super(numberOfClients);
  }

  public DtcockpitDBTestSetup(int numberOfClients, List<String> clientNames) {
    super(numberOfClients, clientNames);
  }

  public DtcockpitDBTestSetup(List<String> clientNames) {
    super(clientNames);
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
