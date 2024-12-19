package example_charts;

import domain.commands.GetBarChart;
import mc.fenix.charts.gembarcharttypes.GemBarChartData;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandResult;
import umlp.jsweet.extension.annotation.Component;

@Component
public class GemBarChartFromCommand extends GemBarChartFromCommandTOP {

  @Override
  public GemBarChartData getBarchartData() {
    CommandResult<GemBarChartData> res = CommandManager.executeCommand(new GetBarChart());
    if(res.isSuccessful() && res.isSimpleResult()) {
      return res.asSimpleResult().get();
    }
    return null;
  }
}
