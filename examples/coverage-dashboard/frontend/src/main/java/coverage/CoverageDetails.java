package coverage;

import app.AppRouter;
import coveragedashboard.CoveragePerspective;
import coveragedashboard.CoverageReport;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.PerspectiveType;
import coveragedashboard.ReportType;
import umlp.jsweet.extension.annotation.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public class CoverageDetails extends CoverageDetailsTOP {
  // Add hwc

  @Override
  public Consumer<CoverageSourceFile> onFileSelect(){
    return file -> this.setSelectedFile(Optional.of(file));
  }

  @Override
  public Consumer<CoveragePerspective> onPerspectiveSelect(){
    return perspective -> this.setSelectedPerspective(perspective);
  }

  @Override
  public Consumer<Void> backToOverview(){
    return $ ->{
      AppRouter.navigateToCoverageDashboard();
    };
  }

  @Override
  public Boolean isSourceMapViewRequired (CoverageReport report, CoveragePerspective perspective) {
    // We have only mapping for now when this is generated code, and we watch the "root"
    if(report.getType() == ReportType.GENERATED_CODE_TEST
        && perspective.getType() == PerspectiveType.MEASURED_ROOT)
      return true;
    else
      return false;
  }

  @Override
  public String getTitle(CoverageReport report){
    return "Coverage Details: " + report.getName();
  }
}
