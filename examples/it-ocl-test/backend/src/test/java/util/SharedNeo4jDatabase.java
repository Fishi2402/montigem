package util;

import umlp.backendrte.common.Neo4jDatabaseStarter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SharedNeo4jDatabase {
  private static final Neo4jDatabaseStarter dbStarter = new Neo4jDatabaseStarter();
  private static boolean started = false;

  public static synchronized void start() throws IOException {
    if (!started) {
      dbStarter.start(SharedNeo4jDatabase::getDBPath);
      started = true;
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        System.out.println("Stopping shared Neo4j database...");
        dbStarter.stop();
      }));
    }
  }

  public static int getPort() {
    return dbStarter.getPort();
  }

  private static Path getDBPath(int port) {
    return Paths.get("build", "neo4j", "shared-" + port);
  }
}
