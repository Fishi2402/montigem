package gui.login;

import dtcockpit.DtcockpitManager;
import dtcockpit.User;
import dtcockpit.UserBuilder;
import dtcockpit.test_infrastructure.DtcockpitMockTestSetup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResetPasswordE2ETest extends DtcockpitMockTestSetup {

  public ResetPasswordE2ETest() {
    super(1);
  }

  @Test
  public void testReset() {

    String userName = "Max";
    String oldPassword = "foo";
    String newPassword = "bar";
    String eMail = "max@rwth-aachen.de";

    UserBuilder userBuilder = DtcockpitManager
        .userBuilder()
        .username(userName)
        .password(oldPassword)
        .email(eMail);
    long gemId = userBuilder.getGemId();
    userBuilder.build();

    getClients(0).activate();

    ResetPassword pageUnderTest = new ResetPassword(null, null);
    pageUnderTest.setGemTextInput2Entry(eMail); //email
    pageUnderTest.setGemTextInput3Entry(newPassword); //password
    pageUnderTest.gemButton6LeftClick(null); //reset pw

    getBackend().activate();

    User user = DtcockpitManager.getUserSure(gemId);
    assertEquals(newPassword, user.getPassword());
  }
}
