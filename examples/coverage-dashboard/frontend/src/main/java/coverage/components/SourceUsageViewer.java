package coverage.components;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageMetrics;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.commands.GetTemplateUsages;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandResult;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class SourceUsageViewer extends SourceUsageViewerTOP{

  private List<CoverageSourceFile> _cachedUsages;

  public SourceUsageViewer() {
    super();
    this._cachedUsages = null;
  }


  @Override
  public void setFile (Optional<CoverageSourceFile> val) {
    this._cachedUsages = null;
    super.setFile(val);
  }

  @Override
  public List<CoverageSourceFile> getUsages (Optional<CoverageSourceFile> fileOpt) {
    if(fileOpt.isEmpty())
      return List.of();

    // Check if the result is already cached
    if(this._cachedUsages != null)
      return this._cachedUsages;

    CommandResult<CoverageSourceFile> commandResult = CommandManager.executeCommand(new GetTemplateUsages(fileOpt.get().getGemId(), this.getDirectPerspective().getGemId()));
    // Cache to avoid re-computing the result
    if(commandResult.isListResult() && commandResult.isSuccessful()){
      this._cachedUsages = commandResult.asListResult().get();

      // Trigger batch-loading of all metrics now to avoid later single requests when the coverage percentage is calculated...
      List<Long> metricsIds = new ArrayList<>();
      for(CoverageSourceFile usage : this._cachedUsages) {
        metricsIds.add(usage.getMetricsGem().getGemId());
      }
      CoverageDashboardManager.loadAllCoverageMetricsWithIDs(metricsIds);

    }else{
      this._cachedUsages = List.of();
    }
    return this._cachedUsages;

  }

  @Override
  public Consumer<Void> onUsedByFileSelected(CoverageSourceFile file) {
    return $ ->{
      if(this.getUsageSelect() != null){
        this.getUsageSelect().accept(file);
      }
    };
  }

  @Override
  public String getUsagesCount(Optional<CoverageSourceFile> fileOpt) {
    return "" + this.getUsages(fileOpt).size();
  }

  @Override
  public Boolean hasUsages(Optional<CoverageSourceFile> fileOpt){
    return this.getUsages(fileOpt).size() > 0;
  }

  @Override
  public String getCoveragePercent(CoverageMetrics metrics){
    return CoverageUtil.getCoveragePercentage(metrics, CoverageUtil::calculateCoverage);
  }

  @Override
  public String getCoverageColor(CoverageMetrics metrics){
    return CoverageUtil.getCoverageColor(metrics, CoverageUtil::calculateCoverage);
  }
}
