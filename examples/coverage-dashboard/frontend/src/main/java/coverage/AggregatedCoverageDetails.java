package coverage;

import app.AppRouter;
import coverage.components.CoverageUtil;
import coveragedashboard.AggregatedCoverageReport;
import coveragedashboard.CoveragePerspective;
import coveragedashboard.CoverageReport;
import coveragedashboard.CoverageSourceFile;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class AggregatedCoverageDetails extends AggregatedCoverageDetailsTOP {

  public AggregatedCoverageDetails() {
    super();
    this.setActiveFiles(List.of());
  }

  @Override
  public Consumer<Void> backToOverview() {
    return $ -> AppRouter.navigateToCoverageDashboard();
  }

  @Override
  public String getTitle(AggregatedCoverageReport report) {
    return "Aggregated Coverage Details: " + report.getName()
        + " — " + CoverageUtil.formatPercentage(report.getCoverageValue());
  }

  @Override
  public String getProvenanceText(AggregatedCoverageReport report) {
    List<String> names = new ArrayList<>();
    for (CoverageReport src : report.getSourceReportsList()) {
      names.add(src.getName());
    }
    return "Merged from: " + String.join(" + ", names);
  }

  @Override
  public String getRootDisplayName(CoveragePerspective perspective) {
    return "Merged perspective - " + perspective.getName();
  }

  @Override
  public Consumer<CoverageSourceFile> onFileSelect() {
    return file -> this.setActiveFiles(List.of(file));
  }

  @Override
  public Consumer<Void> onPerspectiveClicked(CoveragePerspective perspective) {
    return $ -> {
      this.setSelectedPerspective(perspective);
      this.setActiveFiles(List.of());
    };
  }

  @Override
  public Optional<CoverageSourceFile> getBaseFile(List<CoverageSourceFile> activeFiles) {
    if (activeFiles == null || activeFiles.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(activeFiles.get(activeFiles.size() - 1));
  }
}
