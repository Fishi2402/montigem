package umlp.backendrte.common;

import de.se_rwth.commons.logging.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DatabaseAccessManager {

  protected static DatabaseAccessManager instance;
  protected static Connection connection;

  protected abstract void _setup();
  protected abstract long _getFreeGemId();

  public static void setup() {
    getSingleton()._setup();
  }

  public static DatabaseAccessManager getSingleton() {
    if (instance == null) {
      throw new RuntimeException("0xHFJHDF no DatabaseAccessManager initialized.");
    }
    return instance;
  }

  public static long getFreeGemId(){
    return getSingleton()._getFreeGemId();
  }

  public static PreparedStatement prepareStatement(String statement){
    return getSingleton()._prepareStatement(statement);
  }

  public PreparedStatement _prepareStatement(String statement) {
    try {
      return connection.prepareStatement(statement);
    } catch (SQLException e) {
      Log.error("Error while preparing statement\n" + statement, e);
      return null;
    }
  }

  public static void executeStatement(String sql){
    try{
      Statement stmt = connection.createStatement();
      stmt.execute(sql);
    }catch (SQLException e){
      Log.error("JDBC DB Connection Issue: "+e.getMessage(),e);
    }
  }

  public static void setInstance(DatabaseAccessManager databaseAccessManager) {
    instance = databaseAccessManager;
  }

  public static void closeConnection() throws SQLException {
    if (connection == null) {
      Log.warn("Can not close DB connection, since it is null");
      return;
    }

    if (!connection.isClosed()) {
      getSingleton()._closeConnection();
      Log.warn("Closed DB Connection");
    }
  }

  public void _closeConnection() throws SQLException {
    if (connection == null) {
      Log.warn("Can not close DB connection, since it is null");
      return;
    }

    if (!connection.isClosed()) {
      connection.close();
      Log.warn("Closed DB Connection");
    }
  }

}
