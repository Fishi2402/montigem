package gui.process;

import demorec.DemoRecComponent;
import demorec.ProcessStep;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class ProcessOverview extends ProcessOverviewTOP {

  public Consumer<ProcessStep> attachStep() {
    return (ProcessStep step) -> {
      if(this.getP().getConsistsOfList().size() == 0) {
        step.setIndex(0);
      } else {
        int maxIndex = 0;

        for (ProcessStep existingStep : this.getP().getConsistsOfList()) {
          if(existingStep.getIndex() > maxIndex) {
            maxIndex = existingStep.getIndex();
          }
        }

        step.setIndex(maxIndex + 1);
      }
      this.getP().addConsistsOf(step);

      if(step.getDeconstructsGem().isPresent()) {
        for (DemoRecComponent component : step.getDeconstructs().getJoinConnectionKind().getConnectsList()) {
          if(!this.getP().getDeconstructionOf().containsAllComponents(component)) {
            this.getP().getDeconstructionOf().addAllComponents(component);
          }
        }
      }
    };
  }
}
