package demorec.commands;

import demorec.DemoRecManager;
import demorec.ProcessModel;
import demorec.processmodeling.InstructionsGenerator;
import demorec.processmodeling.ProcessModelExporter;
import demorec.processmodeling.ProcessModelTagger;
import umlp.backendrte.command.SimpleResult;

import org.apache.commons.lang3.StringEscapeUtils;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

public class GenerateInstructions extends GenerateInstructionsTOP {
  public GenerateInstructions(long processModelGemId, String workflowDSLEncoding, String tags) {
    super(processModelGemId, workflowDSLEncoding, tags);
  }

  @BackendOnly(value = BackendOnlyModes.THROW_ERROR)
  public SimpleResult<String> doAction() {
    ProcessModel processModel = DemoRecManager.getProcessModelSure(getProcessModelGemId());
    InstructionsGenerator instructionsGenerator = new InstructionsGenerator();

    if (workflowDSLEncoding.isEmpty()) {
      ProcessModelExporter pmSerializer = new ProcessModelExporter();
      workflowDSLEncoding = pmSerializer.exportProcessModel(processModel, false);
    }

    if (tags.isEmpty()) {
      ProcessModelTagger processModelTagger = new ProcessModelTagger();
      tags = processModelTagger.tagProcessModel(processModel, workflowDSLEncoding,false);
    }

    return new SimpleResult<>(getId(), StringEscapeUtils.escapeJson(instructionsGenerator.generateInstructions(workflowDSLEncoding, tags, true)), "java.lang.String");
  }
}
