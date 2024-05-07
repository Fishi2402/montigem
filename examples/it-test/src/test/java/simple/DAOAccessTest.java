package simple;

import context.ContextDAOFactory;
import context.test_infrastructure.ContextSQLDBTestSetup;
import de.se_rwth.commons.logging.MCFatalError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DAOAccessTest extends ContextSQLDBTestSetup {
  public DAOAccessTest() {
    super(1);
  }

  @Test
  void testDAOAccess() {
    //backend may access
    getBackend().activate();
    assertDoesNotThrow(ContextDAOFactory::getPersonDAO);

    //clients may not
    getClients(0).activate();
    assertThrows(MCFatalError.class, ContextDAOFactory::getPersonDAO);
  }
}
