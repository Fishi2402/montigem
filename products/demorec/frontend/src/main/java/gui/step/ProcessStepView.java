/* (c) https://github.com/MontiCore/monticore */
package gui.step;

import demorec.DemoRecComponent;
import umlp.jsweet.extension.annotation.Component;
// Otherwise JSweet fucks up the import
import demorec.ProcessStepType;
import demorec.JoinConnectionType;

import java.util.ArrayList;

@Component
public class ProcessStepView extends ProcessStepViewTOP {
  public String getIndex () {
    if (this.getProcessStep() != null) {
      return this.getProcessStep().getIndex() + "";
    }
    return "None";
  }

  public String getType() {
    if (this.getProcessStep() != null) {
      return this.getProcessStep().getType().toString();
    }
    return "None";
  }

  public String getEndEffectorName() {
    if (this.getProcessStep() != null) {
      if (this.getProcessStep().getUses() != null) {
        return this.getProcessStep().getUses().getName();
      }
      return "None";
    }
    return "None";
  }

  public Boolean getHasJoinConnectionCondition () {
    if (this.getProcessStep() != null) {
      return (this.getProcessStep().getDeconstructs() != null);
    }
    return false;
  }

  public String getJoinConnectionLabelText () {
    if (this.getHasJoinConnectionCondition()) {
      if (this.getProcessStep().getDeconstructs().getJoinConnectionKind() != null) {
        return "Dissolved Components of Join Connection of Type '"
            + this.getProcessStep().getDeconstructs().getJoinConnectionKind().getJoinConnectionType().toString()
            + "':";
      }
    }
    return "Dissolved Components of Join Connection";
  }

  public String getJoinConnectionComponents () {
    if (this.getHasJoinConnectionCondition()) {
      if (this.getProcessStep().getDeconstructs().getJoinConnectionKind() != null) {
        if (this.getProcessStep().getDeconstructs().getJoinConnectionKind().getConnectsList() != null) {
          var componentNames = new ArrayList<>();
          for (DemoRecComponent component : this.getProcessStep().getDeconstructs().getJoinConnectionKind().getConnectsList()) {
            componentNames.add(component.getName());
          }
          return componentNames.toString();
        }
        return "None";
      }
      return "None";
    }
    return "None";
  }

  public String getDescription () {
    if (this.getProcessStep() != null) {
      if (this.getProcessStep().getDescription().isPresent() && this.getProcessStep().getDescription() != null) {
        if (this.getProcessStep().getDescription().get().length() > 0) {
          return this.getProcessStep().getDescription().get();
        }
      }
    }
    return "None";
  }

  public String getDuration() {
    if (this.getProcessStep() != null) {
      if (this.getProcessStep().getTimeConsumption() != null) {
        return this.secondsToMinutes(this.getProcessStep().getTimeConsumption()) + " (" + this.getProcessStep().getTimeConsumption() + " seconds)";
      }
      return "None";
    }
    return "None";
  }

  private String secondsToMinutes(double seconds) {
    var minutes = Math.floor(seconds / 60);
    var remainingSeconds = seconds % 60;
    return minutes+" minute(s) and "+ remainingSeconds +" second(s)";
  }
}


