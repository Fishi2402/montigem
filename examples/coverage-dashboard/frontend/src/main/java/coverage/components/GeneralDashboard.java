package coverage.components;

import coveragedashboard.CoverageReport;
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
