/* (c) https://github.com/MontiCore/monticore */
package gui.step;

import demorec.DemoRecManager;
import demorec.JoinConnection;
import demorec.JoinConnectionKind;
import demorec.ProcessStepType;
import mc.fenix.input.gemdropdowninputtypes.Option;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class ProcessStepEdit extends ProcessStepEditTOP {
  public String joinConnectionEntry = "";
  public List<Option> joinConnectionOptions = new ArrayList<>();
  public boolean addJoinConnectionClicked = false;

  @Override
  public void init(){
    super.init();
    // Initialize connection kind options
    for (JoinConnectionKind kind : this.getP().getDeconstructionOf().getAllConnectionsList()) {
      this.joinConnectionOptions.add(new Option(kind.getGemId()+"", kind.getName()));
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
  public Consumer<Void> editJoinConnection() {
    return $ -> Router.navigate("gui/joinconnection/JoinConnectionEditPage/"+this.getStep().getDeconstructs().getGemId());
  }

  @Override
  public List<Option> getSelectJoinConnectionOptions(){
    return this.joinConnectionOptions;
  }

  @Override
  public List<Option>  getSelectJoinConnection1Options(){
    return this.getSelectJoinConnectionOptions();
  }

  @Override
  public String getSelectJoinConnectionEntry()  {
    if (!this.joinConnectionEntry.equals("")) {
      return this.joinConnectionEntry;
    }
    else {
      this.joinConnectionEntry = this.getStep().getDeconstructs().getGemId() + "";
      return this.joinConnectionEntry;
    }
  }

  @Override
  public String getSelectJoinConnection1Entry() {
    if (this.getStep().getDeconstructs() != null) {
      return "NONE";
    }
    else {
      return this.getSelectJoinConnectionEntry();
    }
  }

  @Override
  public void setSelectJoinConnectionEntry(String value) {
    this.joinConnectionEntry = value;

    if (this.getSelectJoinConnectionEntry() != null) {
      if (this.getSelectJoinConnectionEntry().equals("NONE")) {
        this.getStep().setDeconstructsAbsent();
      }
      else if (!this.getSelectJoinConnectionEntry().equals("NEW")) {
        this.getStep().setDeconstructs(DemoRecManager.getJoinConnectionSure(Long.valueOf(this.getSelectJoinConnectionEntry())));
      }
    }
  }

  @Override
  public void setSelectJoinConnection1Entry(String value) {
    this.setSelectJoinConnectionEntry(value);
  }

  @Override
  public Boolean getCreateNewJoinConnectionCondition() {
    return (this.getSelectJoinConnectionEntry().equals("NEW"));
  }

  @Override
  public Boolean getCreateNewJoinConnection1Condition() {
    return this.getCreateNewJoinConnectionCondition();
  }

  @Override
  public Boolean getIsDeconstructionCondition() {
    return this.getSelectTypeEntry().equals(ProcessStepType.DECONSTRUCTION.toString());
  }

  @Override
  public Boolean getHasJoinConnectionCondition() {
    return this.getStep().getDeconstructs() != null;
  }

  private Option calculateDisplayValueOfJoinConnection(JoinConnection connection) {
    return new Option(connection.getGemId() + "", connection.getJoinConnectionKind().getName());
  }

  @Override
  public Boolean getAddJoinConnectionClickedCondition() {
    return this.addJoinConnectionClicked;
  }

  @Override
  public Consumer<Void> addJoinConnection() {
    return $ -> this.addJoinConnectionClicked = true;
  }

  @Override
  public Consumer<JoinConnection> addNewJoinConnection() {
    return (JoinConnection connection) -> {
      this.joinConnectionOptions.add(calculateDisplayValueOfJoinConnection(connection));
      this.setSelectJoinConnectionEntry(String.valueOf(connection.getGemId()));
    };
  }

  @Override
  public Boolean getAddJoinConnectionButtonDisabled(){
    return this.getAddJoinConnectionClickedCondition();
  }

  @Override
  public Boolean getEditJoinConnectionButtonDisabled() {
    return ((this.getSelectJoinConnectionEntry().equals("NONE") || (this.getSelectJoinConnectionEntry().equals("NEW"))));
  }

  @Override
  public Boolean getEditJoinConnectionButton1Disabled()  {
    return this.getEditJoinConnectionButtonDisabled();
  }

  @Override
  public Consumer<Void> backToProcessOverview() {
    return $ -> Router.navigate("gui/process/ProcessOverview/"+this.getP().getGemId());
  }

}
