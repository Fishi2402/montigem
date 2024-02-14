package gui.login;

import dtcockpit.DtcockpitManager;
import dtcockpit.DtcockpitRole;
import dtcockpit.User;
import gui.DtcockpitCustomMockTestSetup;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserCreateE2ETest extends DtcockpitCustomMockTestSetup {

  static String adminUserName = "admin";

  public UserCreateE2ETest() {
    super(adminUserName);
  }

  @Test
  public void testCreate() {
    String userName = "Max";
    String password = "password";
    String eMail = "max@rwth-aachen.de";

    assertTrue(getUserByEMail(eMail).isEmpty(), "user already exists");

    //TODO extract to method
    //register client as admin
    DtcockpitManager.userBuilder()
                    .username(adminUserName)
                    .password("admin")
                    .email("admin@rwth-aachen.de")
                    .role(DtcockpitRole.ADMIN)
                    .registrationDate(LocalDateTime.now())
                    .build();

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
  }

  private Optional<User> getUserByEMail(String eMail) {
    return DtcockpitManager
        .getUserList()
        .stream()
        .filter(user -> user.getEmail().equals(eMail))
        .findAny();
  }
}
