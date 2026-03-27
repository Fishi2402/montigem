package gui;

import demorec.DemoRecManager;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;
import demorec.Process;

import java.util.function.Consumer;

@Component
public class Start extends StartTOP {

  @Override
  public Consumer<Void> editProcess (Process p) {
    return (x) -> Router.navigate("gui/process/ProcessOverview/" + p.getGemId());
  }

  @Override
  public Consumer<Void> deleteProcess(Process p) {
    return (x) -> {
      if(p.sizeConsistsOfs() == 0) {
        DemoRecManager.deleteProcess(p.getGemId());
      }
    };
  }

  @Override
  public Boolean isProcessDisabled(Process p) {
    return p.sizeConsistsOfs() > 0;
  }
}
