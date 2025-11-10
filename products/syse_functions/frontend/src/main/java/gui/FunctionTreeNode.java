package gui;

import functionstructure4fenix.Function;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FunctionTreeNode extends FunctionTreeNodeTOP{

  @Override
  public Boolean getFunctionSelectionChecked(){
    return getSelectedFunctions().contains(getFunc());
  }

  @Override
  public void setFunctionSelectionChecked(Boolean selected){
    List<Function> selectedFunctions = new ArrayList<>(getSelectedFunctions());
    if (selected) {
      selectedFunctions.add(getFunc());
    } else {
      selectedFunctions.remove(getFunc());
    }
    setSelectedFunctions(selectedFunctions);
  }
}
