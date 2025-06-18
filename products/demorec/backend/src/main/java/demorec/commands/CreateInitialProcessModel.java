package demorec.commands;

import de.se_rwth.commons.logging.Log;
import demorec.DemoRecManager;
import demorec.ProcessModel;
import demorec.processmodeling.InitialProcessModelCreator;
import umlp.backendrte.command.SimpleResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

public class CreateInitialProcessModel extends CreateInitialProcessModelTOP {
  public CreateInitialProcessModel(long processGemId) {
    super(processGemId);
  }

  @BackendOnly(value = BackendOnlyModes.THROW_ERROR)
  public SimpleResult<ProcessModel> doAction () {
    InitialProcessModelCreator ipmC = new InitialProcessModelCreator();
    Log.info("Creating initial process model for process with gemId "+getProcessGemId(), this.getClass().getName());
    ProcessModel pm = ipmC.createInitialProcessModel(DemoRecManager.getProcessSure(getProcessGemId()));

    return new SimpleResult<>(getId(), pm, "demorec.ProcessModel");
  }
}
