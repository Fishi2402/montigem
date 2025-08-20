package gui.component;

import de.se_rwth.commons.logging.Log;
import demorec.ComponentKind;
import demorec.DemoRecComponent;
import demorec.Photo;
import demorec.State;
import mc.fenix.input.gemdropdowninputtypes.Option;
import mc.fenix.input.gemtextinputtypes.GemTextInputValidity;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class ComponentEdit extends ComponentEditTOP {

  @Override
  public Consumer<Photo> addPhoto(DemoRecComponent component) {
    return (Photo p) ->  {
      component.addPhoto(p);
    };
  }

  @Override
  public Consumer<Void> editComponentKind() {
    return $ -> {
      Router.navigate("gui", "kind","EditComponentKindPage",
          String.valueOf(this.getComponent().getIsA().getGemId()));
    };
  }

  @Override
  public Consumer<ComponentKind> changeToKind() {
    return (kind) -> {
      Log.warn("Changing kind" + kind.getName());
      this.getComponent().setIsA(kind);
    };
  }

  // Select Condition Options
  @Override
  public List<Option> getConditionOpts() {
    List<Option> conditionOptions =new ArrayList<>();
    conditionOptions.add(new Option(State.HEALTHY.toString(),"Healthy"));
    conditionOptions.add(new Option(State.OTHER.toString(),"Other"));
    return conditionOptions;
  }


  @Override
  public GemTextInputValidity checkComponentName() {
    if (this.getComponent() == null) {
      return new GemTextInputValidity("Error", "Component not initialized");
    }
    String name = this.getComponent().getName();
    if (name == null || name.trim().isEmpty()) {
      return new GemTextInputValidity("Error", "Component Name must not be empty");
    }
    return new GemTextInputValidity("Success", null);
  }

}
