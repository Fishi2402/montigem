package coveragedashboard.commands;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageMetrics;
import coveragedashboard.CoverageReport;
import coveragedashboard.ReportType;
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
import java.util.List;
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
    List<GemLineChartEntry> entries = new ArrayList<>();
    GemLineChartEntry internalEntryBuilder = new GemLineChartEntryBuilder().label("Internal Coverage");
    GemLineChartEntry e2eFrontendEntryBuilder = new GemLineChartEntryBuilder().label("E2E Frontend coverage");
    GemLineChartEntry e2eBackendEntryBuilder = new GemLineChartEntryBuilder().label("E2E Backend coverage");

    for(Run run : sortedRuns){
      labels.add(run.getTimestamp().toString());
      for(CoverageReport report : run.getReportsList()){
        CoverageMetrics metrics = report.getModule().getMetrics();
        double coverage = ((double)metrics.getCoveredInstructions()) / (metrics.getCoveredInstructions() + metrics.getMissedInstructions());
        switch (report.getType()){
          case ReportType.INTERNAL:
            internalEntryBuilder.addData(coverage);
            break;
          case ReportType.E2E_BACKEND:
            e2eBackendEntryBuilder.addData(coverage);
            break;
          case ReportType.E2E_FRONTEND:
            e2eFrontendEntryBuilder.addData(coverage);
            break;
        }
      }
    }
    entries.add(internalEntryBuilder.build().get());
    entries.add(e2eFrontendEntryBuilder.build().get());
    entries.add(e2eBackendEntryBuilder.build().get());

    GemLineChartData data = new GemLineChartDataBuilder().entries(entries).labels(labels).build().get();

    return Optional.of(data);
  }


}
