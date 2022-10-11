package hardy.commands;

// RTE-P

import hardy.FlensburgManager;
import umlp.command.LongList;
import umlp.command.SimpleResult;
import umlp.request.Request;
import umlp.rte.GemList;
import umlp.rte.GeneralErrorCollector;
import umlp.rte.LoadingState;

import java.util.Optional;

// Loads the gemIds in a GemList, between two indices of the list
// TODO AHe: Rework, since there is no situation in which a sublist of the ids is send
public class GemList_getIds extends Request<LongList> {
  protected long gemListId;
  protected int startIdxIncl;
  protected int endIdxExcl;

  public GemList_getIds(long gemListId, int startIdxIncl, int endIdxExcl) {
    super(
        // Der commandType wird gebraucht um bei der Deserialisierung von Commands eine Instanz der richtigen Klasse zu erzeugen
        "hardy.commands.GemList_getIds"
    );

    if(startIdxIncl < 0){
      throw new IllegalStateException("startIdxIncl must be 0 or higher!");
    }

    this.gemListId = gemListId;
    this.startIdxIncl = startIdxIncl;
    this.endIdxExcl = endIdxExcl;
  }

  @Override
  public SimpleResult<LongList> doRun() {
    Optional<GemList<?>> gemListOpt = FlensburgManager.getGemListOpt(gemListId);
    if (gemListOpt.isEmpty()) {
      GeneralErrorCollector.reportSyncError();
      return new SimpleResult<>(getId(), LoadingState.FAILED_TO_LOAD, "umlp.command.LongList");
    }

    GemList<?> gemList = gemListOpt.get();
    if(endIdxExcl == -1){
      endIdxExcl = gemList.size();
    }

    if(gemList.size() < endIdxExcl){
      GeneralErrorCollector.reportSyncError();
      return new SimpleResult<>(getId(), LoadingState.FAILED_TO_LOAD, "umlp.command.LongList");
    }

    LongList res = new LongList(gemList.getIDs(startIdxIncl, endIdxExcl));
    return new SimpleResult<>(getId(), res, "umlp.command.LongList");
  }
}
