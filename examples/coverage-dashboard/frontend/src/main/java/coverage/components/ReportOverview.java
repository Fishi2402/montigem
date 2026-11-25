package coverage.components;

import app.AppRouter;
import coveragedashboard.CoveragePerspective;
import coveragedashboard.CoverageReport;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class ReportOverview extends ReportOverviewTOP{

  public ReportOverview(){
    super();
  }

  @Override
  public String getReportTitle(CoverageReport report) {
    return report.getName() + " — " + CoverageUtil.formatPercentage(report.getCoverageValue());
  }

  @Override
  public String getDirectSubtitle(CoverageReport report) {
    CoveragePerspective direct = report.getDirectPerspective();
    if (direct == null) {
      return "";
    }
    return "Direct (" + direct.getName() + "): " + CoverageUtil.formatPercentage(direct.getCoverageValue());
  }

  @Override
  public Consumer<Void> showDetails (CoverageReport report) {
    return $ -> {
      // To discuss: await not transpiled correctly!
      CoveragePerspective directPerspective = report.getDirectPerspective();
      AppRouter.navigateToCoverageCoverageDetails(report.getGemId(), directPerspective.getGemId());
    };
  }

  @Override
  public Consumer<Void> showAggregatedCoverage (CoverageReport report) {
    return $ -> AppRouter.navigateToCoverageAggregationDetails(report.getGemId());
  }
}
