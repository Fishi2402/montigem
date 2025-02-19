package gui;

import java.util.List;
import java.util.function.Consumer;
import functionstructure4fenix.Port;
import java.util.ArrayList;

public class PortTreeNode extends PortTreeNodeTOP{

  public Consumer<Void> toggleSelectedPort(){
    return () -> {
      List<Port> selectedPorts = new ArrayList<>(getSelectedPorts());
      if (getSelectedPorts().contains(getPort())) {
        selectedPorts.remove(getPort());
      } else {
        selectedPorts.add(getPort());
      }
      setSelectedPorts(selectedPorts);
    };
  }

  public boolean isPortSelected(){
    return getSelectedPorts().contains(getPort());
  }
}
