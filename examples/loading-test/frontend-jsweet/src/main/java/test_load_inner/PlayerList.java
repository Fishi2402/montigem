package test_load_inner;

import loadingtests.Player;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class PlayerList extends PlayerListTOP {
  @Override
  public Consumer<Void> openPlayerInfo(Player player) {
    return (unused) -> {
      if (player != null && this.getPlayer() != null) {
        if(this.getSelectedPlayer() == player && this.isDisplayed()) {
          this.setDisplayed(false);
        }else {
          this.setSelectedPlayer(player);
          this.setDisplayed(true);
        }
      }
    };
  }
}
