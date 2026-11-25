package coverage;

import app.AppRouter;
import coverage.components.CoverageUtil;
import coveragedashboard.CoveragePerspective;
import coveragedashboard.CoverageReport;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class AggregationDetails extends AggregationDetailsTOP{

  @Override
  public String getDirectTitle(CoverageReport report) {
    CoveragePerspective direct = report.getDirectPerspective();
    if (direct == null) {
      return "Direct coverage";
    }
    return "Direct coverage (" + direct.getName() + ") — " + CoverageUtil.formatPercentage(direct.getCoverageValue());
  }

  @Override
  public String getPerspectiveTitle(CoveragePerspective perspective) {
    return "Aggregated coverage - " + perspective.getName()
        + " — " + CoverageUtil.formatPercentage(perspective.getCoverageValue());
  }

  @Override
  public Consumer<Void> backToOverview(){
    return $ ->{
      AppRouter.navigateToCoverageDashboard();
    };
  }

}
