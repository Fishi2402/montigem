package db;

import de.se_rwth.commons.logging.Log;
import dtcockpit.App;
import dtcockpit.DtcockpitDAOFactory;
import dtcockpit.DtcockpitManager;
import dtcockpit.test_infrastructure.DtcockpitDBTestSetup;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReloadAppFromDb extends DtcockpitDBTestSetup {


  public ReloadAppFromDb() {
    super(0);
  }

  @Test
  public void testLoadingExistingAppInstanceFromDb(){
    App app = DtcockpitManager.getApp();
    assertNotNull(app);

    DtcockpitManager.TESTING_clearCache();

    // Is in DB
    Optional<App> appCloneOpt = DtcockpitDAOFactory.getAppDAO().selectApp(app.getGemId());
    assertTrue(appCloneOpt.isPresent());


    // Manager can load
    App app2 = DtcockpitManager.getApp();
    assertEquals(app, app2);
    assertEquals(0, Log.getErrorCount());
  }
}
