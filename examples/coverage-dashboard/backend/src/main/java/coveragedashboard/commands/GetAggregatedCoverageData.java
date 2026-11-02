package coveragedashboard.commands;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageMetrics;
import coveragedashboard.CoveragePerspective;
import mc.fenix.charts.gempiecharttypes.GemPieChartData;
import mc.fenix.charts.gempiecharttypes.GemPieChartDataBuilder;
import mc.fenix.charts.gempiecharttypes.GemPieChartEntry;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.common.LoadingState;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GetAggregatedCoverageData  extends GetAggregatedCoverageDataTOP {

  public GetAggregatedCoverageData(long id){
    super(id);
  }

  @Override
  public SimpleResult<GemPieChartData> doAction(){
    Optional<GemPieChartData> result = getCoverageData(this.getPerspectiveGemId());
    if(result.isEmpty()){
      return new SimpleResult<>(
          getId(), LoadingState.FAILED_TO_LOAD, GemPieChartData.class.getName());
    }
    return new SimpleResult<>(getId(), result.get(), GemPieChartData.class.getName());
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  protected Optional<GemPieChartData> getCoverageData(long perspectiveGemId){
    Optional<CoveragePerspective> perspectiveOpt = CoverageDashboardManager.coveragePerspectiveOpt(perspectiveGemId);
    if(perspectiveOpt.isEmpty())
      return Optional.empty();

    CoveragePerspective perspective = perspectiveOpt.get();

    CoverageMetrics metrics = perspective.getRootModule().getMetrics();

    List<GemPieChartEntry> entries = new ArrayList<>();
    entries.add(new GemPieChartEntry(metrics.getCoveredInstructions(), "Covered", Optional.of("#1a7f37")));
    entries.add(new GemPieChartEntry(metrics.getMissedInstructions(), "Missed", Optional.of("#cf222e")));
    return new GemPieChartDataBuilder().entries(entries).build();
  }
}

