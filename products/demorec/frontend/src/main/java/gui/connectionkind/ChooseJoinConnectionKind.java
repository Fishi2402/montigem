/* (c) https://github.com/MontiCore/monticore */
package gui.connectionkind;

import demorec.DemoRecManager;
import demorec.JoinConnectionKind;
import mc.fenix.input.gemdropdowninputtypes.Option;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class ChooseJoinConnectionKind extends ChooseJoinConnectionKindTOP {

  private String connectionKindEntry = "";
  private List<Option> connectionKindOptions = new ArrayList<>();
  private boolean chosenKindNotLoaded = true;

  @Override
  public void init() {
    super.init();
    this.connectionKindOptions.add(new Option("NEW","NEW"));

    for (JoinConnectionKind jCKind : DemoRecManager.getJoinConnectionKindList()) {
      this.connectionKindOptions.add(new Option(String.valueOf(jCKind.getGemId()), jCKind.getName()));
    }
  }

  public List<Option> getSelectJoinConnectionKindOptions() {
    return this.connectionKindOptions;
  }

  public String getSelectJoinConnectionKindEntry() {
    // This logic ensure that the type is chosen if the currentType is only loaded much later
/*    if(this.getCurrentKind() && this.chosenKindNotLoaded) {
      this.chosenKindNotLoaded = false;
      this.connectionKindEntry = this.getCurrentKind().getGemId()+"";
    }*/
    return this.connectionKindEntry;
  }

  public void setSelectJoinConnectionKindEntry(String value) {
    this.connectionKindEntry = value;
    JoinConnectionKind k = DemoRecManager.getJoinConnectionKindSure(Long.valueOf(value));
    this.getChosenKind().accept(k);
  }

  public Boolean getCreateJoinConnectionKindCondition() {
    return this.getSelectJoinConnectionKindEntry().equals("NEW");
  }

  public Consumer<JoinConnectionKind> addNewJoinConnectionKind() {
    return (JoinConnectionKind newConnectionKind) -> {
      this.connectionKindOptions.add(new Option(String.valueOf(newConnectionKind.getGemId()), newConnectionKind.getName()));
    };
  }

}
