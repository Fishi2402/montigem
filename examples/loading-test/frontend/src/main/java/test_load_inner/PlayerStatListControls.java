package test_load_inner;

import loadingtests.Game;
import loadingtests.LoadingTestsManager;
import loadingtests.Player;
import loadingtests.commands.AddPlayerRemote;
import loadingtests.commands.AddStatRemote;
import loadingtests.commands.Reset;
import umlp.backendrte.command.CommandManager;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class PlayerStatListControls extends PlayerStatListControlsTOP {
  @Override
  public Consumer<Void> reset() {
    return unused -> {
      CommandManager.executeCommand(new Reset(this.getGemIdForNav()));
    };
  }

  @Override
  public Consumer<Void> addPlayer_Local() {
    return unused -> {
      Game g = LoadingTestsManager.getGameSure(this.getGemIdForNav());

      g.addPlayer(LoadingTestsManager.
          playerBuilder().name("From Local")
              .isActive(true)
              .points(0)
          .build().get());
    };
  }

  @Override
  public Consumer<Void> addStat_Local() {
    return unused -> {
      Game g = LoadingTestsManager.getGameSure(this.getGemIdForNav());

      // TODO: broken var p = g.getPlayerList().get(0);
      Player p = g.getPlayerList().get(0);

      p.addPlayerStat(LoadingTestsManager.
          playerStatBuilder().statID("local stat of " + p.getName()).build().get());
    };
  }

  @Override
  public Consumer<Void> addPlayer_Remote() {
    return unused -> {
      CommandManager.executeCommand(new AddPlayerRemote(this.getGemIdForNav()));
    };
  }

  @Override
  public Consumer<Void> addStat_Remote() {
    return unused -> {
      CommandManager.executeCommand(new AddStatRemote(this.getGemIdForNav()));
    };
  }
}
