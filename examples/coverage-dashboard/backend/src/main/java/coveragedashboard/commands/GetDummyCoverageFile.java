package coveragedashboard.commands;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.Run;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.common.LoadingState;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.util.Optional;

public class GetDummyCoverageFile extends GetDummyCoverageFileTOP {

  @Override
  public SimpleResult<CoverageSourceFile> doAction (){
    Optional<CoverageSourceFile> result = dummyCoverageFile();
    if(result.isEmpty()){
      return new SimpleResult<>(
          getId(), LoadingState.FAILED_TO_LOAD, CoverageSourceFile.class.getName());
    }
    return new SimpleResult<>(getId(), result.get(), CoverageSourceFile.class.getName());
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  protected  Optional<CoverageSourceFile> dummyCoverageFile(){
    Run run = CoverageDashboardManager.getRunList().getFirst();
    return Optional.of(run.getReports(0).getModule().getSubModules(0).getSourceFiles(0));
  }

}
