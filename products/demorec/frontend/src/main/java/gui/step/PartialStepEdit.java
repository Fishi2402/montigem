package gui.step;

import de.se_rwth.commons.logging.Log;
import demorec.DemoRecManager;
import demorec.EndEffector;
import mc.fenix.input.gemdropdowninputtypes.Option;
import mc.fenix.input.gemtextinputtypes.GemTextInputValidity;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static def.dom.Globals.clearInterval;
import static def.dom.Globals.setInterval;

@Component
public class PartialStepEdit extends PartialStepEditTOP {

  public String endEffectorEntry = "";
  public List<Option> endEffectorOptions = new ArrayList<>();


  @Override
  public void init() {
    super.init();
    if(this.getEndEffectors() == null) {
      return;
    }
    for (EndEffector endEffector : this.getEndEffectors()) {
      this.endEffectorOptions.add(new Option(String.valueOf(endEffector.getGemId()), endEffector.getName()));
      this.endEffectorOptions.sort((l,r) -> l.getDisplay().compareTo(r.getDisplay()));
    }
  }

  // Timer
  //private double passedSeconds = 0;
  private double timerInterval;


  @Override
  public Consumer<Void> startTimer() {
    return $ -> {
      this.setPassedSeconds(0.0);
      // TODO Timer
      Consumer<Void> lambda = $$ -> {
        this.setPassedSeconds(this.getPassedSeconds()+0.1);
        Log.info("Is Called", this.getClass().getName());
      };
      timerInterval = setInterval(lambda, 100);
      this.setWasStarted(true);
    };
  }

  @Override
  public Consumer<Void> stopTimer() {
    return $ -> {
      clearInterval(this.timerInterval);
      this.setTimeConsumptionEntry(String.valueOf(Math.round(this.getPassedSeconds())));
      this.setWasStarted(false);
    };
  }

  @Override
  public Boolean getStartTimerButtonDisabled () {
    // Default seems to be "undefined" or after a correct number was given as input "0"
    if(this.getTimeConsumptionEntry() != null) {
      return Integer.valueOf(this.getTimeConsumptionEntry()) > 0;
    }
    return false;
  }

  @Override
  public String getPassedTimeValue () {
    if(this.getPassedSeconds() > 1) {
      return String.valueOf(Math.floor(this.getPassedSeconds()));
    }
    return String.valueOf(this.getPassedSeconds());
  }

  // Select End Effectors
  @Override
  public List<Option> getSelectEndeffectorOptions() {
    Log.info("Returning EndEffectorOptions", this.getClass().getName());
    return this.endEffectorOptions;
  }

  @Override
  public String getSelectEndeffectorEntry() {
    if(this.getStep() != null && this.getStep().getUsesGem().isPresent()) {
      return this.getStep().getUses().getName();
    }
    return this.endEffectorEntry;
  }

  @Override
  public void setSelectEndeffectorEntry(String value) {
    if(value == null || value.isBlank()) {
      return;
    }
    EndEffector endEffector = DemoRecManager.getEndEffectorSure(Long.valueOf(value));
    this.getStep().setUses(endEffector);
    this.endEffectorEntry = value;
  }

  @Override
  public GemTextInputValidity checkEndEffectorSet ()  {
    if(this.getStep() != null) {
      if(this.getSelectEndeffectorEntry().isBlank()) {
        return new GemTextInputValidity("Error", "Please choose an appropriate End Effector");
      } else {
        return new GemTextInputValidity("Success", null);
      }
    } else {
      return new GemTextInputValidity("Error", "Wait for Page initialization.");
    }
  }


  @Override
  public GemTextInputValidity checkTimeConsumptionSet()  {
    if(this.getStep() != null) {
      String timeEntry = this.getTimeConsumptionEntry();
      if (timeEntry == null || timeEntry.trim().isEmpty()) {
        return new GemTextInputValidity("Error", "You have to set a time");
      }
      try {
        double time = Double.parseDouble(timeEntry);
        if (time <= 0) {
          return new GemTextInputValidity("Error", "Time must be greater than 0");
        }
      } catch (NumberFormatException e) {
        return new GemTextInputValidity("Error", "Invalid time format");
      }
      return new GemTextInputValidity("Success", null);
    } else {
      return new GemTextInputValidity("Error", "Wait for Page initialization.");
    }
  }

}
