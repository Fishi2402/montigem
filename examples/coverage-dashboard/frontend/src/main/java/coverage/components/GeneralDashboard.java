package coverage.components;

import coveragedashboard.App;
import coveragedashboard.CoverageReport;
import umlp.backendrte.command.SimpleResult;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class GeneralDashboard extends GeneralDashboardTOP{

  public GeneralDashboard(){
    super();
  }

  @Override
  public Consumer<Void> debugDetails(){

    SimpleResult<CoverageReport> report = App.executeGetDummyCoverageReport();
    return $ -> {
      Router .navigate("coverage", "CoverageDetails", String.valueOf(report.get().getGemId()));
    };
  }
}
