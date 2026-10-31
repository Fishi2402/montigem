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
    Path basePath = Paths.get("/some/CI/dependet/path/frontend/build/app/");
    Path rootPath = Paths.get("/some/root/path/");

    CoverageReport report = parser.parse(mockFile, "Test Report", basePath, rootPath);

    assertNotNull(report);
    assertEquals("Test Report", report.getName());
    assertEquals(ReportType.GENERATED_CODE_TEST, report.getType());

    CoverageModule rootModule = report.getDirectPerspective().getRootModule();
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

  @Test
  void testNormalizeMappingsJSweetVsMontiCore() {
    de.monticore.sourcemap.DecodedMapping mapping = new de.monticore.sourcemap.DecodedMapping(
        14, 0, null, 8, 1, null
    );
    de.monticore.sourcemap.DecodedSourceMap sourceMap = new de.monticore.sourcemap.DecodedSourceMap(
        "Navbar.ts", java.util.List.of(), java.util.List.of(mapping)
    );

    // Case 1: JSweet source map (target ends in .ts AND raw mappings starts with "A;")
    String jsweetContent = "{\"version\":3,\"file\":\"Navbar.ts\",\"mappings\":\"A;;;;;;;;;;;;;;AAQC,C;\"}";
    java.util.List<de.monticore.sourcemap.DecodedMapping> jsweetResult =
        C8JsonCoverageParser.normalizeMappings(sourceMap, jsweetContent, "Navbar.ts");
    assertEquals(14, jsweetResult.get(0).generatedLine());
    assertEquals(8, jsweetResult.get(0).originalLine());

    // Case 2: MontiCore standard source map (0-based, target .java or .ftl)
    de.monticore.sourcemap.DecodedMapping mcMapping = new de.monticore.sourcemap.DecodedMapping(
        8, 0, null, 3, 0, null
    );
    de.monticore.sourcemap.DecodedSourceMap mcSourceMap = new de.monticore.sourcemap.DecodedSourceMap(
        "NavbarTOP.java", java.util.List.of(), java.util.List.of(mcMapping)
    );
    String mcContent = "{\"version\":3,\"file\":\"NavbarTOP.java\",\"mappings\":\"AAGA;AACA;AACA;\"}";
    java.util.List<de.monticore.sourcemap.DecodedMapping> mcResult =
        C8JsonCoverageParser.normalizeMappings(mcSourceMap, mcContent, "NavbarTOP.java");
    // Standard 0-based lines must be shifted by +1 to become 1-based
    assertEquals(9, mcResult.get(0).generatedLine());
    assertEquals(4, mcResult.get(0).originalLine());
  }
}
