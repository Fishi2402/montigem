package coveragedashboard.commands;

import coveragedashboard.AggregatedCoverageReport;
import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageModule;
import coveragedashboard.CoveragePerspective;
import coveragedashboard.CoverageReport;
import coveragedashboard.PerspectiveType;
import coveragedashboard.ReportType;
import coveragedashboard.Run;
import coveragedashboard.TestGroup;
import coveragedashboard.test_infrastructure.CoverageDashboardMockTestSetup;
import mc.fenix.charts.gemlinecharttypes.GemLineChartData;
import mc.fenix.charts.gemlinecharttypes.GemLineChartEntry;
import org.junit.jupiter.api.Test;
import umlp.backendrte.command.ListResult;
import umlp.backendrte.command.SimpleResult;
import umlp.test_rte.ClientSetup;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RunCommandsTest extends CoverageDashboardMockTestSetup {

  public RunCommandsTest() {
    super(ClientSetup.of(1));
  }

  @Test
  void getAllRunsReturnsRunsSortedNewestFirst() {
    Run older = createRun("aaaa1111bbbb", "dev", LocalDateTime.of(2026, 11, 25, 10, 0), 0.45, "group-a", 0.45, null, 0.0);
    Run newer = createRun("cccc2222dddd", "main", LocalDateTime.of(2026, 11, 26, 9, 30), 0.625, "group-a", 0.625, null, 0.0);

    ListResult<Run, List<Run>> result = new GetAllRuns().doAction();
    assertTrue(result.isSuccessful());
    List<Run> runs = result.get();
    assertEquals(2, runs.size());
    assertEquals(newer.getGemId(), runs.get(0).getGemId());
    assertEquals(older.getGemId(), runs.get(1).getGemId());
  }

  @Test
  void getCciTrajectoryReturnsAscendingPercentageSeries() {
    createRun("aaaa1111bbbb", "dev", LocalDateTime.of(2026, 11, 25, 10, 0), 0.4523, "jsweet-test", 0.40, "Combined FTL", 0.50);
    createRun("cccc2222dddd", "main", LocalDateTime.of(2026, 11, 26, 9, 30), 0.625, "jsweet-test", 0.60, "Combined FTL", 0.65);

    SimpleResult<GemLineChartData> result = new GetCciTrajectory().doAction();
    assertTrue(result.isSuccessful());
    GemLineChartData data = result.get();
    assertNotNull(data);

    assertEquals(List.of("2026-11-25 10:00 (aaaa1111)", "2026-11-26 09:30 (cccc2222)"), data.getLabelsList());
    assertEquals(3, data.getEntriesList().size());

    GemLineChartEntry runEntry = data.getEntriesList().get(0);
    assertEquals("Run CCI (%)", runEntry.getLabel());
    assertEquals(List.of(45.23, 62.5), runEntry.getDataList());

    GemLineChartEntry mergedEntry = data.getEntriesList().get(1);
    assertEquals("Combined FTL (%)", mergedEntry.getLabel());
    assertEquals(List.of(50.0, 65.0), mergedEntry.getDataList());

    GemLineChartEntry groupEntry = data.getEntriesList().get(2);
    assertEquals("jsweet-test (%)", groupEntry.getLabel());
    assertEquals(List.of(40.0, 60.0), groupEntry.getDataList());
  }

  private static Run createRun(
      String commitHash,
      String branchName,
      LocalDateTime timestamp,
      double runCci,
      String groupName,
      double groupCci,
      String mergedName,
      double mergedValue) {
    CoverageModule rootMod = CoverageDashboardManager.coverageModuleBuilder()
        .name("root")
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(10).missedInstructions(10)
            .coveredBranches(0).missedBranches(0)
            .coveredLines(5).partialLines(0).missedLines(5).unmappedLines(0)
            .build().get())
        .build().get();
    CoveragePerspective direct = CoverageDashboardManager.coveragePerspectiveBuilder()
        .name("JAVA")
        .type(PerspectiveType.MEASURED_ROOT)
        .rootModule(rootMod)
        .coverageValue(groupCci)
        .build().get();
    CoverageReport report = CoverageDashboardManager.coverageReportBuilder()
        .name("Report-" + commitHash)
        .type(ReportType.GENERATED_CODE_TEST)
        .directPerspective(direct)
        .coverageValue(groupCci)
        .build().get();
    TestGroup group = CoverageDashboardManager.testGroupBuilder()
        .name(groupName)
        .cci(groupCci)
        .reportsAdd(report)
        .build().get();

    var runBuilder = CoverageDashboardManager.runBuilder()
        .commitHash(commitHash)
        .branchName(branchName)
        .timestamp(timestamp)
        .cci(runCci)
        .testGroupsAdd(group);

    if (mergedName != null) {
      CoverageModule mergedMod = CoverageDashboardManager.coverageModuleBuilder()
          .name("root")
          .metrics(CoverageDashboardManager.coverageMetricsBuilder()
              .coveredInstructions(0).missedInstructions(0)
              .coveredBranches(0).missedBranches(0)
              .coveredLines(5).partialLines(0).missedLines(5).unmappedLines(0)
              .build().get())
          .build().get();
      CoveragePerspective mergedPerspective = CoverageDashboardManager.coveragePerspectiveBuilder()
          .name("FTL")
          .type(PerspectiveType.AGGREGATED)
          .rootModule(mergedMod)
          .coverageValue(mergedValue)
          .build().get();
      AggregatedCoverageReport merged = CoverageDashboardManager.aggregatedCoverageReportBuilder()
          .name(mergedName)
          .coverageValue(mergedValue)
          .sourceReportsAdd(report)
          .perspectivesAdd(mergedPerspective)
          .build().get();
      runBuilder.aggregatedReportsAdd(merged);
    }

    return runBuilder.build().get();
  }
}
