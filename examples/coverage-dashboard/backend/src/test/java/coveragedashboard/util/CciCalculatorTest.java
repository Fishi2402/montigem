package coveragedashboard.util;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageMetrics;
import coveragedashboard.CoverageModule;
import coveragedashboard.CoveragePerspective;
import coveragedashboard.CoverageReport;
import coveragedashboard.PerspectiveType;
import coveragedashboard.ReportType;
import coveragedashboard.test_infrastructure.CoverageDashboardMockTestSetup;
import org.junit.jupiter.api.Test;
import umlp.test_rte.ClientSetup;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CciCalculatorTest extends CoverageDashboardMockTestSetup {

  private static final double EPS = 1e-9;

  public CciCalculatorTest() {
    super(ClientSetup.of(1));
  }

  @Test
  void computesDirectPerspectiveCoverage() {
    CoverageMetrics m = metrics(75, 25, 0, 0, 0, 0);
    assertEquals(0.75, CciCalculator.computeDirectPerspectiveCoverage(m), EPS);

    CoverageMetrics empty = metrics(0, 0, 0, 0, 0, 0);
    assertEquals(0.0, CciCalculator.computeDirectPerspectiveCoverage(empty), EPS);
  }

  @Test
  void computesAggregatedPerspectiveCoverageExcludingUnmapped() {
    // (6 + 0.5 * 2) / (6 + 2 + 2) = 7 / 10 = 0.7; unmapped=50 must not affect ratio
    CoverageMetrics m = metrics(0, 0, 6, 2, 2, 50);
    assertEquals(0.7, CciCalculator.computeAggregatedPerspectiveCoverage(m), EPS);

    CoverageMetrics onlyUnmapped = metrics(0, 0, 0, 0, 0, 42);
    assertEquals(0.0, CciCalculator.computeAggregatedPerspectiveCoverage(onlyUnmapped), EPS);
  }

  @Test
  void computesWeightedHarmonicMeanAndRespectsBottleneckZeroRule() {
    // Equal weights: 2 / (1/0.8 + 1/0.2) = 2 / (1.25 + 5.0) = 0.32
    double equal = CciCalculator.weightedHarmonicMean(List.of(0.8, 0.2), null);
    assertEquals(0.32, equal, EPS);
    // Harmonic mean is strictly bounded above by arithmetic mean (0.5)
    assertTrue(equal < 0.5);

    // Custom non-normalized weights [3, 1] -> normalized [0.75, 0.25]:
    // 1 / (0.75/0.8 + 0.25/0.2) = 1 / (0.9375 + 1.25) = 1 / 2.1875
    double weighted = CciCalculator.weightedHarmonicMean(List.of(0.8, 0.2), List.of(3.0, 1.0));
    assertEquals(1.0 / 2.1875, weighted, EPS);

    // Zero bottleneck rule: any component == 0 forces CCI == 0
    assertEquals(0.0, CciCalculator.weightedHarmonicMean(List.of(1.0, 0.9, 0.0), null), EPS);
    assertEquals(0.0, CciCalculator.weightedHarmonicMean(List.of(), null), EPS);
  }

  @Test
  void rejectsInvalidWeights() {
    assertThrows(IllegalArgumentException.class,
        () -> CciCalculator.weightedHarmonicMean(List.of(0.5, 0.5), List.of(1.0)));
    assertThrows(IllegalArgumentException.class,
        () -> CciCalculator.weightedHarmonicMean(List.of(0.5, 0.5), List.of(1.0, 0.0)));
    assertThrows(IllegalArgumentException.class,
        () -> CciCalculator.weightedHarmonicMean(List.of(0.5, 0.5), List.of(1.0, -2.0)));
  }

  @Test
  void computesReportTestGroupAndFlatRunCci() {
    CoveragePerspective ts = perspective("TS", PerspectiveType.MEASURED_ROOT, 0.8);
    CoveragePerspective ftl = perspective("FTL", PerspectiveType.AGGREGATED, 0.4);

    double reportCovDefault = CciCalculator.computeReportCoverage(ts, List.of(ftl), null);
    assertEquals(2.0 / (1.0 / 0.8 + 1.0 / 0.4), reportCovDefault, EPS);

    // Custom perspective weights: FTL weighted 3x TS
    double reportCovWeighted = CciCalculator.computeReportCoverage(
        ts, List.of(ftl), Map.of("TS", 1.0, "FTL", 3.0));
    assertEquals(1.0 / (0.25 / 0.8 + 0.75 / 0.4), reportCovWeighted, EPS);

    CoverageReport r1 = report("R1", 0.8);
    CoverageReport r2 = report("R2", 0.4);
    CoverageReport r3 = report("R3", 0.6);

    // TestGroup CCI with proportional weights [2, 1] -> [2/3, 1/3]
    double groupCci = CciCalculator.computeTestGroupCci(List.of(r1, r2), List.of(2.0, 1.0));
    assertEquals(1.0 / ((2.0 / 3.0) / 0.8 + (1.0 / 3.0) / 0.4), groupCci, EPS);

    // Flat Run CCI across all 3 reports with equal weights
    double runCci = CciCalculator.computeRunCci(List.of(r1, r2, r3), null);
    assertEquals(3.0 / (1.0 / 0.8 + 1.0 / 0.4 + 1.0 / 0.6), runCci, EPS);
  }

  private CoverageMetrics metrics(int covInstr, int missInstr, int covL, int partL, int missL, int unmapL) {
    return CoverageDashboardManager.coverageMetricsBuilder()
        .coveredInstructions(covInstr)
        .missedInstructions(missInstr)
        .coveredBranches(0)
        .missedBranches(0)
        .coveredLines(covL)
        .partialLines(partL)
        .missedLines(missL)
        .unmappedLines(unmapL)
        .build().get();
  }

  private CoveragePerspective perspective(String name, PerspectiveType type, double value) {
    CoverageModule root = CoverageDashboardManager.coverageModuleBuilder()
        .name("")
        .metrics(metrics(0, 0, 0, 0, 0, 0))
        .build().get();
    return CoverageDashboardManager.coveragePerspectiveBuilder()
        .name(name)
        .type(type)
        .rootModule(root)
        .coverageValue(value)
        .build().get();
  }

  private CoverageReport report(String name, double value) {
    CoveragePerspective p = perspective("JAVA", PerspectiveType.MEASURED_ROOT, value);
    return CoverageDashboardManager.coverageReportBuilder()
        .name(name)
        .type(ReportType.GENERATOR_UNIT_TEST)
        .directPerspective(p)
        .coverageValue(value)
        .build().get();
  }
}
