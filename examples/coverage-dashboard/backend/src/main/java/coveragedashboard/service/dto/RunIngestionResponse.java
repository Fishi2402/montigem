package coveragedashboard.service.dto;

import coveragedashboard.ReportType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST response summary for an ingested coverage {@code Run}.
 */
public record RunIngestionResponse(
    long runId,
    String commitHash,
    String branchName,
    LocalDateTime timestamp,
    List<TestGroupSummary> testGroups
) {

  public record TestGroupSummary(
      String name,
      List<ReportSummary> reports
  ) {}

  public record ReportSummary(
      String name,
      ReportType type
  ) {}
}
