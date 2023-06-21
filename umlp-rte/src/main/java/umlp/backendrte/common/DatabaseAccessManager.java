/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;


public class DatabaseAccessManager {

  public static final String JDBC_DRIVER = "jdbc_driver";
  // TODO: replace with database access?
  protected static int gemId = 0;

  public static void init(String[] args) {
  }

  protected static String DB_URL;

  protected static final String DATABASE_PROPERTIES = "database.properties";

  protected static DatabaseAccessManager instance;

  protected static Properties databaseProperties;

  protected static Connection connection;

  protected DatabaseAccessManager() {
  }

  public static DatabaseAccessManager getSingleton() {
    if (instance == null) {
      instance = new DatabaseAccessManager();
    }
    return instance;
  }

  public static void setup() {
    getSingleton()._setup();
  }

  public static void closeConnection() throws SQLException {
    if (connection == null) {
      Log.warn("Can not close DB connection, since it is null");
      return;
    }

    if (!connection.isClosed()) {
      connection.close();
      Log.warn("Closed DB Connection");
    }
  }

  protected void _setup() {
    loadDBPropertiesFromStandardFileLocation();
    try {
      if(databaseProperties.getProperty(JDBC_DRIVER) != null){
        Class<?> c = Class.forName(databaseProperties.getProperty(JDBC_DRIVER));
        DriverManager.registerDriver((Driver) c.getDeclaredConstructor().newInstance());
      }
      connection = DriverManager.getConnection(DB_URL, databaseProperties);
      Log.warn("New DB Connection created");
    } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException |
             InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }

  public static void executeSQLUpdate(String sql) {
    try {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
      Log.error("JDBC DB Connection Issue: "+e.getMessage(),e);
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

  // TODO: versioning?

  /**
   * @return true if the schema was initialized during this call, false if it was already initialized before
   */
  public static boolean initSchema(String schema){
    boolean initialized = false;

    executeStatement("CREATE TABLE IF NOT EXISTS __internal_key_value__ (k VARCHAR, value_bigint BIGINT, value_varchar VARCHAR);");
    Optional<ResultSet> res = executeSQLQuery("SELECT k, value_varchar from __internal_key_value__ WHERE k = 'initialized';");
    if(res.isPresent()){
      ResultSet resultSet = res.get();
      try {
        if(resultSet.next()){
          if(resultSet.getString(2).equals("true")){
            initialized = true;
          }
        }
      } catch (SQLException e) {
        Log.error("JDBC DB Connection Issue: "+e.getMessage(),e);
      }
    }

    if(initialized){
      Log.warn("Skipping initSchema since the database is already initialized");
      return false;
    }else{
      executeStatement(schema);
      executeStatement("DELETE FROM __internal_key_value__ WHERE k = 'initialized';");
      executeStatement("INSERT INTO  __internal_key_value__ (k, value_varchar) VALUES ('initialized', 'true');");
      executeStatement("INSERT INTO  __internal_key_value__ (k, value_bigint) VALUES ('gemid', 0);");
      return true;
    }
  }

  public static Optional<ResultSet> executeSQLQuery(String sql) {
    try {
      Statement stmt = connection.createStatement();
      return Optional.ofNullable(stmt.executeQuery(sql));
    } catch (SQLException e) {
      Log.error("JDBC DB Connection Issue: "+e.getMessage(),e);
    }
    return Optional.empty();
  }

  public static void setUpWithProperties(Properties properties) {
    databaseProperties = properties;
    DB_URL = databaseProperties.getProperty("database_url");
  }

  public static PreparedStatement prepareStatement(String statement){
    return getSingleton()._prepareStatement(statement);
  }

  public PreparedStatement _prepareStatement(String statement){
    try {
      return connection.prepareStatement(statement);
    } catch (SQLException e) {
      Log.error("Error while preparing statement\n" + statement, e);
      return null;
    }
  }

  protected void loadDBPropertiesFromStandardFileLocation() {
    databaseProperties = new Properties();
    ClassLoader loader = Thread.currentThread().getContextClassLoader();

    try (InputStream is = loader.getResourceAsStream(DATABASE_PROPERTIES)) {
      databaseProperties.load(is);
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

  protected void loadDBPropertiesFromPropertiesFile(String path) {
    databaseProperties = new Properties();
    ClassLoader loader = Thread.currentThread().getContextClassLoader();

    try (InputStream is = loader.getResourceAsStream(DATABASE_PROPERTIES)) {
      databaseProperties.load(is);
    } catch (IOException e) {
      e.printStackTrace();
    }
    DB_URL = databaseProperties.getProperty("database_url");
  }

  public static long getFreeGemId(){
    return gemId++;
    // TODO: Bottelneck: one db call per gemid is slow
    // Idea: "reserve" 1000 gemids in one call
    // If the server dies, some ids are "wasted"
    // could be done asynchronously at 90% ids used to remove wait
    /*executeStatement("UPDATE __internal_key_value__ SET value_bigint = value_bigint + 1 WHERE k = 'gemid';");
    Optional<ResultSet> rsOpt = executeSQLQuery("SELECT value_bigint FROM __internal_key_value__ WHERE k = 'gemid';");
    if(rsOpt.isEmpty()){
      throw new RuntimeException("Can not find gemid in database");
    }

    ResultSet rs = rsOpt.get();

    try {
      if(!rs.next()){
        throw new RuntimeException("Can not find gemid in database");
      }else{
        return rs.getLong(1);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }*/
  }
}
