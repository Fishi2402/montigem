package coverage.components;

import coveragedashboard.CoverageMetrics;
import coveragedashboard.CoverageSourceFile;
import umlp.jsweet.extension.annotation.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class ModuleTreeView extends ModuleTreeViewTOP{


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
