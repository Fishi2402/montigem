/* (c) https://github.com/MontiCore/monticore */
package associations.test_infrastructure;

import associations.AssociationsManager;
import associations.AssociationsManagerClientImpl;
import associations.AssociationsManagerImpl;
import associations.AssociationsTestContext;
import org.junit.jupiter.api.BeforeAll;
import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import umlp.backendrte.command.CommandDirectCommunication;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.TestClientCommandManager;
import umlp.backendrte.common.DatabaseAccessManager4Neo4j;

import java.nio.file.Paths;

public class AssociationsDBTestSetup extends associations.test_infrastructure.AssociationsDBTestSetupTOP {

  protected String dbType = "SQL";

  public final static int port = 7687;

  protected static DatabaseManagementService managementService;

  public AssociationsDBTestSetup(int numberOfClients, String dbType) {
    super(numberOfClients);
    this.dbType = dbType;
  }

  public AssociationsDBTestSetup(int numberOfClients) {
    super(numberOfClients);
    this.dbType = "SQL";
  }

  @Override
  public void _init() throws Exception {
    if (dbType.equals("SQL")) {
      super._init();
    } else {
      AssociationsTestContext.clear();
      AssociationsManagerImpl.init();

      DatabaseAccessManager4Neo4j.initBoltLocalhost(port, "neo4j", "");
      AssociationsManagerImpl.initNeo4J();

      backend = AssociationsTestContext.fromCurrentContext();
      for (int i = 0; i < getNumberOfClients(); i++) {
        AssociationsTestContext.clear();
        AssociationsManager.initMe(new AssociationsManagerClientImpl());
        CommandDirectCommunication communication = new CommandDirectCommunication(backend);
        TestClientCommandManager clientCommandManager = new TestClientCommandManager(communication);
        CommandManager.initMe(clientCommandManager);
        var client = AssociationsTestContext.fromCurrentContext();
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
    managementService = new DatabaseManagementServiceBuilder(Paths.get("build", "temporaryNeo4jDB"))
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


