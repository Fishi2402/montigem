/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Objects;
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
    try(Statement stmt = connection.createStatement()){
      stmt.execute(sql);
    }catch (SQLException e){
      Log.error("JDBC DB Connection Issue: "+e.getMessage(),e);
    }
  }

  public static Connection getConnection(){
    return connection;
  }

  // Should not be used because we cannot control closing the Stmnt which leads to problems with certain JDBC drivers such as Neo4j
  @Deprecated
  public static Optional<ResultSet> executeQuery(String sql) {
    try{
      Statement stmt = connection.createStatement();
      return Optional.ofNullable(stmt.executeQuery(sql));
    } catch (SQLException e) {
      Log.error("JDBC DB Connection Issue: "+e.getMessage(),e);
    }
    return Optional.empty();
  }

  @Deprecated(since="Do not use this Method as we cannot control closing the Statement and ResultSet")
  public static Optional<ResultSet> executeFallibleQuery(String sql) {
    try(Statement stmt = connection.createStatement()){
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
      databaseProperties.load(Objects.requireNonNull(is, "Failed to find resource " + DATABASE_PROPERTIES_STD_FILE_NAME));
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Load Properties from Environment Variables
    // Can be used to select a different DB on production server
    // Environment Properties have priority!
    // TODO: Workaround for #3358
    for (Map.Entry<String, String> envVar : System.getenv().entrySet()) {
      if(!envVar.getKey().equalsIgnoreCase("user")) {
        databaseProperties.put(envVar.getKey(), envVar.getValue());
      }
    }

    DB_URL = databaseProperties.getProperty("database_url");
  }
}
