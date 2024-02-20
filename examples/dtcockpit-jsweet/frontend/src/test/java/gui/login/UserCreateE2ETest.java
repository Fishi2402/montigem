package gui.login;

import dtcockpit.DtcockpitManager;
import dtcockpit.DtcockpitRole;
import dtcockpit.User;
import dtcockpit.test_infrastructure.DtcockpitMockTestSetup;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserCreateE2ETest extends DtcockpitMockTestSetup {

  static String adminUserName = "admin";

  public UserCreateE2ETest() {
    super(List.of(adminUserName));
  }

  @Test
  public void testCreate() {
    MailServiceMock.init();

    String userName = "Max";
    String password = "password";
    String eMail = "max@rwth-aachen.de";

    assertTrue(getUserByEMail(eMail).isEmpty(), "user already exists");

    //register client as admin
    registerAdmin(adminUserName);

    getClients(0).activate();

    UserCreate pageUnderTest = new UserCreate();
    pageUnderTest.setGemTextInput2Entry(userName); //username
    pageUnderTest.setGemTextInput3Entry(password); //password
    pageUnderTest.setGemTextInput4Entry(eMail); //e-mail

    pageUnderTest.gemButton7LeftClick(null); //create

    getBackend().activate();

    var optUser = getUserByEMail(eMail);
    assertTrue(optUser.isPresent(), "user has not been created");

    User user = optUser.get();

    assertEquals(userName, user.getUsername());
    assertEquals(password, user.getPassword());
    assertEquals(eMail, user.getEmail());
    assertEquals(DtcockpitRole.USER, user.getRole());

    assertFalse(MailServiceMock.getInstance().getMails().isEmpty());
  }

  @Test
  public void testAbort() {
    String userName = "Max";
    String password = "password";
    String eMail = "max@rwth-aachen.de";

    assertTrue(getUserByEMail(eMail).isEmpty(), "user already exists");

    //register client as admin
    registerAdmin(adminUserName);

    getClients(0).activate();

    UserCreate pageUnderTest = new UserCreate();
    pageUnderTest.setGemTextInput2Entry(userName); //username
    pageUnderTest.setGemTextInput3Entry(password); //password
    pageUnderTest.setGemTextInput4Entry(eMail); //e-mail

    pageUnderTest.gemButton6LeftClick(null); //abort

    getBackend().activate();

    var optUser = getUserByEMail(eMail);
    assertTrue(optUser.isEmpty(), "user has been created");
  }

  private Optional<User> getUserByEMail(String eMail) {
    return DtcockpitManager
        .getUserList()
        .stream()
        .filter(user -> user.getEmail().equals(eMail))
        .findAny();
  }
}
