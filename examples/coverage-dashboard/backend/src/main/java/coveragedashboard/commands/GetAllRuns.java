package coveragedashboard.commands;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.Run;
import umlp.backendrte.command.ListResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.util.Comparator;
import java.util.List;

public class GetAllRuns extends GetAllRunsTOP {

  public GetAllRuns() {
    super();
  }

  @Override
  public ListResult<Run, List<Run>> doAction() {
    List<Run> result = getAllRunsSorted();
    return ListResult.of(getId(), result, Run.class.getName());
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  protected List<Run> getAllRunsSorted() {
    return CoverageDashboardManager.getRunList().stream()
        .sorted(Comparator.comparing(Run::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
        .toList();
  }
}
