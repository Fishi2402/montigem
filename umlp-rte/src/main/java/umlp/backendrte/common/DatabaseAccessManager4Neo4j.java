package umlp.backendrte.common;

import de.se_rwth.commons.logging.Log;

import java.lang.reflect.InvocationTargetException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class DatabaseAccessManager4Neo4j extends DatabaseAccessManager {

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
    try {
      Optional<ResultSet> resultSet = executeQuery("MATCH(n:METANODE) return n");
      if(resultSet.isEmpty() || !resultSet.get().next()) {
        executeStatement("CREATE (n:METANODE {gemIdCounter: 0})");
      }
    } catch (SQLException exception) {
      Log.error("0xadakfjjflsd: Could not create MetaNode necessary for GemId Counter.", exception);
    }
  }


  // TODO improve performance by bulk requesting of gemIds
  @Override
  protected long _getFreeGemId() {
    try {
      ResultSet res = executeQuery("MATCH(n:METANODE) SET n.gemIdCounter= n.gemIdCounter+1 return n.gemIdCounter").get();
      if(res.next()) {
        return res.getLong(1);
      }
    } catch (SQLException e) {
      Log.error("Oxalhfgjsd: Failed to get fresh Gem Id from DB", e);
    }
    throw new RuntimeException("Failed to get fresh Gem Id from DB.");
  }

  public static void init() {
    DatabaseAccessManager4Neo4j impl = new DatabaseAccessManager4Neo4j();
    DatabaseAccessManager.setInstance(impl);
    impl._setup();
  }

  public static void initBoltLocalhost(int port, String user, String password) {
    DatabaseAccessManager4Neo4j impl = new DatabaseAccessManager4Neo4j();
    DatabaseAccessManager.setInstance(impl);
    impl._setuoBoltLocalhost(port, user, password);
  }

  protected void _setuoBoltLocalhost(int port, String user, String password) {
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
