package gui.pack;

import demorec.BatteryPack;
import demorec.BatteryPackType;
import demorec.DemoRecManager;
import mc.fenix.input.gemdropdowninputtypes.Option;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChoosePackType extends ChoosePackTypeTOP {

  public String packEntry = "";

  @Override
  public List<Option> getOpts(List<BatteryPackType> allPackTypes) {
    List<Option> packOptions = new ArrayList<>();
    packOptions.add(new Option("NEW"));
    if(allPackTypes != null) {
      for (BatteryPackType type : allPackTypes) {
        packOptions.add(new Option(String.valueOf(type.getGemId()), type.getName()));
      }
    }
    return packOptions;
  }

  @Override
  public void setSelectPackTypeEntry(String val) {
    this.packEntry = val;
    if(val == "NEW") {
      this.setNewPackType(true);
      this.setCurrentType(null);
    } else {
      this.setNewPackType(false);
      BatteryPackType type = DemoRecManager.getBatteryPackTypeSure(Long.valueOf(val));
      this.setCurrentType(type);
      this.getChosenType().accept(type);
    }
  }

  @Override
  public String getSelectPackTypeEntry() {
    if(this.packEntry == "NEW") {
      return "";
    }
    if(this.getCurrentType() == null) {
      return "";
    }
    return this.getCurrentType().getName();
  }
}
