package dtcockpit.test_infrastructure;

import dtcockpit.DtcockpitManager;
import umlp.test_rte.ClientSetup;

public class DtcockpitMockTestSetup extends DtcockpitMockTestSetupTOP {

  public DtcockpitMockTestSetup(ClientSetup clientSetup) {
    super(clientSetup);
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
