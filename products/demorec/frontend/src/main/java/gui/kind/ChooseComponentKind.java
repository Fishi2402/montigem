package gui.kind;

import demorec.BatteryPack;
import demorec.ComponentKind;
import demorec.DemoRecManager;
import mc.fenix.input.gemdropdowninputtypes.Option;
import service.BatteryPackService;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class ChooseComponentKind extends ChooseComponentKindTOP {

  public String selectComponentKindEntry = "";

  public ChooseComponentKind(BatteryPackService service) {
    super();
    service.addObserverFunction((BatteryPack p) -> {
      this.setPack(p);
    });
  }

  @Override
  public void setCurrentKind(ComponentKind val) {
    super.setCurrentKind(val);
    if(val != null) {
      this.setSelectComponentKindEntry(String.valueOf(val.getGemId()));
    }
  }

  @Override
  public List<Option> getOptions(List<ComponentKind> kinds, BatteryPack pack) {
    List<Option> options = new ArrayList<>();
    Option newOption = new Option("NEW","AAAAAAA_NEW");
    options.add(newOption);
    if(kinds != null) {
      for (ComponentKind kind : kinds) {
        if (pack != null && kind.isPresentBatteryPack() && pack.getGemId() == kind.getBatteryPack().getGemId()) {
          options.add(new Option(String.valueOf(kind.getGemId()), kind.getName()));
        }
      }
    }
    options.sort((l,r) -> l.getDisplay().compareTo(r.getDisplay()));
    newOption.setDisplay("NEW");
    return options;
  }


  // Is this necessary with JSweet?
  @Override
  public Consumer<ComponentKind> addNewComponentKind() {
    return kind -> {
      this.getAvailableKinds().add(kind);
      this.setSelectComponentKindEntry(String.valueOf(kind.getGemId()));
    };
  }

  @Override
  public void setSelectComponentKindEntry(String val) {
    if(this.getCurrentKind()!=null && (val == String.valueOf(this.getCurrentKind().getGemId()))) {
      return;
    }
    this.selectComponentKindEntry = val;
    if(val == "NEW") {
      this.setNewKind(true);
    } else {
      this.setNewKind(false);
      ComponentKind kind = DemoRecManager.getComponentKindSure(Long.valueOf(val));
      this.setCurrentKind(kind);
      this.getChosenKind().accept(kind);
    }
  }

  @Override
  public String getSelectComponentKindEntry() {
    if(this.getCurrentKind() != null) {
      return this.getCurrentKind().getName();
    }
    return this.selectComponentKindEntry;
  }
}
