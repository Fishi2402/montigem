package demorec.commands;

import demorec.DemoRecManager;
import demorec.processmodeling.ProcessModelExporter;
import umlp.backendrte.command.SimpleResult;

import org.apache.commons.lang3.StringEscapeUtils;

import demorec.ProcessModel;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

public class ExportProcessModel extends ExportProcessModelTOP {
  public ExportProcessModel(long processModelGemId) {
    super(processModelGemId);
  }

  @BackendOnly(value = BackendOnlyModes.THROW_ERROR)
  public SimpleResult<String> doAction() {
    ProcessModel processModel = DemoRecManager.getProcessModelSure(getProcessModelGemId());
    ProcessModelExporter pmSerializer = new ProcessModelExporter();

    return new SimpleResult<>(getId(), StringEscapeUtils.escapeJson(pmSerializer.exportProcessModel(processModel, true)), "java.lang.String");
  }
}
