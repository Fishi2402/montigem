/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public abstract class DatabaseAccessManager {

  public static final String JDBC_DRIVER = "jdbc_driver";
  protected static final String DATABASE_PROPERTIES_STD_FILE_NAME = "database.properties";


  protected static String DB_URL;
  protected static Properties databaseProperties;


  protected static DatabaseAccessManager instance;
  protected static Connection connection;

  protected abstract void _setup();
  protected abstract long _getFreeGemId();
  protected abstract void _clearLocalGemIds();

  public static void setup() {
    getSingleton()._setup();
  }

  public static DatabaseAccessManager getSingleton() {
    if (instance == null) {
      throw new RuntimeException("0xHFJHDF no DatabaseAccessManager initialized.");
    }
    return instance;
  }

  public static void clearLocalGemIds(){
    getSingleton()._clearLocalGemIds();
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

  public static Connection getConnection(){
    return connection;
  }

  public static Optional<ResultSet> executeQuery(String sql) {
    try {
      Statement stmt = connection.createStatement();
      return Optional.ofNullable(stmt.executeQuery(sql));
    } catch (SQLException e) {
      Log.error("JDBC DB Connection Issue: "+e.getMessage(),e);
    }
    return Optional.empty();
  }

  public static Optional<ResultSet> executeFallibleQuery(String sql) {
    try {
      Statement stmt = connection.createStatement();
      return Optional.ofNullable(stmt.executeQuery(sql));
    } catch (SQLException e) {
      Log.warn("JDBC DB Connection Issue: "+e.getMessage(),e);
    }
    return Optional.empty();
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

  protected void loadDBPropertiesFromStandardFileLocation() {
    databaseProperties = new Properties();
    ClassLoader loader = Thread.currentThread().getContextClassLoader();

    try (InputStream is = loader.getResourceAsStream(DATABASE_PROPERTIES_STD_FILE_NAME)) {
      databaseProperties.load(is);
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Load Properties from Environment Variables
    // Each env variable needs prefix MONTIGEM_DB_, which will be removed for the property.
    // To override the property `user`, the environment variable `MONTIGEM_DB_user` must be set.
    // Can be used to select a different DB on production server
    // Environment Properties have priority!
    String prefix = "MONTIGEM_DB_";
    for (Map.Entry<String, String> envVar : System.getenv().entrySet()) {
      if(envVar.getKey().startsWith(prefix)) {
        databaseProperties.put(envVar.getKey().substring(prefix.length()), envVar.getValue());
      }
    }

    DB_URL = databaseProperties.getProperty("database_url");
  }
}
