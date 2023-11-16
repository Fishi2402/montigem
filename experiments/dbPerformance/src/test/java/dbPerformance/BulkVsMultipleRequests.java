/* (c) https://github.com/MontiCore/monticore */

package dbPerformance;

import com.google.common.base.Stopwatch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import umlp.backendrte.common.DatabaseAccessManager;
import umlp.backendrte.common.DatabaseAccessManager4SQL;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static dbPerformance.ExpConstants.NUM_TABLES;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("Slow manual experiment")
public class BulkVsMultipleRequests {
  public static final int NUM_ENTRIES_PER_TABLE = 1000;
  public static String schema;
  static {
    StringBuilder schemaBuilder = new StringBuilder();

    for(int i = 0; i < NUM_TABLES; i++){
      schemaBuilder.append("CREATE TABLE t").append(i).append("(id VARCHAR NOT NULL, PRIMARY KEY (ID));\n");
    }

    schema = schemaBuilder.toString();
  }


  @BeforeAll
  public static void setup() throws IOException {
    DatabaseAccessManager.setup();
    DatabaseAccessManager4SQL.initSchema(schema);

    for(int i = 0; i < NUM_TABLES; i++){
      for(int j = 0; j < NUM_ENTRIES_PER_TABLE; j++){
        DatabaseAccessManager.executeStatement(String.format("INSERT INTO %s (id) VALUES ('%s');", "t" + i, calcId(i, j)));
      }
    }
  }

  private static String calcId(int i, int j) {
    return "t" + i + "-" + (i * NUM_TABLES + j);
  }

  public void testDao(ExpDao dao) throws SQLException {
    Stopwatch stopwatch = Stopwatch.createStarted();
    for(int i = 0; i < NUM_TABLES; i++){
      System.out.println("Start table " + i);
      for(int j = 0; j < NUM_ENTRIES_PER_TABLE; j++){
        String id = calcId(i,j);
        assertTrue(dao.dbContainsId(id), "Did not find id " + id);
      }
    }
    System.out.println("Took " + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + "ms");
  }

  BulkRequestDao bulkRequestDao = new BulkRequestDao();
  BulkRequestPreparedStatementDao bulkRequestPreparedStatementDao = new BulkRequestPreparedStatementDao();
  MultipleRequestsDao multipleRequestsDao = new MultipleRequestsDao();
  EncodedInIdDao encodedInIdDao = new EncodedInIdDao();

  @Test
  public void testBulk() throws SQLException {
    bulkRequestDao.stopwatch.reset();
    testDao(bulkRequestDao);
    System.out.println("Creating statements took " + bulkRequestDao.stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
  }

  @Test
  public void testBulkPreparedStatement() throws SQLException {
    testDao(bulkRequestPreparedStatementDao);
  }

  @Test
  public void testMultiple() throws SQLException {
    testDao(multipleRequestsDao);
  }

  @Test
  public void testEncodedInId() throws SQLException{
    testDao(encodedInIdDao);
  }
}
