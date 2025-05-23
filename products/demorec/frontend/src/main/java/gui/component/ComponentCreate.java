package gui.component;

import de.se_rwth.commons.logging.Log;
import demorec.BatteryPack;
import demorec.DemoRecComponent;
import demorec.State;
import service.BatteryPackService;
import umlp.jsweet.extension.annotation.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public class ComponentCreate extends ComponentCreateTOP {

  protected BatteryPack currentPack = null;

  public ComponentCreate(BatteryPackService service) {
    super();
    service.addObserverFunction((BatteryPack p) -> {
      this.currentPack = p;
    });
  }

  @Override
  protected void initComponent () {
    super.initComponent();
    this.getComponent().setCondition(State.HEALTHY);
  }

  @Override
  public Consumer<Void> createNewComponent() {
    return $ -> {
      if(this.getComponent() != null) {
        if(!this.getComponent().isBuildFinished()) {
          Optional<DemoRecComponent> componentOpt = (Optional<DemoRecComponent>) this.getComponent().build();
          if(componentOpt.isPresent()) {
            if((this.currentPack != null) && !componentOpt.get().isPresentBatteryPack()) {
              componentOpt.get().setBatteryPack(this.currentPack);
            } else {
              Log.warn("No Pack available for Component");
            }
            this.getCreateComponent().accept(componentOpt.get());
          }
        }
      }
    };
  }
}
