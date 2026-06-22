package coveragedashboard.commands;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.DummyContent;
import umlp.backendrte.command.SimpleResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;



public class MockContent extends MockContentTOP{

  @Override
  public SimpleResult<DummyContent> doAction(){
    return new SimpleResult<>(
        this.getId(),
        this.getMockFile(),
        DummyContent.class.getName()
    );
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  protected DummyContent getMockFile(){
    return CoverageDashboardManager.getDummyContentList().getFirst();
  }
}

