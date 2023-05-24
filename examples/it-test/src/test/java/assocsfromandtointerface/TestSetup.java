package assocsfromandtointerface;

import java.sql.SQLException;

abstract class TestSetup {
  public abstract void initialize();

  public void clearCacheOfRemote() {
  }

  public void cleanup() throws SQLException {
  }
}
