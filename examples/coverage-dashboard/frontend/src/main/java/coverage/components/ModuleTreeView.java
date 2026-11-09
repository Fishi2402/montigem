package coverage.components;

import coveragedashboard.CoverageMetrics;
import coveragedashboard.CoverageSourceFile;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;

@Component
public class ModuleTreeView extends ModuleTreeViewTOP{


  @Override
  public Consumer<Void> onFileNodeClick(CoverageSourceFile file){
    return $->{
      if(file.equals(this.getSelectedFile())){
        return;
      }
      this.setSelectedFile(file);
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
  public String getFileDisplayName(CoverageSourceFile file, CoverageSourceFile selectedFile){
    if (selectedFile != null && selectedFile.equals(file)) {
      return "▶ " + file.getName();
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
