package jsweettest.commands;

import jsweettest.JsweettestServerApplication;
import umlp.backendrte.command.StatusResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

public class ResetTestData extends ResetTestDataTOP  {
  @Override
  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  public StatusResult doAction() {
    System.err.println("Resetting test data by command");
    JsweettestServerApplication.resetTestData();
    return StatusResult.ok(this.getId());
  }
}
