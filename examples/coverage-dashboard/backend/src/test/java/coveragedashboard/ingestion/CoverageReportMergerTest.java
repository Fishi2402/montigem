package coveragedashboard.ingestion;

import coveragedashboard.AggregatedCoverageReport;
import coveragedashboard.ArtifactCategory;
import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageLine;
import coveragedashboard.CoverageMetrics;
import coveragedashboard.CoverageModule;
import coveragedashboard.CoveragePerspective;
import coveragedashboard.CoverageReport;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.PerspectiveType;
import coveragedashboard.ReportType;
import coveragedashboard.test_infrastructure.CoverageDashboardMockTestSetup;
import org.junit.jupiter.api.Test;
import umlp.test_rte.ClientSetup;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CoverageReportMergerTest extends CoverageDashboardMockTestSetup {

  private static final double EPS = 1e-9;

  public CoverageReportMergerTest() {
    super(ClientSetup.of(1));
  }

  @Test
  void mergesComplementaryTemplateCoverageViaLogicalOr() {
    // Report 1 covers line 1, misses line 2 (50% FTL coverage)
    CoverageReport r1 = buildReportWithFtl("Report1",
        measuredLine(1, 1, 0),
        measuredLine(2, 0, 1));

    // Report 2 misses line 1, covers line 2 (50% FTL coverage)
    CoverageReport r2 = buildReportWithFtl("Report2",
        measuredLine(1, 0, 1),
        measuredLine(2, 1, 0));

    CoverageReportMerger merger = new CoverageReportMerger();
    AggregatedCoverageReport merged = merger.mergeReports(
        "Combined E2E",
        List.of(r1, r2),
        List.of("FTL"),
        null
    );

    assertEquals("Combined E2E", merged.getName());
    assertEquals(2, merged.getSourceReportsList().size());
    assertEquals(1, merged.getPerspectivesList().size());

    CoveragePerspective mergedFtl = merged.getPerspectives(0);
    assertEquals("FTL", mergedFtl.getName());
    assertEquals(PerspectiveType.AGGREGATED, mergedFtl.getType());
    assertEquals(1.0, mergedFtl.getCoverageValue(), EPS);
    assertEquals(1.0, merged.getCoverageValue(), EPS);
  }

  @Test
  void mergesOnlyExplicitlyRequestedPerspectivesAndAppliesWeights() {
    CoverageReport r1 = buildReportWithFtl("Report1", measuredLine(1, 1, 0), measuredLine(2, 0, 1));
    CoverageReport r2 = buildReportWithFtl("Report2", measuredLine(1, 1, 0), measuredLine(2, 1, 0));

    CoverageReportMerger merger = new CoverageReportMerger();
    // Both reports have JAVA (direct, 100%) and FTL (merged: 100%)
    AggregatedCoverageReport merged = merger.mergeReports(
        "Combined Multi",
        List.of(r1, r2),
        List.of("FTL", "JAVA"),
        Map.of("FTL", 3.0, "JAVA", 1.0)
    );

    assertEquals(2, merged.getPerspectivesList().size());
    assertEquals(1.0, merged.getCoverageValue(), EPS);

    // Requesting a perspective that doesn't exist in either report must fail
    assertThrows(IllegalArgumentException.class, () ->
        merger.mergeReports("Missing", List.of(r1, r2), List.of("TS"), null));
  }

  private CoverageLine measuredLine(int nr, int ci, int mi) {
    return CoverageDashboardManager.measuredLineBuilder()
        .lineNumber(nr)
        .coveredInstructions(ci)
        .missedInstructions(mi)
        .coveredBranches(0)
        .missedBranches(0)
        .build().get();
  }

  private CoverageReport buildReportWithFtl(String name, CoverageLine l1, CoverageLine l2) {
    int cov = (l1 instanceof coveragedashboard.MeasuredLine m1 && m1.getCoveredInstructions() > 0 ? 1 : 0)
        + (l2 instanceof coveragedashboard.MeasuredLine m2 && m2.getCoveredInstructions() > 0 ? 1 : 0);
    int miss = 2 - cov;

    CoverageMetrics ftlMetrics = CoverageDashboardManager.coverageMetricsBuilder()
        .coveredInstructions(cov).missedInstructions(miss)
        .coveredBranches(0).missedBranches(0)
        .coveredLines(cov).partialLines(0).missedLines(miss).unmappedLines(0)
        .build().get();

    CoverageSourceFile ftlFile = CoverageDashboardManager.coverageSourceFileBuilder()
        .name("Shared.ftl")
        .path("templates/Shared.ftl")
        .physicalPath("/tmp/Shared.ftl")
        .category(ArtifactCategory.TEMPLATE)
        .metrics(ftlMetrics)
        .linesAdd(l1)
        .linesAdd(l2)
        .build().get();

    CoverageModuleAggregator aggregator = new CoverageModuleAggregator();
    CoverageModule ftlRoot = aggregator.groupFilesInModules(List.of(ftlFile));
    CoveragePerspective ftlPerspective = CoverageDashboardManager.coveragePerspectiveBuilder()
        .name("FTL")
        .type(PerspectiveType.AGGREGATED)
        .rootModule(ftlRoot)
        .coverageValue(cov / 2.0)
        .build().get();

    CoverageMetrics javaMetrics = CoverageDashboardManager.coverageMetricsBuilder()
        .coveredInstructions(10).missedInstructions(0)
        .coveredBranches(0).missedBranches(0)
        .coveredLines(1).partialLines(0).missedLines(0).unmappedLines(0)
        .build().get();
    CoverageSourceFile javaFile = CoverageDashboardManager.coverageSourceFileBuilder()
        .name(name + ".java")
        .path("pkg/" + name + ".java")
        .physicalPath("/tmp/" + name + ".java")
        .category(ArtifactCategory.GENERATED)
        .metrics(javaMetrics)
        .linesAdd(measuredLine(1, 10, 0))
        .build().get();
    CoverageModule javaRoot = aggregator.groupFilesInModules(List.of(javaFile));
    CoveragePerspective javaPerspective = CoverageDashboardManager.coveragePerspectiveBuilder()
        .name("JAVA")
        .type(PerspectiveType.MEASURED_ROOT)
        .rootModule(javaRoot)
        .coverageValue(1.0)
        .build().get();

    return CoverageDashboardManager.coverageReportBuilder()
        .name(name)
        .type(ReportType.GENERATED_CODE_TEST)
        .directPerspective(javaPerspective)
        .aggregatedPerspectivesAdd(ftlPerspective)
        .coverageValue(cov == 0 ? 0.0 : 2.0 / (1.0 + 2.0 / cov))
        .build().get();
  }
}
