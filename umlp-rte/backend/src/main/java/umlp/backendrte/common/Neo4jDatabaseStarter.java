package umlp.backendrte.common;

import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.harness.Neo4jBuilders;
import org.neo4j.harness.Neo4j;


import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

/**
 * Can be used to start a Neo4j instance from this program.
 * Should only be used for short-lived instances, like tests.
 * Permanent deployments should probably use a neo4j container
 */
public class Neo4jDatabaseStarter {
  protected static int port = -1;

  protected Neo4j embeddedDatabaseServer = null;

  public Neo4j getEmbeddedDatabaseServer() {
    return this.embeddedDatabaseServer;
  }

  public int start(Function<Integer, Path> portToDBBasePath) throws IOException {
    if(this.embeddedDatabaseServer == null) {
      port = getFreePort();
      this.embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
          .withDisabledServer()
          .withWorkingDir(portToDBBasePath.apply(port))
          .withConfig(BoltConnector.listen_address, new org.neo4j.configuration.helpers.SocketAddress("localhost", port))
          .withConfig(GraphDatabaseSettings.udc_enabled, false)
          .withConfig(GraphDatabaseSettings.procedure_allowlist, List.of("apoc.*"))
          .withConfig(GraphDatabaseSettings.procedure_unrestricted, List.of("apoc.*"))
          .withConfig(GraphDatabaseSettings.log_queries, GraphDatabaseSettings.LogQueryLevel.INFO)
          // You can add custom procedures using .withProcedure()
          // .withProcedure(SampleProcedure.class)
          .build();
      embeddedDatabaseServer.printLogs(System.out);



/*
      Map<String, String> config = new HashMap<>();
      config.put("dbms.security.auth_enabled", "false");
      config.put("server.memory.pagecache.size", String.valueOf(ByteUnit.mebiBytes(8)));

      config.put("server.http.enabled", "false");
      config.put("server.http.listen_address", "localhost:"+getFreePort());
      config.put("server.https.enabled", "false");
      config.put("server.https.listen_address", "localhost:"+getFreePort());
      config.put("dbms.routing.enabled", "false");
      config.put("server.bolt.enabled", "true");
      config.put("server.bolt.listen_address","localhost:"+port);
      config.put("server.bolt.tls_level", "DISABLED");
      config.put("dbms.security.procedures.allowlist", "apoc.*");
      config.put("dbms.security.procedures.unrestricted", "apoc.*");

      Neo4jLoggerContext loggerContext =
          LogConfig.createBuilderToOutputStream(System.out, Level.INFO).build();
      var userLogProvider = new Log4jLogProvider(loggerContext);
      GraphDatabaseDependencies dependencies =
          GraphDatabaseDependencies.newDependencies().userLogProvider(userLogProvider);
      //dependencies = dependencies.extensions(buildExtensionList(dependencies));

      Config c = Config.newBuilder()
          .set(BoltConnector.enabled, true)
          .set(BoltConnector.listen_address, new org.neo4j.configuration.helpers.SocketAddress("localhost", port))
          .build();
      var managementService = new DatabaseManagementServiceFactory(COMMUNITY, CommunityEditionModule::new)
          .build(c,false,dependencies);

      ((GraphDatabaseAPI) managementService.database(c.get(GraphDatabaseSettings.initial_default_database)))
          .getDependencyResolver()
          .resolveDependency(ConnectorPortRegister.class);
*/

/*      InProcessNeo4j controls = new InProcessNeo4j(
          serverFolder, userLogFile, internalLogFile, managementService, dbConfig, userLogProvider);
      controls.start();*/

      //CommunityBootstrapper server  = new CommunityBootstrapper();

      // Start full Neo4j server stack (Bolt included), IN-PROCESS
      //int exit = server.start(databaseDir, config);

      System.out.println("Server is running on port: "+port);

    }
    return port;
  }

  public void stop() {
    if(embeddedDatabaseServer != null) {
      this.embeddedDatabaseServer.close();
      this.embeddedDatabaseServer = null;
    }
  }

  public int getPort(){
    return port;
  }

  private int getFreePort() throws IOException {
    ServerSocket socket = new ServerSocket(0);
    int port = socket.getLocalPort();
    socket.close();
    return port;
  }

  /**
   * Sometimes needed for configuring a Neo4j Test Instance
   */
/*  public static DatabaseManagementService getManagementService() {
    return managementService;
  }*/
}
