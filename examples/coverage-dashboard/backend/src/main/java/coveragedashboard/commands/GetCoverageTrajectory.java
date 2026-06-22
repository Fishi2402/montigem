package coveragedashboard.commands;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageMetrics;
import coveragedashboard.CoverageReport;
import coveragedashboard.Run;
import mc.fenix.charts.gemlinecharttypes.GemLineChartData;
import mc.fenix.charts.gemlinecharttypes.GemLineChartDataBuilder;
import mc.fenix.charts.gemlinecharttypes.GemLineChartEntry;
import mc.fenix.charts.gemlinecharttypes.GemLineChartEntryBuilder;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.common.GemList;
import umlp.backendrte.common.LoadingState;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GetCoverageTrajectory extends GetCoverageTrajectoryTOP {

  public GetCoverageTrajectory(){
    super();
  }

  @Override
  public SimpleResult<GemLineChartData> doAction(){
    Optional<GemLineChartData> result = calculateCoverageTrajectory();
    if(result.isEmpty()){
      return new SimpleResult<>(
          getId(), LoadingState.FAILED_TO_LOAD, GemLineChartData.class.getName());
    }
    return new SimpleResult<>(getId(), result.get(), GemLineChartData.class.getName());
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  protected Optional<GemLineChartData> calculateCoverageTrajectory(){
    // Get the runs
    GemList<Run> runs = CoverageDashboardManager.getRunList();

    // Sort them by date in ascending order to provide correct data on x-axis
    List<Run> sortedRuns = runs.stream().sorted(Comparator.comparing(Run::getTimestamp)).toList();

    List<String> labels = new ArrayList<>();
    Map<String, GemLineChartEntryBuilder> entryBuilders = new HashMap<>();

    for(Run run : sortedRuns){
      labels.add(run.getTimestamp().toString());

      List<String> processedReportsInRun = new ArrayList<>();
      for(CoverageReport report : run.getReportsList()){
        String reportName = report.getName();
        processedReportsInRun.add(reportName);

        CoverageMetrics metrics = report.getModule().getMetrics();
        double coverage = ((double)metrics.getCoveredInstructions()) / (metrics.getCoveredInstructions() + metrics.getMissedInstructions());
        if(Double.isNaN(coverage))
          coverage = 0d;

        entryBuilders.computeIfAbsent(reportName, (name)->{
          GemLineChartEntryBuilder newEntryBuilder = new GemLineChartEntryBuilder().label(name);
          // Fill default data when not present from the beginning
          for(int i = 0; i < labels.size() - 1; i++){
            newEntryBuilder.addData(0d);
          }
          return newEntryBuilder;
        }).addData(coverage);
      }
      for (Map.Entry<String, GemLineChartEntryBuilder> entry : entryBuilders.entrySet()) {
        if (!processedReportsInRun.contains(entry.getKey())) {
          entry.getValue().addData(0d);
        }
      }
    }

    List<GemLineChartEntry> entries = new ArrayList<>();
    for(GemLineChartEntryBuilder builder : entryBuilders.values()){
      entries.add(builder.build().get());
    }

    GemLineChartData data = new GemLineChartDataBuilder().entries(entries).labels(labels).build().get();

    return Optional.of(data);
  }


}
