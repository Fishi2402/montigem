package util;

import de.monticore.ocl.ocl._ast.ASTOCLCompilationUnit;
import de.se_rwth.commons.logging.Log;
import mydomain.MydomainDAOFactory;
import mydomain.MydomainManager;
import mydomain.MydomainManagerImpl;
import mydomain.MydomainTestContext;
import mydomain.test_infrastructure.MydomainDBTestSetup;
import ocl2cypher.OCL2Cypher;
import org.junit.jupiter.api.BeforeAll;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import umlp.backendrte.common.DatabaseAccessManager4Neo4j;
import umlp.backendrte.common.GemIdentifiable;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractOCL2CypherTest<T extends GemIdentifiable> extends MydomainDBTestSetup implements IOCL2XTest<T> {
  public AbstractOCL2CypherTest() {
    // No clients needed at the moment
    super(0);
  }

  @BeforeAll
  public static void startSharedDB() throws Exception {
    SharedNeo4jDatabase.start();
  }

  @Override
  public void _init() throws Exception {
    MydomainTestContext.clear();
    DatabaseAccessManager4Neo4j.initBoltLocalhost(SharedNeo4jDatabase.getPort(), "neo4j", "");
    MydomainManagerImpl.initNeo4J();
    super._init();

    var be = getBackend().getManager();
    MydomainDAOFactory.addAccessListener(() -> {
      MydomainManager inst = MydomainManager.getSingleton();
      if (inst != be) {
        Log.error("DAO instances may only be accessed when the backend is active. " +
            "Please activate the backend before accessing the DAOFactory. " +
            "Current manager: " + inst);
      }
    });

    languageSetup();
  }

  @Override
  public void _cleanup() throws Exception {
    DatabaseAccessManager4Neo4j.executeWrite("MATCH(n) DETACH DELETE n");
    DatabaseAccessManager4Neo4j.closeConnection();
    DatabaseAccessManager4Neo4j.clearLocalGemIds();

    super._cleanup();
    languageCleanup();
  }

  @Override
  public List<Long> _translateAndExecQuery(ASTOCLCompilationUnit ocl) {
    List<Long> result = new ArrayList<>();

    String query = OCL2Cypher.translate(ocl);
    try (Session session = DatabaseAccessManager4Neo4j.getSession()) {
      org.neo4j.driver.Result res = session.run(query);
      while (res.hasNext()) {
        Record next = res.next();
        result.add(next.get("gemId").asLong());
      }
    }

    return result;
  }

  @Override
  public List<Long> translateAndExecQueryFromPath(String path) throws SQLException, IOException {
    return translateAndExecQuery(getOclFromFile(getResourceFolder(), path));
  }
}
