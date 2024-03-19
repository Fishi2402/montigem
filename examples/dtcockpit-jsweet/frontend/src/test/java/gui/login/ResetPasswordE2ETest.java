package gui.login;

import dtcockpit.DtcockpitManager;
import dtcockpit.DtcockpitRole;
import dtcockpit.User;
import dtcockpit.UserBuilder;
import dtcockpit.test_infrastructure.DtcockpitMockTestSetup;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResetPasswordE2ETest extends DtcockpitMockTestSetup {

  //be careful when the test case creates users
  ResetPassword pageUnderTest;

  public ResetPasswordE2ETest() {
    super(1);
  }

  @Override
  public void _init() throws Exception {
    super._init();
    pageUnderTest = new ResetPassword(null, null);
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
        .email(eMail)
        .registrationDate(LocalDateTime.now())
        .role(DtcockpitRole.USER);
    long gemId = userBuilder.getGemId();
    userBuilder.build();

    getClients(0).activate();

    pageUnderTest.init(); //fetch new user-list
    pageUnderTest.setGemTextInput2Entry(eMail); //email
    pageUnderTest.setGemTextInput3Entry(newPassword); //password
    pageUnderTest.gemButton6LeftClick(null); //reset pw

    getBackend().activate();

    User user = DtcockpitManager.getUserSure(gemId);
    assertEquals(newPassword, user.getPassword());
  }

  @Test
  public void testAbort() {
    String userName = "max";
    String oldPassword = "foo";
    String newPassword = "bar";
    String eMail = "max@rwth-aachen.de";

    UserBuilder userBuilder = DtcockpitManager
        .userBuilder()
        .username(userName)
        .password(oldPassword)
        .email(eMail)
        .registrationDate(LocalDateTime.now())
        .role(DtcockpitRole.USER);
    long gemId = userBuilder.getGemId();
    userBuilder.build();

    getClients(0).activate();

    pageUnderTest.init();
    pageUnderTest.setGemTextInput2Entry(eMail); //email
    pageUnderTest.setGemTextInput3Entry(newPassword); //password
    pageUnderTest.gemButton5LeftClick(null); //abort reset

    getBackend().activate();

    User user = DtcockpitManager.getUserSure(gemId);
    assertEquals(oldPassword, user.getPassword());
  }

  @Test
  public void testTitle() {
    assertEquals("Reset password", pageUnderTest.getGemCard0Title());
  }

  @Test
  public void testLabels() {
    assertEquals("Cancel", pageUnderTest.getGemButton5Label());
    assertEquals("Reset password", pageUnderTest.getGemButton6Label());
  }
}
