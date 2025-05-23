package gui.pack;

import demorec.BatteryPack;
import service.BatteryPackService;
import umlp.jsweet.extension.annotation.Component;

@Component
public class BatteryPackOverview extends BatteryPackOverviewTOP {

  BatteryPackService overallService;

  public BatteryPackOverview(BatteryPackService service) {
    overallService = service;
  }

  @Override
  public void setPack(BatteryPack pack) {
    super.setPack(pack);
    overallService.setBatteryPack(pack);
  }
}
