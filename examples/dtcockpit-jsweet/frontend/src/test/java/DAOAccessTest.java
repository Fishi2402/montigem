import de.se_rwth.commons.logging.MCFatalError;
import dtcockpit.DtcockpitDAOFactory;
import dtcockpit.test_infrastructure.DtcockpitMockTestSetup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

//TODO would fit better into umlp-tool/test/decorator, but I don't know how to set it up there
public class DAOAccessTest extends DtcockpitMockTestSetup {

  public DAOAccessTest() {
    super(1);
  }

  @Test
  public void testDAOAccess() {
    getBackend().activate();

    //should be fine
    assertDoesNotThrow(DtcockpitDAOFactory::getAppDAO);

    getClients(0).activate();

    //should not be fine anymore
    assertThrows(MCFatalError.class, DtcockpitDAOFactory::getAppDAO);
  }
}
