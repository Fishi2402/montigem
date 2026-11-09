package coverage.components;

import coveragedashboard.CoverageMetrics;

import java.util.function.Function;

public final class CoverageUtil {

  private CoverageUtil(){
  }

  public static double calculateCoverage(CoverageMetrics metrics){
    double covered = metrics.getCoveredLines() + 0.5 * metrics.getPartialLines();
    int total = metrics.getCoveredLines() + metrics.getMissedLines() + metrics.getPartialLines();
    if (total == 0) {
      return 0;
    }
    double coverage = covered / total;
    return Math.round(coverage * 10000.0) / 100.0;
  }

  public static double calculateCoverageForInstructions(CoverageMetrics metrics){
    int covered = metrics.getCoveredInstructions();
    int total = metrics.getCoveredInstructions() + metrics.getMissedInstructions();
    if (total == 0) {
      return 0;
    }
    double coverage = (double) covered / total;
    return Math.round(coverage * 10000.0) / 100.0;
  }

  public static String getCoveragePercentage(CoverageMetrics metrics, Function<CoverageMetrics, Double> coverageCalculator){
    double coverage = coverageCalculator.apply(metrics);
    return coverage + "%";
  }

  public static String getCoverageColor(CoverageMetrics metrics, Function<CoverageMetrics, Double> coverageCalculator) {
    double coverage = coverageCalculator.apply(metrics);
    if(coverage < 50){
      return "#cf222e";
    }else if(coverage < 80){
      return "#8a5c00";
    }
    return "#1a7f37";
  }
}
