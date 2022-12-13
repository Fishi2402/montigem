package umlp.rte;

import de.monticore.cd.codegen.methods.accessor.OptionalAccessorDecorator;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Optional;
import java.util.Properties;


public class DatabaseAccessManager {

  public static void init(String[] args) {
  }

  private static String DB_URL;

  private static final String DATABASE_PROPERTIES = "database.properties";

  private static DatabaseAccessManager instance;

  private static Properties databaseProperties;

  private static Connection connection;

  /**
   * Don't let anyone else instantiate this class.
   */
  private DatabaseAccessManager() {
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

  private void _setup() {
    loadDBPropertiesFromStandardFileLocation();
    try {
      DriverManager.registerDriver(new org.postgresql.Driver());
      connection = DriverManager.getConnection(DB_URL, databaseProperties);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
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

  private void loadDBPropertiesFromStandardFileLocation() {
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

  private void loadDBPropertiesFromPropertiesFile(String path) {
    databaseProperties = new Properties();
    ClassLoader loader = Thread.currentThread().getContextClassLoader();

    try (InputStream is = loader.getResourceAsStream(DATABASE_PROPERTIES)) {
      databaseProperties.load(is);
    } catch (IOException e) {
      e.printStackTrace();
    }
    DB_URL = databaseProperties.getProperty("database_url");
  }
}
