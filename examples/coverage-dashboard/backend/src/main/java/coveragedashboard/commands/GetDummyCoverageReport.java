package coveragedashboard.commands;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageReport;
import coveragedashboard.Run;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.common.LoadingState;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.util.Optional;

public class GetDummyCoverageReport extends GetDummyCoverageReportTOP {

  @Override
  public SimpleResult<CoverageReport> doAction (){
    Optional<CoverageReport> result = dummyCoverageReport();
    if(result.isEmpty()){
      return new SimpleResult<>(
          getId(), LoadingState.FAILED_TO_LOAD, CoverageReport.class.getName());
    }
    return new SimpleResult<>(getId(), result.get(), CoverageReport.class.getName());
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  protected  Optional<CoverageReport> dummyCoverageReport(){
    Run run = CoverageDashboardManager.getRunList().getFirst();
    return Optional.of(run.getReports(0));
  }

}
