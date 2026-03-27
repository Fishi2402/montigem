/* (c) https://github.com/MontiCore/monticore */
package gui.step;

import demorec.DemoRecComponent;
import demorec.DemoRecManager;
import demorec.Process;
import demorec.ProcessStep;
import gui.step.StepTableTOP;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class StepTable extends StepTableTOP {

  public List<ProcessStep> getReverseProcessSteps(Process process){
    if(process == null) {
      return new ArrayList<>();
    }
    return process.getConsistsOfList();
  }

  public String getMeasuredValuesCountValue(ProcessStep step) {
    return step.getMeasuredList().size() + "";
  }

  public Consumer<Void> editStep(ProcessStep step) {
    return (x) -> Router.navigate("gui/step/ProcessStepEditPage/"+this.getProcess().getGemId()+"/"+step.getGemId());
  }

  public Consumer<Void> deleteStep(ProcessStep step) {
    return $ -> {
      if(this.getProcess().removeConsistsOf(step)) {
        DemoRecManager.deleteProcessStep(step.getGemId());
      }
    };
  }

  public String getConnectionType(ProcessStep step)  {
    if(step.isPresentDeconstructs()) {
      return step.getDeconstructs().getJointConnectionKind().getName();
    }
    return "";
  }

  public String getComponents(ProcessStep step) {
    String components = "";
    boolean first = true;
    if(!step.isPresentDeconstructs()) {
      return "";
    }
    for (DemoRecComponent component : step.getDeconstructs().getJointConnectionKind().getConnectsList()) {
      if(!first) {
        components+=",";
      }
      first = false;
      components+=component.getName();
    }

    return components;
  }
}
