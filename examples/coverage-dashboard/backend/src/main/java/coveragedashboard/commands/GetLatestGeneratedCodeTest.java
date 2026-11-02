package coveragedashboard.commands;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageReport;
import coveragedashboard.ReportType;
import coveragedashboard.Run;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.common.LoadingState;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.util.Optional;

public class GetLatestGeneratedCodeTest extends GetLatestGeneratedCodeTestTOP {

  public GetLatestGeneratedCodeTest(){
    super();
  }


  @Override
  public SimpleResult<CoverageReport> doAction(){
    Optional<CoverageReport> result = getReport();
    if(result.isEmpty()){
      return new SimpleResult<>(
          getId(), LoadingState.FAILED_TO_LOAD, CoverageReport.class.getName());
    }
    return new SimpleResult<>(getId(), result.get(), CoverageReport.class.getName());
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  protected Optional<CoverageReport> getReport(){
    Run run = CoverageDashboardManager.getRunList().getFirst();
    for(CoverageReport report : run.getReportsList()){
      if(report.getType() == ReportType.GENERATED_CODE_TEST){
        return Optional.of(report);
      }
    }
    return Optional.empty();
  }
}
