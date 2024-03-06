import de.se_rwth.commons.logging.MCFatalError;
import dtcockpit.DtcockpitManager;
import dtcockpit.DtcockpitRole;
import dtcockpit.User;
import dtcockpit.test_infrastructure.DtcockpitMockTestSetup;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

//TODO would be better in umlp-tool/test/decorator but I don't know how to set that up properly
public class ContextMutationTest extends DtcockpitMockTestSetup {

  public ContextMutationTest() {
    super(1);
  }

  @Test
  void testContextMutation() {
    long id = 123;
    DtcockpitManager.userBuilderFromId(123)
                    .username("foo")
                    .email("bar")
                    .password("1234")
                    .role(DtcockpitRole.USER)
                    .registrationDate(LocalDateTime.now())
                    .build();

    getBackend().activate();

    User user = DtcockpitManager.getUserSure(id);
    assertDoesNotThrow(() -> user.setUsername("bob"));

    getClients(0).activate();

    //getter is okay
    assertDoesNotThrow(user::getUsername);
    //mutator shouldn't be
    assertThrows(MCFatalError.class, () -> user.setUsername("bla"));
  }
}
