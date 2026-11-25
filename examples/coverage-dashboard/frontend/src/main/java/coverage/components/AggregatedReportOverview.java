package coverage.components;

import app.AppRouter;
import coveragedashboard.AggregatedCoverageReport;
import coveragedashboard.CoveragePerspective;
import coveragedashboard.CoverageReport;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class AggregatedReportOverview extends AggregatedReportOverviewTOP {

  public AggregatedReportOverview() {
    super();
  }

  @Override
  public String getReportTitle(AggregatedCoverageReport report) {
    return report.getName() + " — " + CoverageUtil.formatPercentage(report.getCoverageValue());
  }

  @Override
  public String getProvenanceText(AggregatedCoverageReport report) {
    List<String> names = new ArrayList<>();
    for (CoverageReport src : report.getSourceReportsList()) {
      names.add(src.getName());
    }
    return "Merged from: " + String.join(" + ", names) + " (replaces individual suites in Run CCI)";
  }

  @Override
  public String getPerspectiveTitle(CoveragePerspective perspective) {
    return "Merged " + perspective.getName() + " — " + CoverageUtil.formatPercentage(perspective.getCoverageValue());
  }

  @Override
  public Consumer<Void> showDetails(AggregatedCoverageReport report) {
    return $ -> {
      if (!report.getPerspectivesList().isEmpty()) {
        CoveragePerspective first = report.getPerspectives(0);
        AppRouter.navigateToCoverageAggregatedCoverageDetails(report.getGemId(), first.getGemId());
      }
    };
  }
}
