/* (c) https://github.com/MontiCore/monticore */
package gui.step;

import de.se_rwth.commons.logging.Log;
import demorec.BatteryPack;
import demorec.DemoRecManager;
import demorec.JoinConnection;
import demorec.JoinConnectionBuilder;
import demorec.JoinConnectionKind;
import demorec.ProcessStep;
import demorec.ProcessStepBuilder;
import demorec.ProcessStepType;
import demorec.State;
import mc.fenix.input.gemdropdowninputtypes.Option;
import mc.fenix.input.gemtextinputtypes.GemTextInputValidity;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class ProcessStepCreate extends ProcessStepCreateTOP {

  public String typeEntry = String.valueOf(ProcessStepType.OTHER.ordinal());
  public List<Option> selectTypeOptions = new ArrayList<>();

  public String endEffectorEntry = "";

  public String joinConnectionKindEntry = "";
  public List<Option> joinConnectionKindOptions = new ArrayList<>();

  public boolean building = false;

  private int amountOfSteps = 1;

  protected BatteryPack currentPack;

  @Override
  public void init() {
    super.init();

    this.selectTypeOptions.add(new Option(String.valueOf(ProcessStepType.DECONSTRUCTION.ordinal()), "Deconstruction"));
    this.selectTypeOptions.add(new Option(String.valueOf(ProcessStepType.OTHER.ordinal()),  "Other"));

    this.joinConnectionKindOptions.add(new Option("NEW", "NEW"));
    List<JoinConnectionKind> kindList = DemoRecManager.getJoinConnectionKindList();
    for (JoinConnectionKind kind : kindList) {
      this.joinConnectionKindOptions.add(new Option(String.valueOf(kind.getGemId()), kind.getName()));
    }
  }

  // We do not want a builder object here
  @Override
  protected void initKindOfConnections () {
    this._kindOfConnections = null;
  }

  // Select Types
  @Override
  public List<Option> getSelectTypeOptions() {
    return this.selectTypeOptions;
  }

  @Override
  public String getSelectTypeEntry() {
    return this.typeEntry;
  }

  @Override
  public void setSelectTypeEntry(String value) {
    this.typeEntry = value;
    this.setDeconstruct(Integer.valueOf(value) == ProcessStepType.DECONSTRUCTION.ordinal());
    Log.info("Deconstruction value = "+this.isDeconstruct(), this.getClass().getName());
  }

  // Select Join Connection
  public List<Option> getSelectJoinConnectionKindOptions() {
    return this.joinConnectionKindOptions;
  }

  public String getSelectJoinConnectionKindEntry() {
    if(this.getKindOfConnections() != null) {
      return String.valueOf(this.getKindOfConnections().getName());
    }
    return this.joinConnectionKindEntry;
  }

  public void setSelectJoinConnectionKindEntry(String value) {
    this.joinConnectionKindEntry = value;
    if(value == "NEW") {
      this.setNewJoinConnection(true);
      this.setKindOfConnections(null);
      return;
    } else {
      this.setNewJoinConnection(false);
    }
    if(value !=null) {
      Optional<JoinConnectionKind> kind = DemoRecManager.joinConnectionKindOpt(Long.valueOf(value));
      if(kind.isPresent()) {
        this.setKindOfConnections(kind.get());
      }
    }
  }

  public Consumer<Void> createNewStep() {
    return $ -> {
      JoinConnection joinConnection = null;
      if (Integer.valueOf(this.getSelectTypeEntry()) == ProcessStepType.DECONSTRUCTION.ordinal()) {
        this.getStep().setType(ProcessStepType.DECONSTRUCTION);
        if(this.getSelectJoinConnectionKindEntry() != "NEW") {
          joinConnection = DemoRecManager.joinConnectionBuilder().joinConnectionKind(
              DemoRecManager.getJoinConnectionKindSure(Long.valueOf(this.joinConnectionKindEntry)))
                  .condition(State.HEALTHY)
                  .build().get();
          this.getStep().setDeconstructs(joinConnection);
        }
      } else {
        this.getStep().setType(ProcessStepType.OTHER);
      }
      // Will be set in ProcessOverview
      this.getStep().setIndex(0);
      this.building = true;
      var step = this.getStep().build();
      if(step.isPresent()) {
        this.getCreate().accept(step.get());
        //this._create.accept(step.get());
        if(this.amountOfSteps > 1) {
          for(int i = 0; i < this.amountOfSteps-1; i++) {
            this.createCopyOfConnectionAndStep(joinConnection, step.get());
          }
        }
        this.setStep(DemoRecManager.processStepBuilder());
        this.setSelectTypeEntry(String.valueOf(ProcessStepType.OTHER.ordinal()));
        this.endEffectorEntry = "";
        this.joinConnectionKindEntry = "";
        this.building = false;
        super.init();
      } else {
        Log.error("Could not build Step.");
      }

    };
  }

  protected void createCopyOfConnectionAndStep(JoinConnection connection, ProcessStep step) {
    Log.info("Creating Copies!", this.getClass().getName());
    JoinConnectionBuilder builder = DemoRecManager.joinConnectionBuilder();
    Optional<JoinConnection> connectionCopy = Optional.empty();
    if(connection != null) {
      connectionCopy = builder.condition(connection.getCondition())
          .joinConnectionKind(connection.getJoinConnectionKind())
          .xPos(connection.getXPos())
          .yPos(connection.getYPos())
          .zPos(connection.getZPos())
          .conditionDescription(connection.getConditionDescription())
          .build();
    }

    ProcessStepBuilder processStepBuilder = DemoRecManager.processStepBuilder();
    processStepBuilder.uses(step.getUses())
        .timeConsumption(step.getTimeConsumption())
        .type(step.getType())
        .index(0);
    if(connectionCopy.isPresent()) {
      processStepBuilder.deconstructs(connectionCopy.get());
    }
    Optional<ProcessStep> stepOptional = processStepBuilder.build();
    if(stepOptional.isPresent()) {
      this.getCreate().accept(stepOptional.get());
    }
  }

  public Boolean getBuildStepDisabled() {
    if (this.getStep() == null || this.building) {
      return true;
    }
    return false;
  }

  public Consumer<JoinConnectionKind> addNewJoinConnectionKind() {
    return (JoinConnectionKind connectionKind) -> {
      Log.warn("Adding new JointConnectionKind");
      this.joinConnectionKindOptions.add(new Option(connectionKind.getGemId()+"", connectionKind.getName()));
      this.setSelectJoinConnectionKindEntry(String.valueOf(connectionKind.getGemId()));
    };
  }

  public Boolean getDeconstructionGuardCondition() {
    return true;
    //return this.inputStep === 1;
  }

  public Boolean getStepChooseJoinConnectionKindCondition() {
    return true;
    //return this.inputStep === 2;
  }

  public Boolean getStepProcessStepRelatedCondition() {
    return true;
    //return this.inputStep === 3;
  }

  public String getAmountOfConnsAndStepsEntry () {
    return this.amountOfSteps+"";
  }

  public void setAmountOfConnsAndStepsEntry (String amount) {
    this.amountOfSteps = Integer.valueOf(amount);
  }

  // Validity Functions
  public GemTextInputValidity checkTimeConsumptionSet () {
    if(this.getStep() != null) {
      if(this.getStep().getTimeConsumption() == null) {
        return new GemTextInputValidity("Error", "You have to set a time");
      } else {
        return new GemTextInputValidity("Success", null);
      }
    } else {
      return new GemTextInputValidity("Error", "Wait for Page initialization.");
    }
  }

  public GemTextInputValidity checkStepTypeSet()  {
    if(this.typeEntry != null) {
      return new GemTextInputValidity("Success", null);
    } else {
      return new GemTextInputValidity("Error", "Please choose a Step Type");
    }
  }

  public GemTextInputValidity checkJoinConnectionKind() {
    if(this.joinConnectionKindEntry != null) {
      if(this.joinConnectionKindEntry.equals("NEW")) {
        return new GemTextInputValidity("Error", "Please Save the new Connection.");
      }
      return new GemTextInputValidity("Success",null );
    } else {
      return new GemTextInputValidity("Error", "Please choose an existing Connection or create a new one.");
    }
  }

}
