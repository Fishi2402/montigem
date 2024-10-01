/* (c) https://github.com/MontiCore/monticore */
package types.test_infrastructure;

import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import types.TypesManager;
import types.TypesManagerClientImpl;
import types.TypesManagerImpl;
import types.TypesTestContext;
import umlp.backendrte.command.CommandDirectCommunication;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.TestClientCommandManager;
import umlp.backendrte.common.DatabaseAccessManager4Neo4j;
import umlp.test_rte.ClientSetup;

import java.nio.file.Paths;

public abstract class TypesSQLDBTestSetup extends TypesSQLDBTestSetupTOP {

  protected String dbType = "SQL";

  public final static int port = 7691;

  protected static DatabaseManagementService managementService;

  public TypesSQLDBTestSetup(int numberOfClients, String dbType) {
    super(numberOfClients);
    this.dbType = dbType;
  }

  public TypesSQLDBTestSetup(int numberOfClients) {
    super(numberOfClients);
    this.dbType = "SQL";
  }

  public TypesSQLDBTestSetup(ClientSetup clientSetup) {
    super(clientSetup);
  }

  @Override
  public void _init() throws Exception {
    if (dbType.equals("SQL")) {
      super._init();
    } else {
      TypesTestContext.clear();
      TypesManagerImpl.init();

      DatabaseAccessManager4Neo4j.initBoltLocalhost(port, "neo4j", "");
      TypesManagerImpl.initNeo4J();

      backend = TypesTestContext.fromCurrentContext();
      for (int i = 0; i < getNumberOfClients(); i++) {
        TypesTestContext.clear();
        TypesManager.initMe(new TypesManagerClientImpl());
        CommandDirectCommunication communication = new CommandDirectCommunication(backend);
        TestClientCommandManager clientCommandManager = new TestClientCommandManager(communication);
        CommandManager.initMe(clientCommandManager);
        var client = TypesTestContext.fromCurrentContext();
        communication.setClientContext(client);
        clients.add(client);
      }
      backend.activate();
    }
  }

  @Override
  public void _cleanup() throws Exception {
    if (dbType == "SQL") {
      super._cleanup();
    } else {
      // After each test set the database into a blank state again
      DatabaseAccessManager4Neo4j.executeStatement("MATCH(n) DETACH DELETE n");
      DatabaseAccessManager4Neo4j.closeConnection();
      DatabaseAccessManager4Neo4j.clearLocalGemIds();
    }
  }

  public static void setUpLocalNeo4jInstance() {
    if (managementService != null) {
      managementService.database(GraphDatabaseSettings.DEFAULT_DATABASE_NAME);
      return;
    }
    managementService = new DatabaseManagementServiceBuilder(Paths.get("build", "typesDbTest"))
        .setConfig(BoltConnector.enabled, true)
        .setConfig(BoltConnector.listen_address, new SocketAddress("localhost", port)).build();
    registerShutdownHook(managementService);
    managementService.database(GraphDatabaseSettings.DEFAULT_DATABASE_NAME);

  }

  protected static void registerShutdownHook(DatabaseManagementService managementService) {
    // Registers a shutdown hook for the Neo4j instance so that it
    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
    // running application).
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        managementService.shutdown();
      }
    });
  }

}


