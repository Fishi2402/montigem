package loadingtests.commands;

import loadingtests.Game;
import loadingtests.LoadingTestsManager;
import umlp.backendrte.command.StatusResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;


public class AddPlayerRemote extends AddPlayerRemoteTOP {
  public AddPlayerRemote(long aGemId) {
    super(aGemId);
  }

  @Override
  @BackendOnly(value = BackendOnlyModes.THROW_ERROR)
  public StatusResult doAction() {
    Game g = LoadingTestsManager.getGameSure(this.getGGemId());

    g.addPlayer(LoadingTestsManager.playerBuilder().name("From Remote").points(0).isActive(false).build().get());

    return StatusResult.ok(getId());
  }
}
