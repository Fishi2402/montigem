package gui.montiverse;

import dtcockpit.Branch;
import dtcockpit.Project;
import dtcockpit.commands.StartMontiVerseLitePipeline;
import mc.fenix.input.gemdropdowninputtypes.Option;
import umlp.backendrte.command.CommandManager;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class OverviewPage extends OverviewPageTOP {

  @Override
  public Consumer<Void> startPipeline(Project p) {
    return $ -> {
      CommandManager.executeCommand(new StartMontiVerseLitePipeline());
    };
  }

  @Override
  public List<Option> getBranchDropdownOptions(Project dependentProject) {
    List<Option> res = new ArrayList<>();
    for (Branch b : dependentProject.getBranchesList()) {
      res.add(new Option(b.getName()));
    }
    return res;
  }
}
