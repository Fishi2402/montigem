package loadingtests.commands;

import loadingtests.Animal;
import loadingtests.Food;
import loadingtests.Game;
import loadingtests.LoadingTestsManager;
import loadingtests.Player;
import loadingtests.PlayerStat;
import umlp.backendrte.command.StatusResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Reset extends ResetTOP {
  public Reset(long aGemId) {
    super(aGemId);
  }

  @Override
  @BackendOnly(value = BackendOnlyModes.THROW_ERROR)
  public StatusResult doAction() {
    Game g = LoadingTestsManager.getGameSure(this.getGGemId());
    Objects.nonNull(g);
    for (Player p : g.getPlayerList()) {
      var stats = p.getPlayerStatList().stream().toList();
      p.clearPlayerStats();
      stats.forEach(playerStat -> {
        LoadingTestsManager.deletePlayerStat(playerStat.getGemId());
      });
    }
    var players = g.getPlayerList().stream().map(Player::getGemId).collect(Collectors.toList());
    g.clearPlayers();
    players.forEach(LoadingTestsManager::deletePlayer);

    g.addPlayer(LoadingTestsManager.playerBuilder().name("Initial").points(0).build().get());

    return StatusResult.ok(getId());
  }
}
