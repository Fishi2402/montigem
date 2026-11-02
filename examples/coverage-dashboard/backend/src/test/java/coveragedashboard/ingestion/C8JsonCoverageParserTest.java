package coveragedashboard.ingestion;

import coveragedashboard.CoverageModule;
import coveragedashboard.CoverageReport;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.ReportType;
import coveragedashboard.test_infrastructure.CoverageDashboardMockTestSetup;
import org.junit.jupiter.api.Test;
import umlp.test_rte.ClientSetup;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class C8JsonCoverageParserTest extends CoverageDashboardMockTestSetup {

  public C8JsonCoverageParserTest(){
    super(ClientSetup.of(1));
  }


  @Test
  void c8ParsingTest() throws Exception {
    URL resourceUrl = getClass().getResource("/ingestion/mock-c8-coverage.json");
    assertNotNull(resourceUrl, "Mock JSON file not found in resources");
    File mockFile = new File(resourceUrl.toURI());

    C8JsonCoverageParser parser = new C8JsonCoverageParser();
    Path basePath = Paths.get("/some/CI/dependet/path/");

    CoverageReport report = parser.parse(mockFile, "Test Report", basePath);

    assertNotNull(report);
    assertEquals("Test Report", report.getName());
    assertEquals(ReportType.GENERATED_CODE_TEST, report.getType());

    CoverageModule rootModule = report.getModule();
    assertNotNull(rootModule);

    CoverageModule guiModule = rootModule.getSubModules(0);
    assertEquals("gui2java2ts", guiModule.getName());

    CoverageSourceFile navbarFile = guiModule.getSourceFiles(0);
    assertEquals("Navbar.ts", navbarFile.getName());
    assertEquals(Paths.get("gui2java2ts/Navbar.ts").toString(), navbarFile.getPath());

    assertEquals(1, navbarFile.getMetrics().getCoveredInstructions());
    assertEquals(1, navbarFile.getMetrics().getMissedInstructions());
    assertEquals(1, navbarFile.getMetrics().getCoveredBranches());
    assertEquals(1, navbarFile.getMetrics().getMissedBranches());
  }
}
