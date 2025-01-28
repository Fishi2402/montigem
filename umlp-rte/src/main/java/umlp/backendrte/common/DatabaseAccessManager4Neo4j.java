/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.common;

import de.se_rwth.commons.logging.Log;

import java.lang.reflect.InvocationTargetException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class DatabaseAccessManager4Neo4j extends DatabaseAccessManager {

  GemIdManager4Neo4j idManager4Neo4j = new GemIdManager4Neo4j();

  @Override
  protected long _getFreeGemId() {
    return idManager4Neo4j.getFreeGemId();
  }

  @Override
  protected void _clearLocalGemIds(){
    idManager4Neo4j.clear();
  }

  @Override
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
    createMetaNodeSingleton();
  }

  /**
   * This method is called once when the DB is started to create a MetaNode which only contains a GemID Counter for now.
   * The MetaNode is a singleton and is only created, if it does not exist already (DB was initialized beforehand).
   */
  protected void createMetaNodeSingleton() {
    boolean createMetaNode = false;
    try(Statement stmt = connection.createStatement()) {
      ResultSet resultSet= stmt.executeQuery("MATCH(n:METANODE) return n");
      createMetaNode = !resultSet.next();
    } catch (SQLException var2) {
      Log.error("0xadakfjjflsd: Could not create MetaNode necessary for GemId Counter.", var2);
    }
    if(createMetaNode) {
      try (Statement stmt2 = connection.createStatement()) {
        stmt2.executeUpdate("CREATE (n:METANODE {gemIdCounter: 0})");
      } catch (SQLException var2) {
        Log.error("JDBC DB Connection Issue: " + var2.getMessage(), var2);
      }
    }
  }


  public static void init() {
    DatabaseAccessManager4Neo4j impl = new DatabaseAccessManager4Neo4j();
    DatabaseAccessManager.setInstance(impl);
    impl._setup();
  }

  public static void initBoltLocalhost(int port, String user, String password) {
    DatabaseAccessManager4Neo4j impl = new DatabaseAccessManager4Neo4j();
    DatabaseAccessManager.setInstance(impl);
    impl._setupBoltLocalhost(port, user, password);
  }

  protected void _setupBoltLocalhost(int port, String user, String password) {
    try {
      Class<?> c = Class.forName("org.neo4j.jdbc.bolt.BoltDriver");
      DriverManager.registerDriver((Driver) c.getDeclaredConstructor().newInstance());

      connection = DriverManager.getConnection("jdbc:neo4j:bolt://localhost:"+port+"/?flatten=1", user, password);
      Log.warn("New DB Connection created");
    } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException |
        InvocationTargetException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
    createMetaNodeSingleton();
  }

}
