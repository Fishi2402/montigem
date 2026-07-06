/* (c) https://github.com/MontiCore/monticore */
package gui.pack;

import demorec.BatteryPack;
import demorec.service.BatteryPackService;
import umlp.guiutil.AngularUtils;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;
import java.util.function.Consumer;

@Component
public class BatteryPackOverview extends BatteryPackOverviewTOP {

  public Consumer<Void> editPack() {
    return (x) -> {
        Router.navigate("gui/pack/PackEditPage/" + this.getPack().getGemId());
    };
  }

  BatteryPackService overallService = AngularUtils.inject(BatteryPackService.class);

  public BatteryPackOverview() {}

  @Override
  public void setPack(BatteryPack pack) {
    super.setPack(pack);
    overallService.setBatteryPack(pack);
  }
}
