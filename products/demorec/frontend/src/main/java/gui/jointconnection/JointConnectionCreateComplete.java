/* (c) https://github.com/MontiCore/monticore */
package gui.jointconnection;

import mc.fenix.input.gemtextinputtypes.GemTextInputValidity;
import umlp.jsweet.extension.annotation.Component;

@Component
public class JointConnectionCreateComplete extends JointConnectionCreateCompleteTOP {
  @Override
  public GemTextInputValidity checkJointConnectionKind() {
    String selectedKind = this.getSelectJointConnectionKindEntry();
    if (selectedKind == null || selectedKind.trim().isEmpty()) {
      return new GemTextInputValidity("Error", "Please select a Joint connection kind.");
    }
    return new GemTextInputValidity("Success", null);
  }
}
