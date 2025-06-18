package gui.pack;

import demorec.BatteryPackType;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class PackEdit extends PackEditTOP {

  public Consumer<BatteryPackType> changePackType() {
    return (t) -> {
      this.getPack().setType(t);
    };
  }
}
