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
    double cci,
    List<TestGroupSummary> testGroups
) {

  public record TestGroupSummary(
      String name,
      double cci,
      List<ReportSummary> reports
  ) {}

  public record ReportSummary(
      String name,
      ReportType type,
      double coverageValue
  ) {}
}
