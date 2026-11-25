package coveragedashboard.service;

import de.se_rwth.commons.logging.Log;
import jsweet.lang.Erased;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Safely extracts uploaded ZIP coverage bundles into a persistent per-run directory so both
 * ingestion parsers and {@code GetFileContent} can read reports, source maps, and source files.
 */
@Service
@Erased
public class ArtifactStorageService {

  private static final DateTimeFormatter DIR_TIMESTAMP =
      DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
  private static final int BUFFER_SIZE = 8192;

  private final IngestionProperties properties;

  public ArtifactStorageService(IngestionProperties properties) {
    this.properties = properties;
  }

  public Path extractToRunDirectory(MultipartFile archive) {
    if (archive == null || archive.isEmpty()) {
      throw new InvalidIngestionRequestException("Uploaded artifact must not be empty.");
    }

    Path storageRoot = properties.getStorageDir().toAbsolutePath().normalize();
    Path runDir = storageRoot.resolve(
        "_staging_" + UUID.randomUUID().toString().substring(0, 8)
    ).normalize();

    try {
      Files.createDirectories(runDir);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to create run storage directory: " + runDir, e);
    }

    long maxBytes = properties.getMaxExtractedBytes();
    long totalBytes = 0;
    int entryCount = 0;

    try (InputStream rawStream = new BufferedInputStream(archive.getInputStream());
         ZipInputStream zis = new ZipInputStream(rawStream)) {

      byte[] buffer = new byte[BUFFER_SIZE];
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        entryCount++;
        Path target = resolveSafePath(runDir, entry.getName());

        if (entry.isDirectory()) {
          Files.createDirectories(target);
        } else {
          Path parent = target.getParent();
          if (parent != null) {
            Files.createDirectories(parent);
          }
          try (OutputStream out = Files.newOutputStream(target)) {
            int read;
            while ((read = zis.read(buffer)) != -1) {
              totalBytes += read;
              if (totalBytes > maxBytes) {
                throw new InvalidIngestionRequestException(
                    "Extracted archive size exceeds maximum allowed limit of " + maxBytes + " bytes.");
              }
              out.write(buffer, 0, read);
            }
          }
        }
        zis.closeEntry();
      }

      if (entryCount == 0) {
        throw new InvalidIngestionRequestException("Uploaded file is not a valid ZIP archive or contains no entries.");
      }

      return runDir;
    } catch (InvalidIngestionRequestException e) {
      deleteQuietly(runDir);
      throw e;
    } catch (IOException e) {
      deleteQuietly(runDir);
      throw new InvalidIngestionRequestException("Failed to extract ZIP archive: " + e.getMessage(), e);
    } catch (RuntimeException e) {
      deleteQuietly(runDir);
      throw e;
    }
  }

  public Path moveToCommitDirectory(Path stagingDir, String commitHash) {
    String sanitizedCommit = commitHash.trim().replaceAll("[^a-zA-Z0-9_-]", "_");
    if (sanitizedCommit.isEmpty()) {
      throw new InvalidIngestionRequestException("Manifest field 'commitHash' contains no valid identifier characters.");
    }

    Path storageRoot = properties.getStorageDir().toAbsolutePath().normalize();
    String baseName = DIR_TIMESTAMP.format(LocalDateTime.now()) + "_" + sanitizedCommit;
    Path targetDir = storageRoot.resolve(baseName).normalize();
    int suffix = 1;
    while (Files.exists(targetDir)) {
      targetDir = storageRoot.resolve(baseName + "_" + suffix++).normalize();
    }

    try {
      return Files.move(stagingDir, targetDir);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to move staging directory to " + targetDir, e);
    }
  }

  public Path resolveSafePath(Path runDir, String relativeEntryPath) {
    if (relativeEntryPath == null || relativeEntryPath.isBlank()) {
      throw new InvalidIngestionRequestException("Archive or manifest contains an empty path.");
    }
    Path normalizedRunDir = runDir.toAbsolutePath().normalize();
    Path target = normalizedRunDir.resolve(relativeEntryPath).normalize();
    if (!target.startsWith(normalizedRunDir)) {
      throw new InvalidIngestionRequestException(
          "Path escapes run directory (Zip-Slip / path traversal rejected): " + relativeEntryPath);
    }
    return target;
  }

  public void deleteQuietly(Path directory) {
    if (directory == null || !Files.exists(directory)) {
      return;
    }
    try (Stream<Path> walk = Files.walk(directory)) {
      walk.sorted(Comparator.reverseOrder()).forEach(path -> {
        try {
          Files.deleteIfExists(path);
        } catch (IOException e) {
          Log.warn("Failed to clean up path " + path + ": " + e.getMessage());
        }
      });
    } catch (IOException e) {
      Log.warn("Failed to clean up run directory " + directory + ": " + e.getMessage());
    }
  }
}
