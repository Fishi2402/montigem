package coveragedashboard.commands;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.Run;
import coveragedashboard.TestGroup;
import umlp.backendrte.command.ListResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.util.ArrayList;
import java.util.List;

public class GetLatestTestGroups extends GetLatestTestGroupsTOP {

  public GetLatestTestGroups() {
    super();
  }

  @Override
  public ListResult<TestGroup, List<TestGroup>> doAction() {
    List<TestGroup> result = getTestGroups();
    return ListResult.of(getId(), result, TestGroup.class.getName());
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  protected List<TestGroup> getTestGroups() {
    if (CoverageDashboardManager.getRunList().isEmpty()) {
      return new ArrayList<>();
    }
    Run run = CoverageDashboardManager.getRunList().getFirst();
    return new ArrayList<>(run.getTestGroupsList());
  }
}
