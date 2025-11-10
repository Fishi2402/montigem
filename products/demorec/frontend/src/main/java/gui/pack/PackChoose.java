package gui.pack;

import demorec.BatteryPack;
import demorec.DemoRecComponent;
import demorec.DemoRecManager;
import mc.fenix.input.gemdropdowninputtypes.Option;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PackChoose extends PackChooseTOP {

  public String packEntry = "";

  @Override
  public List<Option> getOpts(List<BatteryPack> allPacks) {
    List<Option> packOptions = new ArrayList<>();
    packOptions.add(new Option("NEW"));
    if(allPacks != null) {
      for (BatteryPack p : allPacks) {
        packOptions.add(new Option(String.valueOf(p.getGemId()), p.getName()));
      }
    }
    return packOptions;
  }

  @Override
  public void setSelectPackEntry(String val) {
    this.packEntry = val;
    if(val == "NEW") {
      this.setNewPack(true);
      this.setPack(null);
    } else {
      this.setNewPack(false);
      BatteryPack pack = DemoRecManager.getBatteryPackSure(Long.valueOf(val));
      this.setPack(pack);
      this.getChosenPack().accept(pack);
    }
  }

  @Override
  public String getSelectPackEntry() {
    if(this.getPack() == null) {
      return "NEW";
    }
    return this.getPack().getName();
  }
}
