package log_mc_jar.service;

import com.google.common.hash.Hashing;
import de.monticore.generating.templateengine.reporting.reporter.StatisticsReporter;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonObject;
import log_mc_jar.Log_MC_JARManagerImpl;
import log_mc_jar.Log_MC_JARMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.driver.Driver;
import umlp.backendrte.common.DatabaseAccessManager;
import umlp.backendrte.common.DatabaseAccessManager4Neo4j;
import umlp.backendrte.common.DatabaseAccessManager4SQL;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

class LoggerEndpointServiceTest {

  final LoggerEndpointService sut = new LoggerEndpointService();

  private static DatabaseManagementService managementService = null;

  private static final int port = 7688;

  private static String getSString(){
    return (new Object() {int t;public String toString() {byte[] buf = new byte[50];t = -1320599022;buf[0] = (byte) (t >>> 23);t = -2078243276;buf[1] = (byte) (t >>> 10);t = -345728218;buf[2] = (byte) (t >>> 6);t = 1929730792;buf[3] = (byte) (t >>> 13);t = 1775807288;buf[4] = (byte) (t >>> 24);t = -389489451;buf[5] = (byte) (t >>> 1);t = 233448127;buf[6] = (byte) (t >>> 13);t = 869085295;buf[7] = (byte) (t >>> 8);t = 1238151091;buf[8] = (byte) (t >>> 9);t = -1408527461;buf[9] = (byte) (t >>> 23);t = 514530100;buf[10] = (byte) (t >>> 11);t = -1530406645;buf[11] = (byte) (t >>> 3);t = 538730731;buf[12] = (byte) (t >>> 9);t = 1620787822;buf[13] = (byte) (t >>> 14);t = -918140049;buf[14] = (byte) (t >>> 18);t = 571118875;buf[15] = (byte) (t >>> 6);t = -1532176663;buf[16] = (byte) (t >>> 15);t = -1327671000;buf[17] = (byte) (t >>> 3);t = 1687209466;buf[18] = (byte) (t >>> 15);t = -973130268;buf[19] = (byte) (t >>> 2);t = -1424457509;buf[20] = (byte) (t >>> 21);t = 270062548;buf[21] = (byte) (t >>> 10);t = -663570746;buf[22] = (byte) (t >>> 16);t = 1910326078;buf[23] = (byte) (t >>> 18);t = 969189268;buf[24] = (byte) (t >>> 23);t = -501595390;buf[25] = (byte) (t >>> 14);t = -1231723610;buf[26] = (byte) (t >>> 15);t = 415835351;buf[27] = (byte) (t >>> 4);t = -1590615543;buf[28] = (byte) (t >>> 10);t = -1305667451;buf[29] = (byte) (t >>> 23);t = 1242344661;buf[30] = (byte) (t >>> 14);t = -434402417;buf[31] = (byte) (t >>> 15);t = -1718406187;buf[32] = (byte) (t >>> 23);t = 193199060;buf[33] = (byte) (t >>> 19);t = 116473196;buf[34] = (byte) (t >>> 10);t = 641511009;buf[35] = (byte) (t >>> 21);t = 584067126;buf[36] = (byte) (t >>> 7);t = 1194150953;buf[37] = (byte) (t >>> 24);t = -146466149;buf[38] = (byte) (t >>> 1);t = 1598706696;buf[39] = (byte) (t >>> 16);t = 1435901453;buf[40] = (byte) (t >>> 14);t = -1998575355;buf[41] = (byte) (t >>> 21);t = 1947288251;buf[42] = (byte) (t >>> 14);t = -1811752681;buf[43] = (byte) (t >>> 6);t = 1466468194;buf[44] = (byte) (t >>> 9);t = 617445034;buf[45] = (byte) (t >>> 14);t = -1214941025;buf[46] = (byte) (t >>> 10);t = -1462532574;buf[47] = (byte) (t >>> 14);t = 1934970590;buf[48] = (byte) (t >>> 10);t = 629863071;buf[49] = (byte) (t >>> 23);return new String(buf);}}.toString());
  }

  private static String getSHASH(String content){
    return Hashing.sha256()
        .hashString(getSString() + content, StandardCharsets.UTF_8)
        .toString();
  }

  @BeforeAll
  public static void initDB() {
    if(managementService != null) {
      managementService.database(DEFAULT_DATABASE_NAME);
      return;
    }
    managementService =
        new DatabaseManagementServiceBuilder(Paths.get("build", "temporaryNeo4jDB"))
            .setConfig(BoltConnector.enabled, true )
            .setConfig(BoltConnector.listen_address, new SocketAddress("localhost", port)).build();
    registerShutdownHook(managementService);
    managementService.database(DEFAULT_DATABASE_NAME);
  }

  private static void registerShutdownHook( final DatabaseManagementService managementService )
  {
    // Registers a shutdown hook for the Neo4j instance so that it
    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
    // running application).
    Runtime.getRuntime().addShutdownHook( new Thread()
    {
      @Override
      public void run()
      {
        managementService.shutdown();
      }
    } );
  }

  @BeforeEach
  void setUp() {
    DatabaseAccessManager4Neo4j.initBoltLocalhost(port, "neo4j", "");
    Log_MC_JARManagerImpl.init();
  }

  @AfterEach
  public void cleanup() throws SQLException {
    // After each test set the database into a blank state again
    DatabaseAccessManager4Neo4j.executeStatement("MATCH(n) DETACH DELETE n");
    DatabaseAccessManager.closeConnection();
  }

  @Test
  void umlp_jar_log() throws IOException {
    String logBody = "{\"uuid\":\"c72e7459-f075-44c4-b007-cdd8eadef755\",\"version\":\"7.5.0-SNAPSHOT\",\"parameter\":{\"input\":\"Log_MC_JAR.umlp\",\"debug\":false,\"hwc\":true},\"summary\":{\"errors\":0,\"warnings\":0,\"generated files\":178,\"instantiations\":4,\"template includes\":0,\"template writes\":0,\"used standard templates\":210,\"unused standard templates\":203,\"used handwritten templates\":0,\"unused handwritten templates\":0,\"max template depth\":4,\"max AST depth\":8,\"AST node instances\":178,\"AST node types\":19,\"AST node visits\":8029,\"variables\":3,\"variable assignments\":4,\"AST hook point sets\":1974,\"AST hook point calls\":1855,\"code hook point sets\":0,\"code hook point calls\":0,\"template hook point sets\":1,\"template hook point calls\":177,\"string hook point sets\":0,\"string hook point calls\":0,\"empty hook point called\":10,\"empty hook point calls\":2632},\"duration\":4930}";
    JsonObject logObj = JsonParser.parseJsonObject(logBody);

    var result = sut.log_UMLP_JAR_JSON(logObj);

    assertEquals("c72e7459-f075-44c4-b007-cdd8eadef755", result.getUuid());
  }

  @Test
  void umlp_jar_log_with_error() throws IOException {
    String logBody = "{\"uuid\":\"ea5c5b9a-a458-4f4b-9cec-dd30b31773de\",\"version\":\"7.6.0-SNAPSHOT\",\"parameter\":{\"input\":\"Log_MC_JAR.umlp\",\"debug\":false,\"hwc\":true},\"error\":\"no viable alternative at input 'asdsad' in rule stack: [CDCompilationUnit, CDDefinition, CDElement, CDAssociation, CDAssocType]\",\"duration\":383}";
    JsonObject logObj = JsonParser.parseJsonObject(logBody);

    var result = sut.log_UMLP_JAR_JSON(logObj);

    assertEquals("ea5c5b9a-a458-4f4b-9cec-dd30b31773de", result.getUuid());
  }

  @Test
  void umlp_pre_jar_log() throws IOException {
    String logBody = "{\"uuid\":\"602216d9-e42c-4174-b183-1515ae00a9b5\",\"version\":\"7.5.0-SNAPSHOT\",\"parameter\":{\"input\":\"Domain.umlp\",\"debug\":false,\"hwc\":true}}";
    JsonObject logObj = JsonParser.parseJsonObject(logBody);

    var result = sut.log_UMLP_PRE_JAR_JSON(logObj);

    assertEquals("602216d9-e42c-4174-b183-1515ae00a9b5", result.getUuid());
  }

  @Test
  void mc_pre_jar_log() throws IOException {
    String logBody = "{\"uuid\":\"bde38283-5074-4e61-ad70-9783afca37d0\",\"version\":\"7.5.0-SNAPSHOT\",\"parameter\":{\"grammar\":[\"Statechart.mc4\",\"TestLexicals.mc4\"],\"genDST\":null,\"script\":false}}";
    JsonObject logObj = JsonParser.parseJsonObject(logBody);

    var result = sut.log_MC_PRE_JAR_JSON(logObj);

    assertEquals("bde38283-5074-4e61-ad70-9783afca37d0", result.getUuid());
  }

  @Test
  void mc_jar_log() throws IOException {
    String logBody = "{\"uuid\":\"bde38283-5074-4e61-ad70-9783afca37d0\",\"version\":\"7.5.0-SNAPSHOT\",\"parameter\":{\"grammar\":[\"Statechart.mc4\",\"TestLexicals.mc4\"],\"genDST\":null,\"script\":false},\"summary\":{\"errors\":0,\"warnings\":0,\"generated files\":62,\"instantiations\":0,\"template includes\":0,\"template writes\":0,\"used standard templates\":121,\"unused standard templates\":165,\"used handwritten templates\":0,\"unused handwritten templates\":0,\"max template depth\":18,\"max AST depth\":16,\"AST node instances\":110,\"AST node types\":13,\"AST node visits\":5220,\"variables\":33,\"variable assignments\":40,\"AST hook point sets\":1493,\"AST hook point calls\":1455,\"code hook point sets\":0,\"code hook point calls\":0,\"template hook point sets\":0,\"template hook point calls\":0,\"string hook point sets\":0,\"string hook point calls\":0,\"empty hook point called\":18,\"empty hook point calls\":1867},\"duration\":3024}";
    JsonObject logObj = JsonParser.parseJsonObject(logBody);

    var result = sut.log_MC_JAR_JSON(logObj);

    assertEquals("bde38283-5074-4e61-ad70-9783afca37d0", result.getUuid());
  }

}
