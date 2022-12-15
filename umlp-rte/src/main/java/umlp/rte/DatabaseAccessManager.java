package umlp.rte;

import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
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

  protected void _setup() {
    loadDBPropertiesFromStandardFileLocation();
    try {
      if(databaseProperties.getProperty(JDBC_DRIVER) != null){
        Class<?> c = Class.forName(databaseProperties.getProperty(JDBC_DRIVER));
        DriverManager.registerDriver((Driver) c.getDeclaredConstructor().newInstance());
      }
      connection = DriverManager.getConnection(DB_URL, databaseProperties);
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
    executeStatement("CREATE TABLE IF NOT EXISTS __internal_key_value__ (key varchar, value varchar);");
    Optional<ResultSet> res = executeSQLQuery("SELECT key, value from __internal_key_value__ WHERE key = 'initialized';");
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
      return false;
    }else{
      executeStatement(schema);
      executeStatement("DELETE FROM __internal_key_value__ WHERE key = 'initialized';");
      executeStatement("INSERT INTO  __internal_key_value__ (key, value) VALUES ('initialized', 'true');");
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
    databaseProperties.putAll(System.getenv());
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
  }
}
