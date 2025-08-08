/* (c) https://github.com/MontiCore/monticore */
package gui.connectionkind;

import de.se_rwth.commons.logging.Log;
import demorec.BatteryPack;
import demorec.DemoRecComponent;
import demorec.DemoRecManager;
import demorec.JoinConnectionType;
import mc.fenix.input.gemdropdowninputtypes.Option;
import mc.fenix.input.gemtextinputtypes.GemTextInputValidity;
import demorec.service.BatteryPackService;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Component
public class JoinConnectionKindEdit extends JoinConnectionKindEditTOP {
  List<Option> joinConnectionTypeOptions = new ArrayList<>();
  List<Option> componentOptions = new ArrayList<>();
  List<Option> filteredComponentOptions = new ArrayList<>();
  String connectsComponentEntry = "";

  //currentPack : BatteryPack
  BatteryPackService packService = null;

  protected BatteryPack currentPack = null;


  public JoinConnectionKindEdit(BatteryPackService service) {
    super();
    this.packService = service;
  }

  public void init() {
    // Initialize joinConnectionTypeOptions
    this.joinConnectionTypeOptions.add(new Option(JoinConnectionType.SCREW.toString(), "Screw"));
    this.joinConnectionTypeOptions.add(new Option(JoinConnectionType.GLUE.toString(), "Glue"));
    this.joinConnectionTypeOptions.add(new Option(JoinConnectionType.WELDING.toString(), "Welding"));
    this.joinConnectionTypeOptions.add(new Option(JoinConnectionType.PLUG.toString(), "Plug"));
    this.joinConnectionTypeOptions.add(new Option(JoinConnectionType.ELECTRICAL.toString(), "Electrical"));
    this.joinConnectionTypeOptions.add(new Option(JoinConnectionType.CLIP.toString(), "Clip"));
    this.joinConnectionTypeOptions.add(new Option(JoinConnectionType.RIVET.toString(), "Rivet"));
    this.joinConnectionTypeOptions.add(new Option(JoinConnectionType.UNDEFINED.toString(), "Undefined"));

    super.init();
    this.packService.addObserverFunction(this.getUpdaterFunction());
  }

  public List<Option> getComponentOptions(List<DemoRecComponent> components) {
    List<Option> options = new ArrayList<>();
    // Hack for sorting and no prepend Functionality available in JSweet
    Option newOption = new Option("NEW", "AAAAAAAAA_NEW");
    options.add(newOption);
    for (DemoRecComponent comp : components) {
      BatteryPack curPack = this.packService.getBatteryPack();
      if(!(comp instanceof BatteryPack) && comp.getBatteryPack().getGemId() == curPack.getGemId()) {
        long gemId = comp.getGemId();
        String display = comp.getName();
        options.add(new Option(String.valueOf(gemId), display));
      }
    }
    options.sort((a,b) -> a.getDisplay().compareTo(b.getDisplay()));
    // Haha what a genius hack
    newOption.setDisplay("NEW");
    return options;
  }

  public Consumer<BatteryPack> getUpdaterFunction() {
    return (BatteryPack p) -> {
      this.currentPack = p;
      this.filteredComponentOptions = new ArrayList<>();
      for (DemoRecComponent comp : p.getAllComponentsList()) {
        this.filteredComponentOptions.add(new Option(comp.getGemId()+"", comp.getName()));
      }
    };
  }

  private Set<String> getCurrentPackComponents() {
    if(this.currentPack != null) {
      Set<String> gemIds = new HashSet<>();
      //return new HashSet<>(this.currentPack.getAllComponentsList().map(comp => comp.getGemId()+""))
      return gemIds;
    } else {
      return new HashSet<>();
    }
  }

  // Select Type of connection
  public List<Option> getSelectTypeOfConnectionOptions () {
    Log.info("Slect Type Connection Options:"+this.joinConnectionTypeOptions, this.getClass().getName());
    return this.joinConnectionTypeOptions;
  }

  public String getSelectTypeOfConnectionEntry () {
    if(this.getConnectionKind() != null) {
      return this.getConnectionKind().getJoinConnectionType().toString();
    }
    // Default is Undefined
    return JoinConnectionType.UNDEFINED.toString();
  }

  // Select Connected Components
/*  public getAddComponentDisabled(): boolean {
    return (this.getSelectConnectsComponentEntry() === newOption.getValue())
  }*/

  public String getSelectConnectsComponentEntry() {
    if(this.connectsComponentEntry.equals("NEW")) {
      return "NEW";
    } else {
      if(this.getConnectionKind() != null && this.getConnectionKind().sizeConnectss() > 0) {
        return this.getConnectionKind().getConnects(this.getConnectionKind().sizeConnectss()-1).getName();
      }
    }
    return "";
  }

  public void setSelectConnectsComponentEntry(String value) {

    if(value == "NEW") {
      this.setNewCompGuard(true);
    } else {
      this.getConnectionKind().addConnects(
          DemoRecManager.getDemoRecComponentSure(Long.valueOf(value)));
      this.connectsComponentEntry = "";
      this.setNewCompGuard(false);
    }
  }

  public Consumer<DemoRecComponent> addNewComponent() {
    //TODO needed when everything is synchronized since Jsweet?
    return (DemoRecComponent comp) -> {
      Log.warn("Adding DemoRecComponent option.");
      this.componentOptions.add(new Option(comp.getGemId()+"", comp.getName()));
      this.setSelectConnectsComponentEntry(comp.getGemId()+"");
      this.getConnectionKind().addConnects(comp);
      this.setNewCompGuard(false);
    };
  }

  public Consumer<Void> removeSelectedComponent(DemoRecComponent componentToRemove) {
    return $ -> {
      this.getConnectionKind().removeConnects(componentToRemove);
    };
  }

  public Boolean getNewCompGuardCondition() {
    return this.getSelectConnectsComponentEntry().equals("NEW");
  }

  public GemTextInputValidity checkConnectionType() {
    if(this.getConnectionKind() != null) {
      if(this.getConnectionKind().getJoinConnectionType() != null) {
        return new GemTextInputValidity("Success", null);
      }
      return new GemTextInputValidity("Error", "Please choose a connection type.");
    }
    return new GemTextInputValidity("Warning", "Wait for page init.");
  }

  public GemTextInputValidity checkChosenComponents() {
    if(this.getConnectionKind() != null) {
      if(this.getConnectionKind().getConnectsList().size() >= 2) {
        return new GemTextInputValidity("Success", null);
      }
      return new GemTextInputValidity("Warning", "Later on, you should specify at least 2 connected components.");
    }
    return new GemTextInputValidity("Warning", "Wait for page init.");
  }

}
