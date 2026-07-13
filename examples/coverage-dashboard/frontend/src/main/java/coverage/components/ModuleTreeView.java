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
}
