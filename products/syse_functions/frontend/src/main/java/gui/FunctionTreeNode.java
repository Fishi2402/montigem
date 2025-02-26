package gui;

import functionstructure4fenix.Function;
import functionstructure4fenix.Port;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class FunctionTreeNode extends FunctionTreeNodeTOP{

  public Consumer<Void> toggleSelectedFunction(Function func){
    return (v) -> {
      List<Function> selectedFunctions = new ArrayList<>(getSelectedFunctions());
      if (getSelectedFunctions().contains(func)) {
        selectedFunctions.remove(func);
      } else {
        selectedFunctions.add(func);
      }
      setSelectedFunctions(selectedFunctions);
    };
  }

  public Consumer<Void> toggleSelectedPort(Port port){
    return (v) -> {
      List<Port> selectedPorts = new ArrayList<>(getSelectedPorts());
      if (getSelectedPorts().contains(port)) {
        selectedPorts.remove(port);
      } else {
        selectedPorts.add(port);
      }
      setSelectedPorts(selectedPorts);
    };
  }

  public boolean isFunctionSelected(Function func){
    return getSelectedFunctions().contains(func);
  }

  public boolean isPortSelected(Port port){
    return getSelectedPorts().contains(port);
  }
}
