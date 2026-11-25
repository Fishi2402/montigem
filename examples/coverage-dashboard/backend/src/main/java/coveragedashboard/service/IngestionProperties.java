package coveragedashboard.service;

import jsweet.lang.Erased;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@ConfigurationProperties(prefix = "coverage.ingestion")
@Erased
public class IngestionProperties {

  /**
   * Persistent directory where extracted CI/CD coverage bundles are stored per run.
   */
  private Path storageDir = Path.of("./coverage-storage");

  /**
   * Maximum total uncompressed bytes allowed when extracting an uploaded ZIP archive.
   */
  private long maxExtractedBytes = 1024L * 1024L * 1024L; // 1 GiB

  /**
   * Whether {@code CoverageDashboardServerApplication} should ingest local workspace data at startup.
   */
  private boolean loadLocalData = false;

  public Path getStorageDir() {
    return storageDir;
  }

  public void setStorageDir(Path storageDir) {
    this.storageDir = storageDir;
  }

  public long getMaxExtractedBytes() {
    return maxExtractedBytes;
  }

  public void setMaxExtractedBytes(long maxExtractedBytes) {
    this.maxExtractedBytes = maxExtractedBytes;
  }

  public boolean isLoadLocalData() {
    return loadLocalData;
  }

  public void setLoadLocalData(boolean loadLocalData) {
    this.loadLocalData = loadLocalData;
  }
}
