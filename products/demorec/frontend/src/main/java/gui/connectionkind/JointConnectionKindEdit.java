/* (c) https://github.com/MontiCore/monticore */
package gui.connectionkind;

import de.se_rwth.commons.logging.Log;
import demorec.BatteryPack;
import demorec.DemoRecComponent;
import demorec.DemoRecManager;
import demorec.JointConnectionType;
import mc.fenix.input.gemdropdowninputtypes.Option;
import mc.fenix.input.gemtextinputtypes.GemTextInputValidity;
import demorec.service.BatteryPackService;
import umlp.guiutil.AngularUtils;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Component
public class JointConnectionKindEdit extends JointConnectionKindEditTOP {
  List<Option> jointConnectionTypeOptions = new ArrayList<>();
  List<Option> componentOptions = new ArrayList<>();
  String connectsComponentEntry = "";

  //currentPack : BatteryPack
  BatteryPackService packService = AngularUtils.inject(BatteryPackService.class);

  protected BatteryPack currentPack = null;


  public JointConnectionKindEdit() {
    super();
  }

  public void init() {
    // Initialize jointConnectionTypeOptions
    this.jointConnectionTypeOptions.add(new Option(JointConnectionType.SCREW.toString(), "Screw"));
    this.jointConnectionTypeOptions.add(new Option(JointConnectionType.GLUE.toString(), "Glue"));
    this.jointConnectionTypeOptions.add(new Option(JointConnectionType.WELDING.toString(), "Welding"));
    this.jointConnectionTypeOptions.add(new Option(JointConnectionType.PLUG.toString(), "Plug"));
    this.jointConnectionTypeOptions.add(new Option(JointConnectionType.ELECTRICAL.toString(), "Electrical"));
    this.jointConnectionTypeOptions.add(new Option(JointConnectionType.CLIP.toString(), "Clip"));
    this.jointConnectionTypeOptions.add(new Option(JointConnectionType.RIVET.toString(), "Rivet"));
    this.jointConnectionTypeOptions.add(new Option(JointConnectionType.UNDEFINED.toString(), "Undefined"));

    this.packService.addObserverFunction(this.getUpdaterFunction());
    super.init();
  }

  public List<Option> getComponentOptions(List<DemoRecComponent> components) {
    List<Option> options = new ArrayList<>();
    if(this.currentPack == null) {
      return options;
    }
    // Hack for sorting and no prepend Functionality available in JSweet
    Option newOption = new Option("NEW", "AAAAAAAAA_NEW");
    options.add(newOption);
    for (DemoRecComponent comp : components) {
      if(!(comp instanceof BatteryPack) && comp.getBatteryPack().getGemId() == currentPack.getGemId()) {
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
      if(p == null) {
        return;
      }
      this.currentPack = p;
      // abuse this for update mechanism
      this.setComponents(this.getComponents());
    };
  }

  // Select Type of connection
  public List<Option> getSelectTypeOfConnectionOptions () {
    // TODO fix implicit .toString on Lists / IUMLPLists
    //Log.info("Select Type Connection Options:"+this.jointConnectionTypeOptions, this.getClass().getName());
    return this.jointConnectionTypeOptions;
  }

  public String getSelectTypeOfConnectionEntry () {
    if(this.getConnectionKind() != null) {
      return this.getConnectionKind().getJointConnectionType().toString();
    }
    // Default is Undefined
    return JointConnectionType.UNDEFINED.toString();
  }

  // Select Connected Components
/*  public getAddComponentDisabled(): boolean {
    return (this.getSelectConnectsComponentEntry() === newOption.getValue())
  }*/

  @Override
  public void setSelectConnectsComponentEntry(String value) {

    if(value == "NEW") {
      this.setNewCompGuard(true);
    } else {
      if(!value.isBlank()) {
        this.getConnectionKind().addConnects(
            DemoRecManager.getDemoRecComponentSure(Long.valueOf(value)));
        this.connectsComponentEntry = "";
      }
      this.setNewCompGuard(false);
    }
    super.setSelectConnectsComponentEntry(value);
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
      if(this.getConnectionKind().getJointConnectionType() != null) {
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
