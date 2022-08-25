package hardy.commands;

// RTE-P

import hardy.FlensburgManager;
import umlp.command.Command;
import umlp.command.StatusResult;
import umlp.rte.GemList;
import umlp.rte.GeneralErrorCollector;

import java.util.Optional;
import java.util.Set;

// Entfernt die angegebenen indizes aus der GemList
public class GemList_removeIdxs extends Command<Void> {
  protected long gemListId;
  protected Set<Integer> indices;

  public GemList_removeIdxs(long gemListId, Set<Integer> indices) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.GemList_removeIdxs"
    );
    this.gemListId = gemListId;
    this.indices = indices;
  }

  @Override
  public StatusResult doRun() {
    Optional<GemList<?>> gemListOpt = FlensburgManager.getGemListOpt(gemListId);
    if (gemListOpt.isEmpty()) {
      GeneralErrorCollector.reportSyncError();
      return StatusResult.error(getId());
    }
    GemList<?> gemList = gemListOpt.get();

    gemList.removeIdxs(indices);
    // TODO: error handling!

    return StatusResult.ok(getId());
  }
}
