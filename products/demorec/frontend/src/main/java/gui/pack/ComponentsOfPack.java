package gui.pack;

import demorec.BatteryPack;
import demorec.service.BatteryPackService;
import umlp.guiutil.AngularUtils;
import umlp.jsweet.extension.annotation.Component;

@Component
public class ComponentsOfPack extends ComponentsOfPackTOP {

  BatteryPackService overallService = AngularUtils.inject(BatteryPackService.class);;

  public ComponentsOfPack() {
  }

  @Override
  public void setPack(BatteryPack pack) {
    super.setPack(pack);
    overallService.setBatteryPack(pack);
  }
}
