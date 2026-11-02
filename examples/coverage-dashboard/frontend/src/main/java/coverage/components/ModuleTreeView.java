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
    double coverage = calculateCoverage(metrics);
    return coverage + "%";
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
    double coverage = calculateCoverage(metrics);
    if(coverage < 50){
      return "#cf222e";
    }else if(coverage < 80){
      return "#8a5c00";
    }
    return "#1a7f37";
  }

  private double calculateCoverage(CoverageMetrics metrics){
    // We use the instructions only when we watch the "root", otherwise
    // we need to use the lines!
    double coverage;
    if(this.isRootPerspective()) {
      int covered = metrics.getCoveredInstructions();
      int total = metrics.getCoveredInstructions() + metrics.getMissedInstructions();
      if (total == 0) {
        return 0;
      }
      coverage = (double) covered / total;

    }else{
      double covered = metrics.getCoveredLines() + 0.5 * metrics.getPartialLines();
      int total = metrics.getCoveredLines() + metrics.getMissedLines() + metrics.getPartialLines();
      if (total == 0) {
        return 0;
      }
      coverage = covered / total;
    }
    return Math.round(coverage * 10000.0) / 100.0;
  }
}
