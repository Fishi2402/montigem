package gui.kind;

import demorec.ComponentKind;
import mc.fenix.input.gemtextinputtypes.GemTextInputValidity;
import service.BatteryPackService;
import umlp.jsweet.extension.annotation.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public class EditComponentKind extends EditComponentKindTOP {

  @Override
  public GemTextInputValidity checkKindName() {
    if (this.getComponentKind() == null) {
      return new GemTextInputValidity("Error", "Component kind not initialized");
    }
    String name = this.getComponentKind().getName();
    if (name == null || name.trim().isEmpty()) {
      return new GemTextInputValidity("Error", "Component Kind Name must not be empty");
    }
    return new GemTextInputValidity("Success", null);
  }
}
