package gui.process;/* (c) https://github.com/MontiCore/monticore */

import demorec.BatteryPack;
import demorec.DemoRecManager;
import demorec.Process;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class ProcessCreate extends ProcessCreateTOP {

  public void init() {
    super.init();
    this.getP().setStartedAt(LocalDate.now());
  }

  @Override
  public Consumer<BatteryPack> changePack() {
    return (BatteryPack pack) -> {
      this.getP().setDeconstructionOf(pack);
    };
  }

  public Consumer<Void> sendEverything() {
    return $ -> {
      Optional< Process> p = (Optional< Process>) this.getP().build();
      if(p.isPresent()) {
        Router.navigate("gui/process/ProcessOverview/"+this.getP().getGemId());
      }
    };
  }

}
