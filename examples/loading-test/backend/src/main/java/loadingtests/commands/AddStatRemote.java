package loadingtests.commands;

import loadingtests.Animal;
import loadingtests.Food;
import loadingtests.Game;
import loadingtests.LoadingTestsManager;
import loadingtests.Player;
import umlp.backendrte.command.StatusResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.util.List;

public class AddStatRemote extends AddStatRemoteTOP {
  public AddStatRemote(long aGemId) {
    super(aGemId);
  }

  @Override
  @BackendOnly(value = BackendOnlyModes.THROW_ERROR)
  public StatusResult doAction() {
    Game g = LoadingTestsManager.getGameSure(this.getGGemId());
    Player p = g.getPlayerList().get(0);
    if (p != null)
     p.addPlayerStat(LoadingTestsManager.playerStatBuilder().statID("remote stat of " + p.getName()).build().get());

    return StatusResult.ok(getId());
  }
}
