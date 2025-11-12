package gui;

import java.util.List;
import java.util.function.Consumer;
import functionstructure4fenix.Port;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;

@Component
public class PortTreeNode extends PortTreeNodeTOP{

  public void setPortSelectionChecked(Boolean selected){
    List<Port> selectedPorts = new ArrayList<>(getSelectedPorts());
    if (selected) {
      selectedPorts.remove(getPort());
    } else {
      selectedPorts.add(getPort());
    }
    setSelectedPorts(selectedPorts);
  }

  @Override
  public Boolean getPortSelectionChecked(){
    return getSelectedPorts().contains(getPort());
  }
}
