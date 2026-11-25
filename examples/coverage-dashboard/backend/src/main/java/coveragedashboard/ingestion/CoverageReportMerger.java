package coveragedashboard.ingestion;

import coveragedashboard.AggregatedCoverageReport;
import coveragedashboard.AggregatedCoverageReportBuilder;
import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageModule;
import coveragedashboard.CoveragePerspective;
import coveragedashboard.CoverageReport;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.PerspectiveType;
import coveragedashboard.util.CciCalculator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Merges explicitly requested perspectives across multiple {@link CoverageReport}s into a dedicated
 * {@link AggregatedCoverageReport} using line-by-line logical-OR aggregation.
 */
public class CoverageReportMerger {

  private final CoverageModuleAggregator moduleAggregator;

  public CoverageReportMerger() {
    this(new CoverageModuleAggregator());
  }

  CoverageReportMerger(CoverageModuleAggregator moduleAggregator) {
    this.moduleAggregator = moduleAggregator;
  }

  public AggregatedCoverageReport mergeReports(
      String name,
      List<CoverageReport> sourceReports,
      List<String> perspectives,
      Map<String, Double> perspectiveWeights) {

    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Merged report name must not be blank.");
    }
    if (sourceReports == null || sourceReports.size() < 2) {
      throw new IllegalArgumentException("At least two source reports are required to create a merged report.");
    }
    if (perspectives == null || perspectives.isEmpty()) {
      throw new IllegalArgumentException("At least one perspective must be explicitly specified for merging.");
    }

    // Preserve requested perspective names (deduplicated case-insensitively)
    Map<String, String> requestedPerspectives = new LinkedHashMap<>();
    for (String p : perspectives) {
      if (p == null || p.isBlank()) {
        throw new IllegalArgumentException("Perspective names in merged report '" + name + "' must not be blank.");
      }
      String trimmed = p.trim();
      requestedPerspectives.putIfAbsent(trimmed.toUpperCase(Locale.ROOT), trimmed);
    }

    List<CoveragePerspective> mergedPerspectives = new ArrayList<>();
    for (Map.Entry<String, String> entry : requestedPerspectives.entrySet()) {
      String normalizedName = entry.getKey();
      String displayName = entry.getValue();

      List<CoverageSourceFile> collectedFiles = new ArrayList<>();
      for (CoverageReport report : sourceReports) {
        collectFilesForPerspective(report.getDirectPerspective(), normalizedName, collectedFiles);
        for (CoveragePerspective agg : report.getAggregatedPerspectivesList()) {
          collectFilesForPerspective(agg, normalizedName, collectedFiles);
        }
      }

      if (collectedFiles.isEmpty()) {
        throw new IllegalArgumentException(
            "Merged report '" + name + "' requested perspective '" + displayName
                + "', but none of the source reports contain files for that perspective.");
      }

      CoverageModule rootModule = moduleAggregator.mergeFilesInModules(collectedFiles);
      double perspectiveCoverage = CciCalculator.computeAggregatedPerspectiveCoverage(rootModule.getMetrics());

      CoveragePerspective mergedPerspective = CoverageDashboardManager.coveragePerspectiveBuilder()
          .name(displayName)
          .type(PerspectiveType.AGGREGATED)
          .rootModule(rootModule)
          .coverageValue(perspectiveCoverage)
          .build()
          .get();

      mergedPerspectives.add(mergedPerspective);
    }

    double reportCoverage = CciCalculator.computePerspectivesCci(mergedPerspectives, perspectiveWeights);

    AggregatedCoverageReportBuilder builder = CoverageDashboardManager.aggregatedCoverageReportBuilder()
        .name(name)
        .coverageValue(reportCoverage);

    for (CoverageReport source : sourceReports) {
      builder.sourceReportsAdd(source);
    }
    for (CoveragePerspective perspective : mergedPerspectives) {
      builder.perspectivesAdd(perspective);
    }

    return builder.build().get();
  }

  private void collectFilesForPerspective(
      CoveragePerspective perspective,
      String normalizedTargetName,
      List<CoverageSourceFile> out) {
    if (perspective == null || perspective.getName() == null) {
      return;
    }
    if (!perspective.getName().trim().toUpperCase(Locale.ROOT).equals(normalizedTargetName)) {
      return;
    }
    collectModuleFiles(perspective.getRootModule(), out);
  }

  private void collectModuleFiles(CoverageModule module, List<CoverageSourceFile> out) {
    if (module == null) {
      return;
    }
    if (module.getSourceFilesList() != null) {
      out.addAll(module.getSourceFilesList());
    }
    if (module.getSubModulesList() != null) {
      for (CoverageModule sub : module.getSubModulesList()) {
        collectModuleFiles(sub, out);
      }
    }
  }
}
