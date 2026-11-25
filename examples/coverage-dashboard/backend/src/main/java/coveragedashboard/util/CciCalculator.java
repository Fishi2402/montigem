package coveragedashboard.util;

import coveragedashboard.CoverageMetrics;
import coveragedashboard.CoveragePerspective;
import coveragedashboard.CoverageReport;
import jsweet.lang.Erased;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for computing normalized perspective coverage ratios and the
 * Composite Coverage Index (CCI) using a weighted harmonic mean.
 */
@Erased
public final class CciCalculator {

  private CciCalculator() {
  }

  /**
   * Computes the normalized coverage ratio {@code C in [0, 1]} for a direct perspective
   * based on covered and missed instructions (or statements).
   */
  public static double computeDirectPerspectiveCoverage(CoverageMetrics metrics) {
    if (metrics == null) {
      return 0.0;
    }
    long covered = metrics.getCoveredInstructions();
    long total = covered + metrics.getMissedInstructions();
    if (total <= 0) {
      return 0.0;
    }
    return (double) covered / (double) total;
  }

  /**
   * Computes the normalized coverage ratio {@code C in [0, 1]} for an aggregated perspective:
   * {@code (L_covered + 0.5 * L_partial) / (L_covered + L_partial + L_missed)}.
   * Unmapped lines are excluded from the denominator.
   */
  public static double computeAggregatedPerspectiveCoverage(CoverageMetrics metrics) {
    if (metrics == null) {
      return 0.0;
    }
    long covered = metrics.getCoveredLines();
    long partial = metrics.getPartialLines();
    long missed = metrics.getMissedLines();
    long total = covered + partial + missed;
    if (total <= 0) {
      return 0.0;
    }
    return (covered + 0.5 * partial) / (double) total;
  }

  /**
   * Computes the weighted harmonic mean over a report's direct perspective and all aggregated perspectives.
   *
   * @param directPerspective      the direct perspective (must not be null)
   * @param aggregatedPerspectives optional list of aggregated perspectives
   * @param perspectiveWeights     optional map of perspective name (e.g. "TS", "JAVA", "FTL") to positive weight
   */
  public static double computeReportCoverage(
      CoveragePerspective directPerspective,
      List<CoveragePerspective> aggregatedPerspectives,
      Map<String, Double> perspectiveWeights) {
    List<CoveragePerspective> all = new ArrayList<>();
    if (directPerspective != null) {
      all.add(directPerspective);
    }
    if (aggregatedPerspectives != null) {
      all.addAll(aggregatedPerspectives);
    }
    return computePerspectivesCci(all, perspectiveWeights);
  }

  /**
   * Computes the weighted harmonic mean over a list of {@link CoveragePerspective}s.
   */
  public static double computePerspectivesCci(
      List<CoveragePerspective> perspectives,
      Map<String, Double> perspectiveWeights) {
    if (perspectives == null || perspectives.isEmpty()) {
      return 0.0;
    }

    List<Double> values = new ArrayList<>(perspectives.size());
    List<Double> weights = (perspectiveWeights != null && !perspectiveWeights.isEmpty())
        ? new ArrayList<>(perspectives.size())
        : null;

    for (CoveragePerspective perspective : perspectives) {
      values.add(perspective.getCoverageValue());
      if (weights != null) {
        Double w = perspectiveWeights.get(perspective.getName());
        weights.add(w != null ? w : 1.0);
      }
    }

    return weightedHarmonicMean(values, weights);
  }

  /**
   * Computes the TestGroup-level Composite Coverage Index (CCI) across the {@link CoverageReport}s
   * in a {@code TestGroup}, proportionally normalizing their weights to sum to {@code 1.0}.
   */
  public static double computeTestGroupCci(List<CoverageReport> reports, List<Double> weights) {
    return computeReportsHarmonicMean(reports, weights);
  }

  /**
   * Computes the flat Run-level Composite Coverage Index (CCI) across all {@link CoverageReport}s
   * in the {@code Run}.
   */
  public static double computeRunCci(List<CoverageReport> allReports, List<Double> weights) {
    return computeReportsHarmonicMean(allReports, weights);
  }

  /**
   * Computes the flat Run-level Composite Coverage Index (CCI) from precomputed report/aggregated
   * coverage values and optional weights.
   */
  public static double computeRunCciFromValues(List<Double> coverageValues, List<Double> weights) {
    return weightedHarmonicMean(coverageValues, weights);
  }

  /**
   * Computes the weighted harmonic mean of {@code values}.
   * <ul>
   *   <li>Returns {@code 0.0} if {@code values} is null/empty or if any {@code C_i <= 0.0}.</li>
   *   <li>If {@code weights} is null, assigns equal weights {@code 1 / N}.</li>
   *   <li>Otherwise normalizes {@code weights} so {@code sum(w_i) == 1.0}; all weights must be finite and {@code > 0}.</li>
   * </ul>
   */
  public static double weightedHarmonicMean(List<Double> values, List<Double> weights) {
    if (values == null || values.isEmpty()) {
      return 0.0;
    }

    int n = values.size();
    double[] normalizedWeights = new double[n];
    if (weights == null) {
      double equalWeight = 1.0 / n;
      for (int i = 0; i < n; i++) {
        normalizedWeights[i] = equalWeight;
      }
    } else {
      if (weights.size() != n) {
        throw new IllegalArgumentException(
            "Weights size (" + weights.size() + ") must match values size (" + n + ").");
      }
      double weightSum = 0.0;
      for (int i = 0; i < n; i++) {
        Double w = weights.get(i);
        if (w == null || !Double.isFinite(w) || w <= 0.0) {
          throw new IllegalArgumentException("All weights must be finite positive numbers, got: " + w);
        }
        weightSum += w;
      }
      for (int i = 0; i < n; i++) {
        normalizedWeights[i] = weights.get(i) / weightSum;
      }
    }

    double reciprocalSum = 0.0;
    for (int i = 0; i < n; i++) {
      Double c = values.get(i);
      if (c == null || !Double.isFinite(c) || c <= 0.0) {
        return 0.0;
      }
      reciprocalSum += normalizedWeights[i] / c;
    }

    return 1.0 / reciprocalSum;
  }

  private static double computeReportsHarmonicMean(List<CoverageReport> reports, List<Double> weights) {
    if (reports == null || reports.isEmpty()) {
      return 0.0;
    }
    List<Double> values = new ArrayList<>(reports.size());
    for (CoverageReport report : reports) {
      values.add(report.getCoverageValue());
    }
    return weightedHarmonicMean(values, weights);
  }
}
