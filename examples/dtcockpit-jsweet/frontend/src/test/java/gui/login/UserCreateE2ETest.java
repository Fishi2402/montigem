package gui.login;

import dtcockpit.DtcockpitManager;
import dtcockpit.User;
import dtcockpit.test_infrastructure.DtcockpitMockTestSetup;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserCreateE2ETest extends DtcockpitMockTestSetup {

  public UserCreateE2ETest() {
    super(1);
  }

  @Test
  public void testCreate() {
    String userName = "Max";
    String password = "password";
    String eMail = "max@rwth-aachen.de";

    assertTrue(getUserByEMail(eMail).isEmpty(), "user already exists");

    getClients(0).activate();

    UserCreate pageUnderTest = new UserCreate();
    pageUnderTest.setGemTextInput2Entry(userName); //username
    pageUnderTest.setGemTextInput3Entry(password); //password
    pageUnderTest.setGemTextInput4Entry(eMail); //e-mail

    pageUnderTest.gemButton7LeftClick(null); //create

    assertNull(pageUnderTest.getErrorValue());

    getBackend().activate();

    var optUser = getUserByEMail(eMail);
    assertTrue(optUser.isPresent(), "user has not been created");

    User user = optUser.get();

    assertEquals(userName, user.getUsername());
    assertEquals(password, user.getPassword());
    assertEquals(eMail, user.getEmail());
  }

  private Optional<User> getUserByEMail(String eMail) {
    return DtcockpitManager
        .getUserList()
        .stream()
        .filter(user -> user.getEmail().equals(eMail))
        .findAny();
  }
}
