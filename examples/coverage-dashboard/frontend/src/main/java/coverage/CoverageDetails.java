package coverage;

import coveragedashboard.CoverageReport;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.ReportType;
import umlp.guiutil.Router;
import umlp.jsweet.extension.annotation.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public class CoverageDetails extends CoverageDetailsTOP {
  // Add hwc

  @Override
  public Consumer<CoverageSourceFile> onFileSelect(){
    return file ->{
      this.setSelectedFile(Optional.of(file));
    };
  }

  @Override
  public Consumer<Void> backToOverview(){
    return $ ->{
      Router.navigate("/");
    };
  }

  @Override
  public Boolean isSourceMapViewRequired (CoverageReport report, Optional<CoverageSourceFile> file) {
    // For now, only ts has mapping
    if(report.getType() == ReportType.GENERATED_CODE_TEST)
      return true;
    else
      return false;
  }
}
