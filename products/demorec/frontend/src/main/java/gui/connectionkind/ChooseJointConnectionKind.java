/* (c) https://github.com/MontiCore/monticore */
package gui.connectionkind;

import demorec.DemoRecManager;
import demorec.JointConnectionKind;
import mc.fenix.input.gemdropdowninputtypes.Option;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class ChooseJointConnectionKind extends ChooseJointConnectionKindTOP {

  private String connectionKindEntry = "";
  private List<Option> connectionKindOptions = new ArrayList<>();
  private boolean chosenKindNotLoaded = true;

  @Override
  public void init() {
    super.init();
    this.connectionKindOptions.add(new Option("NEW","NEW"));

    for (JointConnectionKind jCKind : DemoRecManager.getJointConnectionKindList()) {
      this.connectionKindOptions.add(new Option(String.valueOf(jCKind.getGemId()), jCKind.getName()));
    }
  }

  public List<Option> getSelectJointConnectionKindOptions() {
    return this.connectionKindOptions;
  }

  public String getSelectJointConnectionKindEntry() {
    // This logic ensure that the type is chosen if the currentType is only loaded much later
/*    if(this.getCurrentKind() && this.chosenKindNotLoaded) {
      this.chosenKindNotLoaded = false;
      this.connectionKindEntry = this.getCurrentKind().getGemId()+"";
    }*/
    return this.connectionKindEntry;
  }

  public void setSelectJointConnectionKindEntry(String value) {
    this.connectionKindEntry = value;
    JointConnectionKind k = DemoRecManager.getJointConnectionKindSure(Long.valueOf(value));
    this.getChosenKind().accept(k);
  }

  public Boolean getCreateJointConnectionKindCondition() {
    return this.getSelectJointConnectionKindEntry().equals("NEW");
  }

  public Consumer<JointConnectionKind> addNewJointConnectionKind() {
    return (JointConnectionKind newConnectionKind) -> {
      this.connectionKindOptions.add(new Option(String.valueOf(newConnectionKind.getGemId()), newConnectionKind.getName()));
    };
  }

}
