package umlp.backendrte.common;

public class DatabaseAccessManager4Neo4j extends DatabaseAccessManager {

  private long freeGemId = 0;

  @Override
  protected void _setup() {
    // Nothing to do here since Neo4j does not need a schema, and we do not have database consistency checks for neo4j
  }

  // TODO synchronize freeGemIds with Database
  @Override
  protected long _getFreeGemId() {
    return freeGemId++;
  }

  public  static  void init () {
    DatabaseAccessManager4Neo4j impl = new DatabaseAccessManager4Neo4j();
    DatabaseAccessManager.setInstance(impl);
  }
}
