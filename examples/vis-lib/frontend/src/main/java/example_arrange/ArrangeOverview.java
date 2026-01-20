/* (c) https://github.com/MontiCore/monticore */
package example_arrange;

import umlp.jsweet.extension.annotation.Component;
import umlp.guiutil.Router;
import java.util.function.Consumer;

@Component
public class ArrangeOverview extends ArrangeOverviewTOP {

  private Consumer<Void> navigateTo(String target) {
      return v -> {
          Router.navigate("example_arrange", target);
      };
  }
  
  private Consumer<Void> navigateToTable(String target) {
      return v -> {
          Router.navigate("example_table", target);
      };
  }

  public Consumer<Void> goToGrid() { return navigateTo("GemGridExample"); }
  public Consumer<Void> goToRow() { return navigateTo("GemRowExample"); }
  public Consumer<Void> goToColumn() { return navigateTo("GemColumnExample"); }
  public Consumer<Void> goToCard() { return navigateTo("GemCardExample"); }
  public Consumer<Void> goToDialog() { return navigateTo("GemDialogExample"); }
  public Consumer<Void> goToTreeNode() { return navigateTo("GemTreeNodeExample"); }
  public Consumer<Void> goToTable() { return navigateToTable("GemTableExample"); }
}
