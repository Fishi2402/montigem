package coverage;

import coverage.components.CoverageUtil;
import coveragedashboard.Run;
import coveragedashboard.commands.GetAllRuns;
import coveragedashboard.commands.GetCciTrajectory;
import mc.fenix.charts.gemlinecharttypes.GemLineChartData;
import mc.fenix.input.gemdropdowninputtypes.Option;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandResult;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class Dashboard extends DashboardTOP {

  private final Map<String, Run> _runsById = new HashMap<>();

  public Dashboard() {
    super();
  }

  @Override
  public void init() {
    super.init();
    this.setShowCciTrend(false);
    this.setCciTrendLabel("CCI Trend");
    this.setSelectedRuns(new ArrayList<>());
    this.setRunOptions(new ArrayList<>());
    this.loadRuns();
  }

  private void loadRuns() {
    this._runsById.clear();
    List<Option> options = new ArrayList<>();
    CommandResult<Run> res = CommandManager.executeCommand(new GetAllRuns());
    if (res.isSuccessful() && res.isListResult()) {
      List<Run> runs = res.asListResult().get();
      for (Run run : runs) {
        String id = Long.toString(run.getGemId());
        this._runsById.put(id, run);
        options.add(new Option(id, formatRunOptionLabel(run)));
      }
      this.setRunOptions(options);
      if (!runs.isEmpty()) {
        this.setSelectedRunId(Long.toString(runs.get(0).getGemId()));
      }
    }
  }

  @Override
  public void setSelectedRunId(String value) {
    super.setSelectedRunId(value);
    List<Run> displayed = new ArrayList<>();
    if (value != null && this._runsById.containsKey(value)) {
      displayed.add(this._runsById.get(value));
    }
    this.setSelectedRuns(displayed);
  }

  @Override
  public Consumer<Void> onToggleCciTrend() {
    return $ -> {
      if (this.isShowCciTrend()) {
        this.setShowCciTrend(false);
        this.setCciTrendLabel("CCI Trend");
      } else {
        CommandResult<GemLineChartData> res = CommandManager.executeCommand(new GetCciTrajectory());
        if (res.isSuccessful() && res.isSimpleResult() && res.asSimpleResult().get() != null) {
          this.setCciChartData(res.asSimpleResult().get());
        }
        this.setShowCciTrend(true);
        this.setCciTrendLabel("Hide CCI Trend");
      }
    };
  }

  private String formatRunOptionLabel(Run run) {
    String ts = run.getTimestamp() != null
        ? run.getTimestamp().toString().replace("T", " ")
        : "unknown";
    if (ts.length() > 16) {
      ts = ts.substring(0, 16);
    }
    String hash = run.getCommitHash();
    String shortHash = (hash == null || hash.isEmpty())
        ? "unknown"
        : (hash.length() > 8 ? hash.substring(0, 8) : hash);
    return ts + " (" + shortHash + ") — " + CoverageUtil.formatPercentage(run.getCci());
  }
}
