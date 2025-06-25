package gui.pack;

import demorec.BatteryPack;
import service.BatteryPackService;
import umlp.jsweet.extension.annotation.Component;

@Component
public class ComponentsOfPack extends ComponentsOfPackTOP {

  BatteryPackService overallService;

  public ComponentsOfPack(BatteryPackService service) {
    overallService = service;
  }

  @Override
  public void setPack(BatteryPack pack) {
    super.setPack(pack);
    overallService.setBatteryPack(pack);
  }
}
