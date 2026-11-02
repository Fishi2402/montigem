package coveragedashboard.ingestion;

import coveragedashboard.CoveragePerspective;
import coveragedashboard.CoverageReport;
import coveragedashboard.ReportType;
import coveragedashboard.test_infrastructure.CoverageDashboardMockTestSetup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import umlp.test_rte.ClientSetup;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JacocoXmlCoverageParserTest extends CoverageDashboardMockTestSetup {

  public JacocoXmlCoverageParserTest() {
    super(ClientSetup.of(1));
  }

  @Test
  void testParseJacocoXmlWithSourceMap(@TempDir Path tempDir) throws Exception {
    Path srcDir = tempDir.resolve("pkg");
    Files.createDirectories(srcDir);

    Path javaFile = srcDir.resolve("Foo.java");
    Files.writeString(javaFile, "package pkg;\npublic class Foo {}\n");

    Path templateFile = tempDir.resolve("Foo.ftl");
    Files.writeString(templateFile, "<#-- template -->\npublic class Foo {}\n");

    // Create standard MontiCore source map (0-based)
    String mapContent = String.format(
        "{\"version\":3,\"file\":\"Foo.java\",\"sources\":[\"%s\"],\"mappings\":\"AAGA;AACA;\"}",
        templateFile.toUri()
    );
    Path mapFile = srcDir.resolve("Foo.java.map");
    Files.writeString(mapFile, mapContent);

    // Create Jacoco XML
    String xmlContent = """
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <!DOCTYPE report PUBLIC "-//JACOCO//DTD Report 1.1//EN" "report.dtd">
        <report name="test">
          <package name="pkg">
            <sourcefile name="Foo.java">
              <line nr="2" mi="0" ci="5" mb="0" cb="0"/>
              <counter type="INSTRUCTION" missed="0" covered="5"/>
              <counter type="LINE" missed="0" covered="1"/>
            </sourcefile>
          </package>
        </report>
        """;
    Path xmlFile = tempDir.resolve("jacocoTestReport.xml");
    Files.writeString(xmlFile, xmlContent);

    JacocoXmlCoverageParser parser = new JacocoXmlCoverageParser(ReportType.GENERATED_CODE_TEST);
    CoverageReport report = parser.parse(xmlFile.toFile(), "Assoc Test", tempDir, tempDir);

    assertNotNull(report);
    assertEquals("Assoc Test", report.getName());
    assertEquals(ReportType.GENERATED_CODE_TEST, report.getType());
    assertEquals("JAVA", report.getDirectPerspective().getName());

    // Verify that aggregated FTL perspective was generated via source-mapping
    assertFalse(report.getAggregatedPerspectivesList().isEmpty(), "Aggregated perspectives should not be empty");
    CoveragePerspective ftlPerspective = report.getAggregatedPerspectivesList().stream()
        .filter(p -> "FTL".equals(p.getName()))
        .findFirst()
        .orElse(null);
    assertNotNull(ftlPerspective, "FTL perspective should have been created from Foo.java.map");
  }

  @Test
  void testParseActualAssocReport() throws Exception {
    Path xmlPath = Path.of("../../../examples/it-sync-protocol-test/frontend/build/reports/jacoco/test/jacocoTestReport.xml");
    Path repoRoot = Path.of("../../..");
    if (!Files.exists(xmlPath)) {
      xmlPath = Path.of("examples/it-sync-protocol-test/frontend/build/reports/jacoco/test/jacocoTestReport.xml");
      repoRoot = Path.of(".");
    }
    if (Files.exists(xmlPath)) {
      Path basePath = repoRoot.resolve("examples/it-sync-protocol-test/backend/build/umlp2java");
      JacocoXmlCoverageParser parser = new JacocoXmlCoverageParser(ReportType.GENERATED_CODE_TEST);
      CoverageReport report = parser.parse(xmlPath.toFile(), "Assoc Real Test", basePath, repoRoot);
      assertNotNull(report);
      assertNotNull(report.getDirectPerspective());
      assertFalse(report.getAggregatedPerspectivesList().isEmpty(), "Aggregated perspectives should be generated for real assoc report");
    }
  }
}
