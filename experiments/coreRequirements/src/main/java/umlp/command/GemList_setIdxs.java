package umlp.command;

// RTE-P

import hardy.FlensburgManager;
import umlp.rte.GemList;
import umlp.rte.GeneralErrorCollector;

import java.util.Map;
import java.util.Optional;

// Setzt die gegebenen indices der gemList auf die entsprechenden gemIds
// z.B. als Resultat des Aufruf p.setOwns(1, car);
public class GemList_setIdxs extends Command<Void> {
  protected long gemListGemId;
  protected Map<Integer, Long> idxToId;

  public GemList_setIdxs(long gemListGemId, Map<Integer, Long> idxToId) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "umlp.command.GemList_setIdxs"
    );

    this.gemListGemId = gemListGemId;
    this.idxToId = idxToId;
  }

  @Override
  public StatusResult doRun() {
    Optional<GemList<?>> gemListOpt = FlensburgManager.getGemListOpt(gemListGemId);
    if (gemListOpt.isEmpty()) {
      GeneralErrorCollector.reportSyncError();
      return StatusResult.error(getId());
    }

    GemList<?> gemList = gemListOpt.get();

    gemList.setIDs(idxToId);
    // TODO: error handling

    return StatusResult.ok(getId());
  }
}
