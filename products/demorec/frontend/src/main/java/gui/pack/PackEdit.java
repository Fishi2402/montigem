package gui.pack;

import demorec.BatteryPackType;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class PackEdit extends PackEditTOP {

  public Consumer<BatteryPackType> changePackType() {
    return (t) -> {
      this.getPack().setType(t);
    };
  }

  public Consumer<Void> editBatteryPackType() {
    return $ -> {
      Router.navigate("gui", "pack", "PackTypeEditPage",String.valueOf(
          this.getPack().getGemId()), String.valueOf(
          this.getPack().getType().getGemId()));
    };
  }
}
