package coverage.components;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageReport;
import mc.fenix.charts.gemlinecharttypes.DataPointClickEvent;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public class GeneralDashboard extends GeneralDashboardTOP{

  public GeneralDashboard(){
    super();
  }

  /*
  @Override
  public Consumer<Object> onDataPointClick () {
    return (Object o) ->{
      if(o instanceof DataPointClickEvent){
        // cast via instance of can not be transpiled via JSweet...
        DataPointClickEvent dataPointClickEvent = (DataPointClickEvent) o;

        Optional<CoverageReport> reportOpt = CoverageDashboardManager.coverageReportOpt(dataPointClickEvent.getId());
        if(reportOpt.isPresent()){
          Router.navigate("coverage", "CoverageDetails", String.valueOf(reportOpt.get().getGemId()));
        }
      }
    };
  }*/
}
