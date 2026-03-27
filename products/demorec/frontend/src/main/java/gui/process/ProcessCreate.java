package gui.process;/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.Log;
import demorec.BatteryPack;
import demorec.DemoRecManager;
import demorec.Process;
import umlp.guiutil.HTMLUtils;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Consumer;


@Component
public class ProcessCreate extends ProcessCreateTOP {

  public void init() {
    super.init();
  }

  @Override
  public Consumer<BatteryPack> changePack() {
    return (BatteryPack pack) -> {
      this.getP().setDeconstructionOf(pack);
    };
  }

  public Consumer<Void> sendEverything() {
    return $ -> {
      if (this.getP().getStartedAt() == null || !this.getP().getDeconstructionOfGem().isPresent()) {
        HTMLUtils.alert("Please set a Started at Date and also set the pack you want to disassemble.");
        return;
      }
      Optional<Process> p = (Optional<Process>) this.getP().build();
      if (p.isPresent()) {
        Router.navigate("gui/process/ProcessOverview/" + this.getP().getGemId());
      }
    };
  }
  }

