package coveragedashboard.commands;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageReport;
import coveragedashboard.ReportType;
import coveragedashboard.TestGroup;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.common.LoadingState;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.util.Optional;

public class GetLatestGeneratedCodeTest extends GetLatestGeneratedCodeTestTOP {

  public GetLatestGeneratedCodeTest(long groupGemId){
    super(groupGemId);
  }

  @Override
  public SimpleResult<CoverageReport> doAction(){
    Optional<CoverageReport> result = getReport(this.getGroupGemId());
    if(result.isEmpty()){
      return new SimpleResult<>(
          getId(), LoadingState.FAILED_TO_LOAD, CoverageReport.class.getName());
    }
    return new SimpleResult<>(getId(), result.get(), CoverageReport.class.getName());
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  protected Optional<CoverageReport> getReport(long groupGemId){
    Optional<TestGroup> groupOpt = CoverageDashboardManager.testGroupOpt(groupGemId);
    if(groupOpt.isEmpty()){
      return Optional.empty();
    }
    TestGroup group = groupOpt.get();
    for(CoverageReport report : group.getReportsList()){
      if(report.getType() == ReportType.GENERATED_CODE_TEST){
        return Optional.of(report);
      }
    }
    return Optional.empty();
  }
}
