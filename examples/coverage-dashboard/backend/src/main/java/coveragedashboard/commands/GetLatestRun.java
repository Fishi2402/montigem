package coveragedashboard.commands;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.Run;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.common.LoadingState;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.util.Comparator;
import java.util.Optional;

public class GetLatestRun extends GetLatestRunTOP {

  public GetLatestRun() {
    super();
  }

  @Override
  public SimpleResult<Run> doAction() {
    Optional<Run> result = getLatestRun();
    if (result.isEmpty()) {
      return new SimpleResult<>(
          getId(), LoadingState.FAILED_TO_LOAD, Run.class.getName());
    }
    return new SimpleResult<>(getId(), result.get(), Run.class.getName());
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  protected Optional<Run> getLatestRun() {
    return CoverageDashboardManager.getRunList().stream()
        .max(Comparator.comparing(Run::getTimestamp));
  }
}
