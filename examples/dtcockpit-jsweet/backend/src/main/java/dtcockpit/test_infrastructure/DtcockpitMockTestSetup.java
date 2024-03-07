package dtcockpit.test_infrastructure;

import dtcockpit.DtcockpitManager;
import dtcockpit.DtcockpitTestContext;

import java.util.List;

public class DtcockpitMockTestSetup extends DtcockpitMockTestSetupTOP {

  public DtcockpitMockTestSetup(int numberOfClients) {
    super(numberOfClients);
  }

  public DtcockpitMockTestSetup(int numberOfClients, List<String> clientNames) {
    super(numberOfClients, clientNames);
  }

  public DtcockpitMockTestSetup(List<String> clientNames) {
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
