package coveragedashboard.service.dto;

import coveragedashboard.ReportType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Schema for {@code manifest.json} placed at the root of an uploaded coverage artifact archive.
 */
public record IngestionManifest(
    String commitHash,
    String branchName,
    LocalDateTime timestamp,
    List<TestGroupEntry> testGroups
) {

  public record TestGroupEntry(
      String name,
      List<ReportEntry> reports
  ) {}

  public record ReportEntry(
      String name,
      ReportType type,
      String reportPath,
      String basePath,
      Double weight,
      Map<String, Double> perspectiveWeights
  ) {}
}
