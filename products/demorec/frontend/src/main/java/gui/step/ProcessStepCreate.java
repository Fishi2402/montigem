/* (c) https://github.com/MontiCore/monticore */
package gui.step;

import de.se_rwth.commons.logging.Log;
import demorec.BatteryPack;
import demorec.DemoRecManager;
import demorec.JointConnection;
import demorec.JointConnectionBuilder;
import demorec.JointConnectionKind;
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

  public String endEffectorEntry = "";

  public String jointConnectionKindEntry = "";
  public List<Option> jointConnectionKindOptions = new ArrayList<>();

  public boolean building = false;

  private int amountOfSteps = 1;

  protected BatteryPack currentPack;

  @Override
  public void init() {
    super.init();

    this.jointConnectionKindOptions.add(new Option("NEW", "NEW"));
    List<JointConnectionKind> kindList = DemoRecManager.getJointConnectionKindList();
    for (JointConnectionKind kind : kindList) {
      this.jointConnectionKindOptions.add(new Option(String.valueOf(kind.getGemId()), kind.getName()));
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
    List<Option> selectTypeOptions = new ArrayList<>();
    selectTypeOptions.add(new Option(ProcessStepType.DECONSTRUCTION.toString(), "Deconstruction"));
    selectTypeOptions.add(new Option(ProcessStepType.OTHER.toString(),  "Other"));
    return selectTypeOptions;
  }

  @Override
  public void setSelectTypeEntry(String val) {
    super.setSelectTypeEntry(val);
    if(val.equals(ProcessStepType.DECONSTRUCTION.toString())) {
      this.setDeconstruct(true);
    } else {
      this.setDeconstruct(false);
    }
  }

  // Select Joint Connection
  @Override
  public List<Option> getSelectJointConnectionKindOptions() {
    return this.jointConnectionKindOptions;
  }

  @Override
  public String getSelectJointConnectionKindEntry() {
    if(this.getKindOfConnections() != null) {
      return String.valueOf(this.getKindOfConnections().getName());
    }
    return this.jointConnectionKindEntry;
  }

  @Override
  public void setSelectJointConnectionKindEntry(String value) {
    this.jointConnectionKindEntry = value;
    if(value == "NEW") {
      this.setNewJointConnection(true);
      this.setKindOfConnections(null);
      return;
    } else {
      this.setNewJointConnection(false);
    }
    if(value !=null) {
      Optional<JointConnectionKind> kind = DemoRecManager.jointConnectionKindOpt(Long.valueOf(value));
      if(kind.isPresent()) {
        this.setKindOfConnections(kind.get());
      } else {
        Log.error("0xDEE01: Internal error that should never occur. Please report to the DemoRec developers, when you see this message in the console.");
      }
    }
  }

  @Override
  public Consumer<Void> createNewStep() {
    return $ -> {
      JointConnection jointConnection = null;
      if (this.getStep().getType() == ProcessStepType.DECONSTRUCTION) {
        if(this.getSelectJointConnectionKindEntry() != "NEW") {
          jointConnection = DemoRecManager.jointConnectionBuilder().jointConnectionKind(
              DemoRecManager.getJointConnectionKindSure(Long.valueOf(this.jointConnectionKindEntry)))
                  .condition(State.HEALTHY)
                  .build().get();
          this.getStep().setDeconstructs(jointConnection);
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
            this.createCopyOfConnectionAndStep(jointConnection, step.get());
          }
        }
        this.setStep(DemoRecManager.processStepBuilder());
        this.setSelectTypeEntry(ProcessStepType.OTHER.toString());
        this.endEffectorEntry = "";
        this.jointConnectionKindEntry = "";
        this.building = false;
        super.init();
      } else {
        Log.error("Could not build Step.");
      }

    };
  }

  protected void createCopyOfConnectionAndStep(JointConnection connection, ProcessStep step) {
    Log.info("Creating Copies!", this.getClass().getName());
    JointConnectionBuilder builder = DemoRecManager.jointConnectionBuilder();
    Optional<JointConnection> connectionCopy = Optional.empty();
    if(connection != null) {
      connectionCopy = builder.condition(connection.getCondition())
          .jointConnectionKind(connection.getJointConnectionKind())
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

  @Override
  public Boolean disableStepCreation(ProcessStep step, JointConnectionKind kind) {
    if(step == null) {
      return true;
    }
    if(!(step.getTimeConsumption() > 0)) {
      return true;
    }
    if(step.getUsesGem() == null || !step.getUsesGem().isPresent()) {
      return true;
    }
    if(step.getType() == null || (step.getType() == ProcessStepType.OTHER && step.getDescription().isEmpty())) {
      return true;
    }
    if(step.getType() == ProcessStepType.DECONSTRUCTION && ((kind == null) || !kind.isBuildFinished())) {
      return true;
    }
    return false;
  }

  @Override
  public Consumer<JointConnectionKind> addNewJointConnectionKind() {
    return (JointConnectionKind connectionKind) -> {
      Log.warn("Adding new JointConnectionKind");
      this.jointConnectionKindOptions.add(new Option(connectionKind.getGemId()+"", connectionKind.getName()));
      this.setSelectJointConnectionKindEntry(String.valueOf(connectionKind.getGemId()));
    };
  }

  public Boolean getDeconstructionGuardCondition() {
    return true;
    //return this.inputStep === 1;
  }

  public Boolean getStepChooseJointConnectionKindCondition() {
    return true;
    //return this.inputStep === 2;
  }

  public Boolean getStepProcessStepRelatedCondition() {
    return true;
    //return this.inputStep === 3;
  }

  @Override
  public String getAmountOfConnsAndStepsEntry () {
    return this.amountOfSteps+"";
  }

  @Override
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

  @Override
  public GemTextInputValidity checkStepTypeSet()  {
    if(this.getStep().getType() != null) {
      return new GemTextInputValidity("Success", null);
    } else {
      return new GemTextInputValidity("Error", "Please choose a Step Type");
    }
  }

  @Override
  public GemTextInputValidity checkJointConnectionKind() {
    if(this.jointConnectionKindEntry != null && this.getStep().getType() != null) {
      if(this.jointConnectionKindEntry.equals("NEW")) {
        return new GemTextInputValidity("Error", "Please Save the new Connection.");
      }
      return new GemTextInputValidity("Success",null );
    } else {
      return new GemTextInputValidity("Error", "Please choose an existing Connection or create a new one.");
    }
  }

}
