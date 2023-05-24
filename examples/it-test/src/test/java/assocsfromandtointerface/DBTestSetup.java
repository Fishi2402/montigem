package assocsfromandtointerface;

import org.apache.commons.io.IOUtils;
import umlp.backendrte.common.DatabaseAccessManager;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DBTestSetup extends TestSetup {
  @Override
  public void initialize() {
    try {
      AssocsFromAndToInterfaceManagerImpl4Test instance = new AssocsFromAndToInterfaceManagerImpl4Test();
      instance.assocsFromAndToInterfaceObservers.add(new AssocsFromAndToInterfaceDbObserver());
      AssocsFromAndToInterfaceManager.initMe(instance);
      String schema = IOUtils.resourceToString("/AssocsFromAndToInterface.sql", StandardCharsets.UTF_8);
      assertTrue(DatabaseAccessManager.initSchema(schema));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void clearCacheOfRemote() {
    ((AssocsFromAndToInterfaceManagerImpl4Test) AssocsFromAndToInterfaceManager.getSingleton()).clearCache();
  }

  @Override
  public void cleanup() throws SQLException {
    DatabaseAccessManager.closeConnection();
  }
}
