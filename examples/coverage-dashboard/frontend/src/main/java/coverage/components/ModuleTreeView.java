package coverage.components;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageMetrics;
import coveragedashboard.CoverageModule;
import coveragedashboard.CoverageSourceFile;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class ModuleTreeView extends ModuleTreeViewTOP{

  private Long lastActiveFileId;
  private Boolean isExpanded;

  public ModuleTreeView(){
    super();
    this.isExpanded = false;
    this.lastActiveFileId = null;
  }

  @Override
  public Consumer<Boolean> onNodeToggle(CoverageModule module){
    return expanded ->{
      this.isExpanded = expanded;
      if(expanded && module != null){
        loadMetricsForModule(module);
      }
    };
  }

  private void loadMetricsForModule(CoverageModule module) {
    if (module != null && module.getSourceFilesList() != null) {
      List<Long> metricsIds = new ArrayList<>();
      for (CoverageSourceFile file : module.getSourceFilesList()) {
        if (file != null && file.getMetricsGem() != null) {
          metricsIds.add(file.getMetricsGem().getGemId());
        }
      }
      if (!metricsIds.isEmpty()) {
        CoverageDashboardManager.loadAllCoverageMetricsWithIDs(metricsIds);
      }
    }
  }

  @Override
  public String buildCurrentPath(String subModuleName){
    if(this.getCurrentPath().isEmpty())
      return subModuleName;
    return this.getCurrentPath() + "/" + subModuleName;
  }

  @Override
  public Boolean shouldBeExpanded(List<CoverageSourceFile> activeFiles){
    if(activeFiles == null || activeFiles.isEmpty()) {
      this.lastActiveFileId = null;
      return this.isExpanded;
    }

    CoverageSourceFile baseFile = activeFiles.get(0);
    Long currentFileId = baseFile.getGemId();


    if(this.lastActiveFileId != null & this.lastActiveFileId.equals(currentFileId))
      // No new file, do not try to expand automatically
      return this.isExpanded;

    // Check if we have to auto-expand
    String activeFilePath = baseFile.getPath().replace('\\', '/');
    if(activeFilePath.startsWith(this.getCurrentPath())){
      this.isExpanded = true;
      loadMetricsForModule(this.getRootModule());
    }
    return this.isExpanded;
  }

  @Override
  public Consumer<Void> onFileNodeClick(CoverageSourceFile file){
    return $->{
      this.getFileSelect().accept(file);
    };
  }

  @Override
  public String getCoveragePercent(CoverageMetrics metrics){
    if(this.isRootPerspective()){
      return CoverageUtil.getCoveragePercentage(metrics, CoverageUtil::calculateCoverageForInstructions);
    }else{
      return CoverageUtil.getCoveragePercentage(metrics, CoverageUtil::calculateCoverage);
    }
  }

  @Override
  public String getFileDisplayName(CoverageSourceFile file, List<CoverageSourceFile> activeFiles){
    // Nothing selected so far
    if(activeFiles.isEmpty()){
      return file.getName();
    }
    CoverageSourceFile baseFile = activeFiles.get(0);
    if (baseFile != null && baseFile.getGemId() == file.getGemId() ) {
      String name = "▶ " + file.getName();
      // Add chain of active files for base-file
      for(CoverageSourceFile activeFile : activeFiles.stream().skip(1).toList()){
        if(activeFile != null && activeFile.getGemId() != file.getGemId()){
          name = name + "\n  ↳" + activeFile.getName();
        }
      }
      return name;
    }
    return file.getName();
  }

  @Override
  public String getCoverageColor(CoverageMetrics metrics){
    if(this.isRootPerspective()){
      return CoverageUtil.getCoverageColor(metrics, CoverageUtil::calculateCoverageForInstructions);
    }else{
      return CoverageUtil.getCoverageColor(metrics, CoverageUtil::calculateCoverage);
    }
  }

}
