package gui.pack;

import demorec.BatteryPack;
import umlp.jsweet.extension.annotation.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public class PackCreate extends PackCreateTOP {

  public Consumer<Void> createNewBatteryPack() {
    return $ -> {
      if(this.getPack() != null) {
        Optional<BatteryPack> packOpt = (Optional<BatteryPack>) this.getPack().build();
        if(packOpt.isPresent()) {
          this.getNewPack().accept(packOpt.get());
        }
      }
    };
  }
}
