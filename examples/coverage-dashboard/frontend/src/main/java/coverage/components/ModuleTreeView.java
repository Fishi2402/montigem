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
    int covered = metrics.getCoveredInstructions();
    int total = metrics.getCoveredInstructions() + metrics.getMissedInstructions();
    if(total == 0){
      return "0.00%";
    }
    double coverage = (double) covered / total;
    double rounded = Math.round(coverage * 10000.0) / 100.0;
    return rounded + "%";
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
    int covered = metrics.getCoveredInstructions();
    int total = metrics.getCoveredInstructions() + metrics.getMissedInstructions();
    if(total == 0){
      return "#cf222e";
    }
    double coverage = (double) covered / total;
    if(coverage < 0.50){
      return "#cf222e";
    }else if(coverage < 0.80){
      return "#8a5c00";
    }
    return "#1a7f37";
  }
}
