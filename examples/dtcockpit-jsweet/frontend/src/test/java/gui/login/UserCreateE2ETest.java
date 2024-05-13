package gui.login;

import dtcockpit.DtcockpitManager;
import dtcockpit.DtcockpitRole;
import dtcockpit.DtcockpitTestContext;
import dtcockpit.User;
import dtcockpit.test_infrastructure.DtcockpitDBTestSetup;
import dtcockpit.test_infrastructure.DtcockpitMockTestSetup;
import dtcockpit.test_infrastructure.DtcockpitSQLDBTestSetup;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import umlp.backendrte.test_infrastructure.ClientSetup;
import umlp.backendrte.test_infrastructure.TestSetup;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// TODO: Generate common super class of DtcockpitMockTestSetup and DtcockpitDBTestSetup,
// for simultaneous adaption using TOP
public interface UserCreateE2ETest extends TestSetup<DtcockpitTestContext> {
  String adminUserName = "admin";

  class UserCreateE2EMockTest extends DtcockpitMockTestSetup implements UserCreateE2ETest {
    public UserCreateE2EMockTest() {
      super(ClientSetup.of(adminUserName));
    }
  }

  @Disabled
  class UserCreateE2EDbTest extends DtcockpitSQLDBTestSetup implements UserCreateE2ETest {
    public UserCreateE2EDbTest() {
      super(ClientSetup.of(adminUserName));
    }
  }

  @Test
  default void testCreate() {
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
  default void testAbort() {
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

  void registerAdmin(String adminUserName);

  private Optional<User> getUserByEMail(String eMail) {
    return DtcockpitManager
        .getUserList()
        .stream()
        .filter(user -> user.getEmail().equals(eMail))
        .findAny();
  }
}
