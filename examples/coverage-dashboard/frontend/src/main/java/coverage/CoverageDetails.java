package coverage;

import app.AppRouter;
import coveragedashboard.CoveragePerspective;
import coveragedashboard.CoverageReport;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.PerspectiveType;
import coveragedashboard.ReportType;
import coveragedashboard.TemplateUsage;
import umlp.jsweet.extension.annotation.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class CoverageDetails extends CoverageDetailsTOP {
  // Add hwc

  public CoverageDetails(){
    super();
    // init as empty list to avoid loading everything during init!
    this.setActiveFiles(List.of());
  }

  @Override
  public Consumer<CoverageSourceFile> onFileSelect(){
    return file -> {
      this.setActiveFiles(List.of(file));
    };
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
  public Boolean isUsageViewRequired(CoverageReport report, CoveragePerspective perspective){
    // We have this currently only for aggregated files
    if(report.getType() == ReportType.GENERATED_CODE_TEST
        && perspective.getType() == PerspectiveType.AGGREGATED)
      return true;
    else
      return false;
  }

  @Override
  public Consumer<TemplateUsage> onUsageSelect(){
    return usage -> {
      // Switch back to direct view
      this.setSelectedPerspective(this.getReport().getDirectPerspective());
      // Set the active files
      this.setActiveFiles(usage.getChainList());
    };
  }

  @Override
  public Consumer<CoverageSourceFile> onOpenSource(){
    return file -> {
      List<CoverageSourceFile> activeFiles = new ArrayList<>(this.getActiveFiles());
      activeFiles.add(file);
      this.setActiveFiles(activeFiles);
    };
  }

  @Override
  public Optional<CoverageSourceFile> getBaseFile(List<CoverageSourceFile> activeFiles){
    if(activeFiles.isEmpty()){
      return Optional.empty();
    }
    return Optional.of(activeFiles.get(activeFiles.size() - 1));
  }

  @Override
  public String getTitle(CoverageReport report){
    return "Coverage Details: " + report.getName();
  }
}
