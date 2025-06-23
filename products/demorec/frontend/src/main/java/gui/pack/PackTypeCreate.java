package gui.pack;

import demorec.BatteryPackType;
import demorec.DemoRecComponent;
import demorec.DemoRecManager;
import umlp.jsweet.extension.annotation.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public class PackTypeCreate extends PackTypeCreateTOP {

  public Consumer<Void> createNewBatteryPackType() {
    return $ -> {
      Optional<BatteryPackType> typeOpt = (Optional<BatteryPackType>) this.getType().build();
      if(typeOpt.isPresent()) {
        this.getNewType().accept(typeOpt.get());
      }
    };
  }
}
