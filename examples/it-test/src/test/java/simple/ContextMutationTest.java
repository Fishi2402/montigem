package simple;

import context.ContextManager;
import context.Person;
import context.test_infrastructure.ContextMockTestSetup;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.MCFatalError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ContextMutationTest extends ContextMockTestSetup {

  public ContextMutationTest() {
    super(1);
  }

  @Test
  void testContextMutation() {
    long id = 123;
    ContextManager.personBuilderFromId(id).name("Bob").age(24).build();

    getBackend().activate();

    Person user = ContextManager.getPersonSure(id);
    assertDoesNotThrow(() -> user.setName("Robert"));

    getClients(0).activate();

    //getter is okay
    Assertions.assertDoesNotThrow(user::getName);

    //mutator shouldn't be
    Log.setErrorHook(() -> {throw new MCFatalError("");});
    assertThrows(MCFatalError.class, () -> user.setName("Bobbera"));
  }
}
