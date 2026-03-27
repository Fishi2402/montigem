/* (c) https://github.com/MontiCore/monticore */
package gui.step;

import demorec.DemoRecManager;
import demorec.JointConnection;
import demorec.JointConnectionKind;
import demorec.ProcessStepType;
import mc.fenix.input.gemdropdowninputtypes.Option;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class ProcessStepEdit extends ProcessStepEditTOP {
  public String jointConnectionEntry = "";
  public List<Option> jointConnectionOptions = new ArrayList<>();
  public boolean addJointConnectionClicked = false;

  @Override
  public void init(){
    super.init();
    // Initialize connection kind options
    for (JointConnectionKind kind : this.getP().getDeconstructionOf().getAllConnectionsList()) {
      this.jointConnectionOptions.add(new Option(kind.getGemId()+"", kind.getName()));
    }
  }

  // Select Types
  @Override
  public List<Option> getSelectTypeOptions() {
    List<Option> selectTypeOptions = new ArrayList<>();
    for(ProcessStepType type : ProcessStepType.values()) {
      String strType = type.toString();
      selectTypeOptions.add(new Option(strType, strType));
    }
    return selectTypeOptions;
  }

  @Override
  public void setSelectTypeEntry(String value) {

    if(value.equals(ProcessStepType.DECONSTRUCTION.toString())) {
      this.getStep().setType(ProcessStepType.DECONSTRUCTION);
    } else {
      this.getStep().setType(ProcessStepType.OTHER);
      this.getStep().setDeconstructsAbsent();
    }
  }

  @Override
  public Consumer<Void> editJointConnection() {
    return $ -> Router.navigate("gui/jointconnection/JointConnectionEditPage/"+this.getStep().getDeconstructs().getGemId());
  }

  @Override
  public List<Option> getSelectJointConnectionOptions(){
    return this.jointConnectionOptions;
  }

  @Override
  public List<Option>  getSelectJointConnection1Options(){
    return this.getSelectJointConnectionOptions();
  }

  @Override
  public String getSelectJointConnectionEntry()  {
    if (!this.jointConnectionEntry.equals("")) {
      return this.jointConnectionEntry;
    }
    else {
      this.jointConnectionEntry = this.getStep().getDeconstructs().getGemId() + "";
      return this.jointConnectionEntry;
    }
  }

  @Override
  public String getSelectJointConnection1Entry() {
    if (this.getStep().getDeconstructs() != null) {
      return "NONE";
    }
    else {
      return this.getSelectJointConnectionEntry();
    }
  }

  @Override
  public void setSelectJointConnectionEntry(String value) {
    this.jointConnectionEntry = value;

    if (this.getSelectJointConnectionEntry() != null) {
      if (this.getSelectJointConnectionEntry().equals("NONE")) {
        this.getStep().setDeconstructsAbsent();
      }
      else if (!this.getSelectJointConnectionEntry().equals("NEW")) {
        this.getStep().setDeconstructs(DemoRecManager.getJointConnectionSure(Long.valueOf(this.getSelectJointConnectionEntry())));
      }
    }
  }

  @Override
  public void setSelectJointConnection1Entry(String value) {
    this.setSelectJointConnectionEntry(value);
  }

  @Override
  public Boolean getCreateNewJointConnectionCondition() {
    return (this.getSelectJointConnectionEntry().equals("NEW"));
  }

  @Override
  public Boolean getCreateNewJointConnection1Condition() {
    return this.getCreateNewJointConnectionCondition();
  }

  @Override
  public Boolean getIsDeconstructionCondition() {
    return this.getSelectTypeEntry().equals(ProcessStepType.DECONSTRUCTION.toString());
  }

  @Override
  public Boolean getHasJointConnectionCondition() {
    return this.getStep().getDeconstructs() != null;
  }

  private Option calculateDisplayValueOfJointConnection(JointConnection connection) {
    return new Option(connection.getGemId() + "", connection.getJointConnectionKind().getName());
  }

  @Override
  public Boolean getAddJointConnectionClickedCondition() {
    return this.addJointConnectionClicked;
  }

  @Override
  public Consumer<Void> addJointConnection() {
    return $ -> this.addJointConnectionClicked = true;
  }

  @Override
  public Consumer<JointConnection> addNewJointConnection() {
    return (JointConnection connection) -> {
      this.jointConnectionOptions.add(calculateDisplayValueOfJointConnection(connection));
      this.setSelectJointConnectionEntry(String.valueOf(connection.getGemId()));
    };
  }

  @Override
  public Boolean getAddJointConnectionButtonDisabled(){
    return this.getAddJointConnectionClickedCondition();
  }

  @Override
  public Boolean getEditJointConnectionButtonDisabled() {
    return ((this.getSelectJointConnectionEntry().equals("NONE") || (this.getSelectJointConnectionEntry().equals("NEW"))));
  }

  @Override
  public Boolean getEditJointConnectionButton1Disabled()  {
    return this.getEditJointConnectionButtonDisabled();
  }

  @Override
  public Consumer<Void> backToProcessOverview() {
    return $ -> Router.navigate("gui/process/ProcessOverview/"+this.getP().getGemId());
  }

}
