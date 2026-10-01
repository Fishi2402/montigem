package coverage.components;

import coveragedashboard.CoverageReport;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class ReportOverview extends ReportOverviewTOP{

  public ReportOverview(){
    super();
  }

  @Override
  public Consumer<Void> showDetails (CoverageReport report) {
    return $ ->{
      Router.navigate("coverage", "CoverageDetails", String.valueOf(report.getGemId()));
    };
  }

  @Override
  public Consumer<Void> showAggregatedCoverage (CoverageReport report) {
    return $ ->{
      Router.navigate("coverage", "AggregationDetails", String.valueOf(report.getGemId()));
    };
  }
}
