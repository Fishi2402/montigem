package gui;

import functionstructure4fenix.Function;
import functionstructure4fenix.Port;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FunctionTreeNode extends FunctionTreeNodeTOP{

  public Consumer<Void> toggleSelectedFunction(Function function){
    return () -> {
      List<Function> selectedFunctions = new ArrayList<>(getSelectedFunctions());
      if (getSelectedFunctions().contains(function)) {
        selectedFunctions.remove(function);
      } else {
        selectedFunctions.add(function);
      }
      setSelectedFunctions(selectedFunctions);
    };
  }

  public Consumer<Void> toggleSelectedPort(Port port){
    return () -> {
      List<Port> selectedPorts = new ArrayList<>(getSelectedPorts());
      if (getSelectedPorts().contains(port)) {
        selectedPorts.remove(port);
      } else {
        selectedPorts.add(port);
      }
      setSelectedPorts(selectedPorts);
    };
  }

  public boolean isFunctionSelected(Function function){
    return getSelectedFunctions().contains(function);
  }

  public boolean isPortSelected(Port port){
    return getSelectedPorts().contains(port);
  }
}
