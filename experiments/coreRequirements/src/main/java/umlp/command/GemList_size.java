package umlp.command;

// RTE-P

import hardy.FlensburgManager;
import umlp.request.Request;
import umlp.rte.GemList;
import umlp.rte.GeneralErrorCollector;
import umlp.rte.LoadingState;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class GemList_size extends Request<Integer> {
  protected long gemListId;

  public GemList_size(long gemListId) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "umlp.command.GemList_size"
    );

    this.gemListId = gemListId;
  }

  @Override
  public SimpleResult<Integer> doRun() {
    Optional<GemList<?>> gemListOpt = FlensburgManager.getGemListOpt(gemListId);
    if (gemListOpt.isEmpty()) {
      GeneralErrorCollector.reportSyncError();
      return new SimpleResult<>(getId(), LoadingState.FAILED_TO_LOAD, "java.lang.Integer");
    }

    return new SimpleResult<>(getId(), gemListOpt.get().size(), "java.lang.Integer");
  }

  @Override
  public Collection<Long> getNewlySubscribedGemIds() {
    return Collections.singleton(gemListId);
  }
}
