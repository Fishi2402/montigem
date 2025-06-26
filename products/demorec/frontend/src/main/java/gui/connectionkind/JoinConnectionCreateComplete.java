/* (c) https://github.com/MontiCore/monticore */
package gui.connectionkind;

import demorec.DemoRecManager;
import demorec.JoinConnectionKind;
import mc.fenix.input.gemdropdowninputtypes.Option;
import mc.fenix.input.gemtextinputtypes.GemTextInputValidity;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class JoinConnectionCreateComplete extends JoinConnectionCreateCompleteTOP {
  @Override
  public GemTextInputValidity checkJoinConnectionKind() {
    String selectedKind = this.getSelectJoinConnectionKindEntry();
    if (selectedKind == null || selectedKind.trim().isEmpty()) {
      return new GemTextInputValidity("Error", "Please select a join connection kind.");
    }
    return new GemTextInputValidity("Success", null);
  }
}
