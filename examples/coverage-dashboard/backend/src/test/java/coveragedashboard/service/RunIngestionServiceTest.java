package coveragedashboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import coveragedashboard.ReportType;
import coveragedashboard.service.dto.RunIngestionResponse;
import coveragedashboard.test_infrastructure.CoverageDashboardMockTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import umlp.test_rte.ClientSetup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RunIngestionServiceTest extends CoverageDashboardMockTestSetup {

  @TempDir
  Path storageDir;

  private RunIngestionService runIngestionService;

  public RunIngestionServiceTest() {
    super(ClientSetup.of(1));
  }

  @BeforeEach
  void setUpService() {
    IngestionProperties properties = new IngestionProperties();
    properties.setStorageDir(storageDir);
    ArtifactStorageService storageService = new ArtifactStorageService(properties);
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    runIngestionService = new RunIngestionService(storageService, objectMapper);
  }

  @Test
  void ingestsZipWithJacocoReportAndPersistsFiles() throws IOException {
    String manifest = """
        {
          "commitHash": "f0879ff463c32226eea6b39e41860f79b70668d8",
          "branchName": "ma-mfl/Generator_coverage",
          "timestamp": "2026-11-25T17:00:00",
          "testGroups": [
            {
              "name": "umlp-jsweet",
              "reports": [
                {
                  "name": "Generator Unit Test - JAVA",
                  "type": "GENERATOR_UNIT_TEST",
                  "reportPath": "reports/jacoco.xml",
                  "basePath": "src/main/java"
                }
              ]
            }
          ]
        }
        """;

    String jacocoXml = """
        <?xml version="1.0" encoding="UTF-8"?>
        <report name="umlp-jsweet">
          <package name="umlp/jsweet">
            <sourcefile name="Demo.java">
              <line nr="1" mi="0" ci="3" mb="0" cb="0"/>
              <counter type="INSTRUCTION" missed="0" covered="3"/>
              <counter type="BRANCH" missed="0" covered="0"/>
              <counter type="LINE" missed="0" covered="1"/>
            </sourcefile>
          </package>
        </report>
        """;

    byte[] zip = createZip(Map.of(
        "manifest.json", manifest,
        "reports/jacoco.xml", jacocoXml,
        "src/main/java/umlp/jsweet/Demo.java", "package umlp.jsweet; class Demo {}"
    ));

    MockMultipartFile file = new MockMultipartFile("artifact", "bundle.zip", "application/zip", zip);

    RunIngestionResponse response = runIngestionService.ingestArchive(file);

    assertEquals("f0879ff463c32226eea6b39e41860f79b70668d8", response.commitHash());
    assertEquals("ma-mfl/Generator_coverage", response.branchName());
    assertEquals(1.0, response.cci(), 1e-9);
    assertEquals(1, response.testGroups().size());
    assertEquals("umlp-jsweet", response.testGroups().get(0).name());
    assertEquals(1.0, response.testGroups().get(0).cci(), 1e-9);
    assertEquals(1, response.testGroups().get(0).reports().size());
    assertEquals(ReportType.GENERATOR_UNIT_TEST, response.testGroups().get(0).reports().get(0).type());
    assertEquals(1.0, response.testGroups().get(0).reports().get(0).coverageValue(), 1e-9);

    Optional<RunIngestionResponse> fetched = runIngestionService.getRunSummary(response.runId());
    assertTrue(fetched.isPresent());
    assertEquals(response.runId(), fetched.get().runId());

    try (var dirs = Files.list(storageDir)) {
      var persistedDirs = dirs.toList();
      assertEquals(1, persistedDirs.size(), "Extracted run directory should persist for GetFileContent");
      assertTrue(
          persistedDirs.get(0).getFileName().toString().endsWith("_f0879ff463c32226eea6b39e41860f79b70668d8"),
          "Persisted directory should use commitHash instead of UUID: " + persistedDirs.get(0).getFileName()
      );
    }
  }

  @Test
  void ingestsZipWithRewrittenRelativeTemplateSourceMap() throws IOException {
    String manifest = """
        {
          "commitHash": "deadbeef1234",
          "branchName": "dev",
          "timestamp": "2026-11-25T18:30:00",
          "testGroups": [
            {
              "name": "assoc-test",
              "reports": [
                {
                  "name": "Assoc Test - JAVA",
                  "type": "GENERATED_CODE_TEST",
                  "reportPath": "reports/jacoco.xml",
                  "basePath": "src/main/java"
                }
              ]
            }
          ]
        }
        """;

    String jacocoXml = """
        <?xml version="1.0" encoding="UTF-8"?>
        <report name="assoc-test">
          <package name="pkg">
            <sourcefile name="Foo.java">
              <line nr="2" mi="0" ci="5" mb="0" cb="0"/>
              <counter type="INSTRUCTION" missed="0" covered="5"/>
              <counter type="LINE" missed="0" covered="1"/>
            </sourcefile>
          </package>
        </report>
        """;

    // Simulates a .map rewritten by packageCoverageBundle (relative path to templates/<jar>/<ftl>, no jar: URI)
    String rewrittenMap = """
        {"version":3,"file":"Foo.java","sources":["../../../../templates/umlp-tool/umlp/Foo.ftl"],"mappings":"AAGA;AACA;"}
        """;

    byte[] zip = createZip(Map.of(
        "manifest.json", manifest,
        "reports/jacoco.xml", jacocoXml,
        "src/main/java/pkg/Foo.java", "package pkg;\npublic class Foo {}\n",
        "src/main/java/pkg/Foo.java.map", rewrittenMap,
        "templates/umlp-tool/umlp/Foo.ftl", "<#-- template -->\npublic class Foo {}\n"
    ));

    MockMultipartFile file = new MockMultipartFile("artifact", "bundle.zip", "application/zip", zip);
    RunIngestionResponse response = runIngestionService.ingestArchive(file);

    assertEquals("deadbeef1234", response.commitHash());
    var run = coveragedashboard.CoverageDashboardManager.runOpt(response.runId()).orElseThrow();
    var report = run.getTestGroupsList().get(0).getReportsList().get(0);
    var ftlPerspective = report.getAggregatedPerspectivesList().stream()
        .filter(p -> "FTL".equals(p.getName()))
        .findFirst()
        .orElseThrow();
    assertTrue(!ftlPerspective.getRootModule().getSubModulesList().isEmpty()
        || !ftlPerspective.getRootModule().getSourceFilesList().isEmpty());
  }

  @Test
  void rejectsMissingManifestAndCleansUpStorageDir() throws IOException {
    byte[] zip = createZip(Map.of("reports/jacoco.xml", "<report/>"));
    MockMultipartFile file = new MockMultipartFile("artifact", "no-manifest.zip", "application/zip", zip);

    assertThrows(InvalidIngestionRequestException.class, () -> runIngestionService.ingestArchive(file));

    try (var dirs = Files.list(storageDir)) {
      assertEquals(0, dirs.count(), "Failed ingestion should clean up extracted files");
    }
  }

  @Test
  void rejectsMissingReferencedReportAndCleansUpStorageDir() throws IOException {
    String manifest = """
        {
          "commitHash": "abc123",
          "branchName": "main",
          "testGroups": [
            {
              "name": "grp",
              "reports": [
                {
                  "name": "Missing",
                  "type": "GENERATOR_UNIT_TEST",
                  "reportPath": "does/not/exist.xml",
                  "basePath": "src/main/java"
                }
              ]
            }
          ]
        }
        """;

    byte[] zip = createZip(Map.of("manifest.json", manifest));
    MockMultipartFile file = new MockMultipartFile("artifact", "missing-report.zip", "application/zip", zip);

    assertThrows(InvalidIngestionRequestException.class, () -> runIngestionService.ingestArchive(file));

    try (var dirs = Files.list(storageDir)) {
      assertEquals(0, dirs.count(), "Failed ingestion should clean up extracted files");
    }
  }

  @Test
  void rejectsNonPositiveWeightInManifestAndCleansUp() throws IOException {
    String manifest = """
        {
          "commitHash": "abc123",
          "branchName": "main",
          "testGroups": [
            {
              "name": "grp",
              "reports": [
                {
                  "name": "Bad Weight",
                  "type": "GENERATOR_UNIT_TEST",
                  "reportPath": "reports/jacoco.xml",
                  "basePath": "src/main/java",
                  "weight": 0
                }
              ]
            }
          ]
        }
        """;

    byte[] zip = createZip(Map.of(
        "manifest.json", manifest,
        "reports/jacoco.xml", "<report name=\"x\"/>"
    ));
    MockMultipartFile file = new MockMultipartFile("artifact", "bad-weight.zip", "application/zip", zip);

    assertThrows(InvalidIngestionRequestException.class, () -> runIngestionService.ingestArchive(file));

    try (var dirs = Files.list(storageDir)) {
      assertEquals(0, dirs.count(), "Invalid weight should clean up extracted files");
    }
  }

  @Test
  void ingestsExplicitMergedReportAndReplacesSourceReportsInRunCci() throws IOException {
    String manifest = """
        {
          "commitHash": "cafe1234",
          "branchName": "dev",
          "testGroups": [
            {
              "name": "suite-a",
              "reports": [
                {
                  "name": "Suite A",
                  "type": "GENERATED_CODE_TEST",
                  "reportPath": "reports/a.xml",
                  "basePath": "srcA"
                }
              ]
            },
            {
              "name": "suite-b",
              "reports": [
                {
                  "name": "Suite B",
                  "type": "GENERATED_CODE_TEST",
                  "reportPath": "reports/b.xml",
                  "basePath": "srcB"
                }
              ]
            }
          ],
          "mergedReports": [
            {
              "name": "Combined E2E FTL",
              "sourceReports": ["Suite A", "Suite B"],
              "perspectives": ["FTL"]
            }
          ]
        }
        """;

    // Suite A covers line 1 of Shared.ftl (via A.java line 1) and misses line 2 (via A.java line 2) -> FTL = 0.5
    String xmlA = """
        <?xml version="1.0" encoding="UTF-8"?>
        <report name="a">
          <package name="pkg">
            <sourcefile name="A.java">
              <line nr="1" mi="0" ci="5" mb="0" cb="0"/>
              <line nr="2" mi="5" ci="0" mb="0" cb="0"/>
              <counter type="INSTRUCTION" missed="5" covered="5"/>
              <counter type="LINE" missed="1" covered="1"/>
            </sourcefile>
          </package>
        </report>
        """;
    // Suite B misses line 1 of Shared.ftl and covers line 2 -> FTL = 0.5
    String xmlB = """
        <?xml version="1.0" encoding="UTF-8"?>
        <report name="b">
          <package name="pkg">
            <sourcefile name="B.java">
              <line nr="1" mi="5" ci="0" mb="0" cb="0"/>
              <line nr="2" mi="0" ci="5" mb="0" cb="0"/>
              <counter type="INSTRUCTION" missed="5" covered="5"/>
              <counter type="LINE" missed="1" covered="1"/>
            </sourcefile>
          </package>
        </report>
        """;

    // Both A.java.map and B.java.map map line 1->1 and line 2->2 of templates/Shared.ftl (0-based -> +1)
    String mapA = "{\"version\":3,\"file\":\"A.java\",\"sources\":[\"../../templates/Shared.ftl\"],\"mappings\":\"AAAA;AACA;\"}";
    String mapB = "{\"version\":3,\"file\":\"B.java\",\"sources\":[\"../../templates/Shared.ftl\"],\"mappings\":\"AAAA;AACA;\"}";

    byte[] zip = createZip(Map.of(
        "manifest.json", manifest,
        "reports/a.xml", xmlA,
        "reports/b.xml", xmlB,
        "srcA/pkg/A.java", "line1\nline2\n",
        "srcA/pkg/A.java.map", mapA,
        "srcB/pkg/B.java", "line1\nline2\n",
        "srcB/pkg/B.java.map", mapB,
        "templates/Shared.ftl", "tpl1\ntpl2\n"
    ));

    MockMultipartFile file = new MockMultipartFile("artifact", "merged.zip", "application/zip", zip);
    RunIngestionResponse response = runIngestionService.ingestArchive(file);

    // Individual reports each have 0.5 coverage, but the merged FTL report has 1.0 and replaces both in Run.cci
    assertEquals(1, response.mergedReports().size());
    assertEquals("Combined E2E FTL", response.mergedReports().get(0).name());
    assertEquals(1.0, response.mergedReports().get(0).coverageValue(), 1e-9);
    assertEquals(1.0, response.cci(), 1e-9);
    assertEquals(0.5, response.testGroups().get(0).cci(), 1e-9);
    assertEquals(0.5, response.testGroups().get(1).cci(), 1e-9);
  }

  @Test
  void rejectsMergedReportWithoutExplicitPerspectives() throws IOException {
    String manifest = """
        {
          "commitHash": "abc123",
          "branchName": "main",
          "testGroups": [
            {
              "name": "g",
              "reports": [
                { "name": "R1", "type": "GENERATED_CODE_TEST", "reportPath": "r1.xml", "basePath": "src" },
                { "name": "R2", "type": "GENERATED_CODE_TEST", "reportPath": "r2.xml", "basePath": "src" }
              ]
            }
          ],
          "mergedReports": [
            {
              "name": "Invalid Merge",
              "sourceReports": ["R1", "R2"]
            }
          ]
        }
        """;

    byte[] zip = createZip(Map.of(
        "manifest.json", manifest,
        "r1.xml", "<report name=\"1\"/>",
        "r2.xml", "<report name=\"2\"/>"
    ));
    MockMultipartFile file = new MockMultipartFile("artifact", "no-persp.zip", "application/zip", zip);

    assertThrows(InvalidIngestionRequestException.class, () -> runIngestionService.ingestArchive(file));
    try (var dirs = Files.list(storageDir)) {
      assertEquals(0, dirs.count(), "Invalid mergedReport should clean up extracted files");
    }
  }

  private static byte[] createZip(Map<String, String> entries) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ZipOutputStream zos = new ZipOutputStream(baos)) {
      for (Map.Entry<String, String> e : entries.entrySet()) {
        zos.putNextEntry(new ZipEntry(e.getKey()));
        zos.write(e.getValue().getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
      }
    }
    return baos.toByteArray();
  }
}
