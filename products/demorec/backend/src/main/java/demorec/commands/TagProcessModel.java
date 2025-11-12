package demorec.commands;

import demorec.DemoRecManager;
import demorec.processmodeling.ProcessModelExporter;
import demorec.processmodeling.ProcessModelTagger;
import umlp.backendrte.command.SimpleResult;

import org.apache.commons.lang3.StringEscapeUtils;

import demorec.ProcessModel;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

public class TagProcessModel extends TagProcessModelTOP {
  public TagProcessModel(long processModelGemId, String workflowDSLEncoding) {
    super(processModelGemId, workflowDSLEncoding);
  }

  @BackendOnly(value = BackendOnlyModes.THROW_ERROR)
  public SimpleResult<String> doAction() {
    ProcessModel processModel = DemoRecManager.getProcessModelSure(getProcessModelGemId());
    ProcessModelTagger processModelTagger = new ProcessModelTagger();

    if (workflowDSLEncoding.isEmpty()) {
      ProcessModelExporter pmSerializer = new ProcessModelExporter();
      workflowDSLEncoding = pmSerializer.exportProcessModel(processModel, false);
    }

    return new SimpleResult<>(getId(), StringEscapeUtils.escapeJson(processModelTagger.tagProcessModel(processModel, workflowDSLEncoding,true)), "java.lang.String");
  }
}
