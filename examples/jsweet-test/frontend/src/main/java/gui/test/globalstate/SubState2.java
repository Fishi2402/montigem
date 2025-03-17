/* (c) https://github.com/MontiCore/monticore */
package gui.test.globalstate;

import jsweettest.JsweettestManager;
import jsweettest.State;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class SubState2 extends SubState2TOP {

  @Override
  public Consumer<Void> changeState() {
    return Void -> {
      if (this.getGs().getState().getName().equals("State A")) {
        this.getGs().setState(getNewState("State B"));
      } else {
        this.getGs().setState(getNewState("State A"));
      }
    };
  }

  private State getNewState(String stateName) {
    State newState = null;
    List<State> list = new ArrayList<>();
    for (State state : JsweettestManager.getStateList()) {
      if (state.getName().equals(stateName)) {
        newState = state;
      }
    }
    if (list.isEmpty()) {
      newState = JsweettestManager.stateBuilder().name(stateName).build().get();
    }
    return newState;
  }
}
