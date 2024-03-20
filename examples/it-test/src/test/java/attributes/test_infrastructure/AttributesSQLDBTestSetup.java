/* (c) https://github.com/MontiCore/monticore */
package attributes.test_infrastructure;

import attributes.AttributesManager;
import attributes.AttributesManagerClientImpl;
import attributes.AttributesManagerImpl;
import attributes.AttributesTestContext;
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

public class AttributesSQLDBTestSetup extends attributes.test_infrastructure.AttributesSQLDBTestSetupTOP {

  protected String dbType = "SQL";

  public final static int port = 7690;

  protected static DatabaseManagementService managementService;

  public AttributesSQLDBTestSetup(int numberOfClients, String dbType) {
    super(numberOfClients);
    this.dbType = dbType;
  }

  public AttributesSQLDBTestSetup(int numberOfClients) {
    super(numberOfClients);
    this.dbType = "SQL";
  }

  @Override
  public void _init() throws Exception {
    if (dbType.equals("SQL")) {
      super._init();
    } else {
      AttributesTestContext.clear();
      AttributesManagerImpl.init();

      DatabaseAccessManager4Neo4j.initBoltLocalhost(port, "neo4j", "");
      AttributesManagerImpl.initNeo4J();

      backend = AttributesTestContext.fromCurrentContext();
      for (int i = 0; i < getNumberOfClients(); i++) {
        AttributesTestContext.clear();
        AttributesManager.initMe(new AttributesManagerClientImpl());
        CommandDirectCommunication communication = new CommandDirectCommunication(backend);
        TestClientCommandManager clientCommandManager = new TestClientCommandManager(communication);
        CommandManager.initMe(clientCommandManager);
        var client = AttributesTestContext.fromCurrentContext();
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
    managementService = new DatabaseManagementServiceBuilder(Paths.get("build", "attributesDbTest"))
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


