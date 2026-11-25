package coveragedashboard.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArtifactStorageServiceTest {

  @TempDir
  Path tempDir;

  private IngestionProperties properties;
  private ArtifactStorageService storageService;

  @BeforeEach
  void setUp() {
    properties = new IngestionProperties();
    properties.setStorageDir(tempDir);
    properties.setMaxExtractedBytes(1024 * 1024);
    storageService = new ArtifactStorageService(properties);
  }

  @Test
  void extractsValidZipPreservingStructure() throws IOException {
    byte[] zip = createZip(Map.of(
        "manifest.json", "{\"commitHash\":\"abc\"}",
        "sub/dir/report.xml", "<report/>"
    ));
    MockMultipartFile file = new MockMultipartFile("artifact", "bundle.zip", "application/zip", zip);

    Path runDir = storageService.extractToRunDirectory(file);

    assertTrue(Files.isDirectory(runDir));
    assertTrue(runDir.startsWith(tempDir.toAbsolutePath().normalize()));
    assertEquals("{\"commitHash\":\"abc\"}", Files.readString(runDir.resolve("manifest.json")));
    assertEquals("<report/>", Files.readString(runDir.resolve("sub/dir/report.xml")));
  }

  @Test
  void rejectsEmptyArchive() {
    MockMultipartFile empty = new MockMultipartFile("artifact", "empty.zip", "application/zip", new byte[0]);
    assertThrows(InvalidIngestionRequestException.class, () -> storageService.extractToRunDirectory(empty));
  }

  @Test
  void rejectsNonZipPayloadAndCleansUp() throws IOException {
    MockMultipartFile notZip = new MockMultipartFile(
        "artifact", "bad.zip", "application/zip", "not a zip".getBytes(StandardCharsets.UTF_8));

    assertThrows(InvalidIngestionRequestException.class, () -> storageService.extractToRunDirectory(notZip));
    try (var children = Files.list(tempDir)) {
      assertEquals(0, children.count(), "Failed extraction should not leave behind directories");
    }
  }

  @Test
  void rejectsZipSlipAndCleansUp() throws IOException {
    byte[] maliciousZip = createZip(Map.of(
        "../../escape.txt", "pwned"
    ));
    MockMultipartFile file = new MockMultipartFile("artifact", "evil.zip", "application/zip", maliciousZip);

    assertThrows(InvalidIngestionRequestException.class, () -> storageService.extractToRunDirectory(file));
    try (var children = Files.list(tempDir)) {
      assertEquals(0, children.count(), "Rejected Zip-Slip upload should clean up its run directory");
    }
  }

  @Test
  void rejectsArchiveExceedingMaxExtractedBytes() throws IOException {
    properties.setMaxExtractedBytes(10);
    byte[] zip = createZip(Map.of(
        "large.txt", "01234567890123456789"
    ));
    MockMultipartFile file = new MockMultipartFile("artifact", "large.zip", "application/zip", zip);

    assertThrows(InvalidIngestionRequestException.class, () -> storageService.extractToRunDirectory(file));
    try (var children = Files.list(tempDir)) {
      assertEquals(0, children.count(), "Oversized archive should clean up its run directory");
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
