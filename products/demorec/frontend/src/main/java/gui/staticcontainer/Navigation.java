package gui.staticcontainer;

import umlp.guiutil.Router;
import java.util.function.Consumer;

public class Navigation extends NavigationTOP {

  public Consumer<Void> navigateToProcesses() {
    return event -> Router.navigate("gui", "Start");
  }

  public Consumer<Void> navigateToNewProcess() {
    return event -> Router.navigate("gui", "process", "ProcessCreate");
  }

  public Consumer<Void> navigateToBatteryPacks() {
    return event -> Router.navigate("gui", "pack", "AllPacksOverview");
  }

  public Consumer<Void> navigateToEndEffectors() {
    return event -> Router.navigate("gui", "endeffector", "EndEffectorOverview");
  }

  public Consumer<Void> navigateToDimensions() {
    return event -> Router.navigate("gui", "dimension", "DimensionOverview");
  }

  public Consumer<Void> navigateToAbout() {
    return event -> Router.navigate("generalinfo", "About");
  }

}
