package gui.kind;

import demorec.ComponentKind;
import demorec.service.BatteryPackService;
import umlp.guiutil.AngularUtils;
import umlp.jsweet.extension.annotation.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public class CreateComponentKind extends CreateComponentKindTOP {

  BatteryPackService packService = AngularUtils.inject(BatteryPackService.class);;

  public CreateComponentKind() {
    super();
  }

  public Consumer<Void> createNewComponentKind() {
    return $ -> {
      Optional<ComponentKind> kindOpt = (Optional<ComponentKind>) this.getKind().build();
      if(kindOpt.isPresent()) {
        if(this.packService != null) {
          if(packService.getBatteryPack() != null) {
            kindOpt.get().setBatteryPack(packService.getBatteryPack());
          }
        }
        this.getNewKind().accept(kindOpt.get());
      }
    };
  }
}
