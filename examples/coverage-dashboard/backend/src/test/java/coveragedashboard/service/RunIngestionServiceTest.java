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
    assertEquals(1, response.testGroups().size());
    assertEquals("umlp-jsweet", response.testGroups().get(0).name());
    assertEquals(1, response.testGroups().get(0).reports().size());
    assertEquals(ReportType.GENERATOR_UNIT_TEST, response.testGroups().get(0).reports().get(0).type());

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
