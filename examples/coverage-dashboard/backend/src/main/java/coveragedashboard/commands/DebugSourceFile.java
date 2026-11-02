package coveragedashboard.commands;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageSourceFile;
import umlp.backendrte.command.SimpleResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;



public class DebugSourceFile extends DebugSourceFileTOP{

  @Override
  public SimpleResult<CoverageSourceFile> doAction(){
    return new SimpleResult<CoverageSourceFile>(
        this.getId(),
        this.createDebugSourceFile(),
        CoverageSourceFile.class.getName()
    );
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  protected CoverageSourceFile createDebugSourceFile(){
    return CoverageDashboardManager.getCoverageSourceFileSure(2L);
  }
}

