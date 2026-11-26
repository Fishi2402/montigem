package coverage.components;

import coveragedashboard.CoverageReport;
import coveragedashboard.Run;
import coveragedashboard.TestGroup;
import coveragedashboard.commands.GetLatestComponentTest;
import coveragedashboard.commands.GetLatestGeneratedCodeTest;
import coveragedashboard.commands.GetLatestIntegrationTest;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandResult;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GeneralDashboard extends GeneralDashboardTOP {

  public GeneralDashboard() {
    super();
  }

  @Override
  public String getRunCciText(Run run) {
    return CoverageUtil.formatPercentage(run.getCci());
  }

  @Override
  public String getRunCciColor(Run run) {
    return CoverageUtil.getColorForRatio(run.getCci());
  }

  @Override
  public String getShortCommit(Run run) {
    String hash = run.getCommitHash();
    if (hash == null || hash.isEmpty()) {
      return "unknown";
    }
    return hash.length() > 8 ? hash.substring(0, 8) : hash;
  }

  @Override
  public String getFormattedTimestamp(Run run) {
    if (run.getTimestamp() == null) {
      return "";
    }
    return run.getTimestamp().toString().replace("T", " ");
  }

  @Override
  public Boolean hasAggregatedReports(Run run) {
    return run != null && !run.getAggregatedReportsList().isEmpty();
  }

  @Override
  public String getGroupTitle(TestGroup group) {
    return group.getName() + " — CCI: " + CoverageUtil.formatPercentage(group.getCci());
  }

  public List<CoverageReport> getReportsForGroup(TestGroup group) {
    List<CoverageReport> reports = new ArrayList<>();

    CommandResult<CoverageReport> resComp = CommandManager.executeCommand(new GetLatestComponentTest(group.getGemId()));
    if (resComp.isSuccessful() && resComp.isSimpleResult() && resComp.asSimpleResult().get() != null) {
      reports.add(resComp.asSimpleResult().get());
    }

    CommandResult<CoverageReport> resIt = CommandManager.executeCommand(new GetLatestIntegrationTest(group.getGemId()));
    if (resIt.isSuccessful() && resIt.isSimpleResult() && resIt.asSimpleResult().get() != null) {
      reports.add(resIt.asSimpleResult().get());
    }

    CommandResult<CoverageReport> resGen = CommandManager.executeCommand(new GetLatestGeneratedCodeTest(group.getGemId()));
    if (resGen.isSuccessful() && resGen.isSimpleResult() && resGen.asSimpleResult().get() != null) {
      reports.add(resGen.asSimpleResult().get());
    }

    return reports;
  }

}
