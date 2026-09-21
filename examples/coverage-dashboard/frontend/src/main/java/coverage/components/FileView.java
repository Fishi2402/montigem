package coverage.components;

import coveragedashboard.CoveragePerspective;
import coveragedashboard.CoverageReport;
import coveragedashboard.PerspectiveType;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class FileView extends FileViewTOP {

  public FileView(){
    super();
  }

  @Override
  public void setReport(CoverageReport report){
    super.setReport(report);
    this.setSelectedPerspective(this.getReport().getDirectPerspective());
  }

  @Override
  public List<String> getPossibleViews(CoverageReport report){
    List<String> views = new ArrayList<>();
    views.add(report.getDirectPerspective().getName());
    for(CoveragePerspective aggregatedPerspective : report.getAggregatedPerspectivesList()){
      views.add(aggregatedPerspective.getName());
    }
    return views;
  }

  @Override
  public Consumer<Void> onViewClicked (String perspective) {
    return $ ->{
      // Check "root" first
      if(this.getReport().getDirectPerspective().getName().equals(perspective)){
        this.setSelectedPerspective(this.getReport().getDirectPerspective());
        return;
      }
      // Try all aggregated modules
      for(CoveragePerspective aggregatedPerspective : this.getReport().getAggregatedPerspectivesList()){
        if(aggregatedPerspective.getName().equals(perspective)){
          this.setSelectedPerspective(aggregatedPerspective);
          return;
        }
      }
    };
  }

  @Override
  public String getRootDisplayName(CoveragePerspective perspective){
    if(perspective.getType() == PerspectiveType.MEASURED_ROOT){
      return "Direct perspective - " + perspective.getName();
    }else {
      return "Aggregated perspective - " +perspective.getName();
    }
  }
}
