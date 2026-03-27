package demorec.processmodeling;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._symboltable.WFActivitySymbol;
import de.monticore.tagging.SimpleSymbolTagger;
import de.monticore.tagging.tags.TagsMill;
import de.monticore.tagging.tags._ast.ASTTagUnit;
import de.monticore.tagging.tags._ast.ASTTagUnitBuilder;
import de.monticore.tagging.tags._ast.ASTValuedTag;
import demorec.ProcessModel;
import demorec.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

public class ProcessModelTagger {
  public final static String STANDARD_OUTPUT_DIR = "./src/main/java/demorec/processmodeling/generatedTags";
  final static String STANDARD_OUTPUT_FILE_NAME_PREFIX = "tags_of_process_model_with_gemid_";
  final static String STANDARD_TAG_DEFINITION_FILE_ENDING = "tags";

  String tag_definition_file_extension;
  public final static String gemIdTagName = "ProcessGemId";
  public final static String processModelGemIdTagName = "ProcessModelGemId";
  public final static String taskGemIdTagName = "taskGemId";
  public final static String taskDurationTagName = "taskDurationSeconds";
  public final static String taskEndEffectorTagName = "taskUsedEndEffectorName";
  public final static String taskJointConnection3DPositionTagName = "taskJointConnection3DPosition";
  //public final static String taskJointConnectionTypeTagName = "taskJointConnectionType";

  public ProcessModelTagger() {
    this.tag_definition_file_extension = STANDARD_TAG_DEFINITION_FILE_ENDING;
  }

  public String tagProcessModel(ProcessModel processModel, boolean writeToFile) {
    ProcessModelExporter pmExporter = new ProcessModelExporter();
    String workflowDSLEncoding = pmExporter.exportProcessModel(processModel, false);

    return tagProcessModel(processModel, workflowDSLEncoding, writeToFile);
  }

  public String tagProcessModel(ProcessModel processModel, String workflowDSLEncoding, boolean writeToFile) {
    // 1) Compile the Workflow DSL-encoding
    ASTWorkflowCompilationUnit cu = null;
    try {
      cu = getWorkflowDSLCompilationUnit(workflowDSLEncoding);
    }
    catch(IOException ex) {
      System.err.println("Cannot tag the workflow DSL-encoded model of process model with GemId " + processModel.getGemId());
      return "";
    }

    // 2) Establish the Tag Unit
    ASTTagUnitBuilder tub = TagsMill.tagUnitBuilder();
    tub.setName("tags_for_" + cu.getWFProcess().getName());
    ASTTagUnit tagUnit = tub.build();
    SimpleSymbolTagger tagger = new SimpleSymbolTagger(() -> Collections.singleton(tagUnit));

    // 3) Tag the process models' GemId and it's underlying process' GemId
    String processGemId = "not_present";
    if (processModel.getUnderlyingProcess() != null) {
      processGemId = processModel.getUnderlyingProcess().getGemId() + "";
    }
    ASTValuedTag processTag = TagsMill.valuedTagBuilder().setName(gemIdTagName).setValue(processGemId).build();
    ASTValuedTag processModelTag = TagsMill.valuedTagBuilder().setName(processModelGemIdTagName).setValue(processModel.getGemId() + "").build();
    tagger.addTag(cu.getWFProcess().getSymbol(), processTag);
    tagger.addTag(cu.getWFProcess().getSymbol(), processModelTag);

    // 4) Tag the GemId, duration, end effector name and join connection position of each task
    for (Task task : processModel.getSubTasksList()) {
      String grammarTaskName = ProcessModelExporter.generateTaskName(task);

      WFActivitySymbol activitySymbol = cu.getWFProcess().getFlowElement(0).getEnclosingScope().resolveWFActivity(grammarTaskName).get();
      ASTValuedTag gemIdTag = TagsMill.valuedTagBuilder().setName(taskGemIdTagName).setValue(task.getGemId() + "").build();
      tagger.addTag(activitySymbol, gemIdTag);

      ASTValuedTag durationTag = TagsMill.valuedTagBuilder().setName(taskDurationTagName).setValue(task.getTimeConsumption() + "").build();
      tagger.addTag(activitySymbol, durationTag);

      ASTValuedTag endEffectorTag = TagsMill.valuedTagBuilder().setName(taskEndEffectorTagName).setValue(task.getUses().getName()).build();
      tagger.addTag(activitySymbol, endEffectorTag);

      if(task.getDeconstructsGem().isPresent()) {
        StringBuilder positionBuilder = new StringBuilder();
        positionBuilder.append("(");
        positionBuilder.append(task.getDeconstructs().getXPos().map(String::valueOf).orElse("undefined"));
        positionBuilder.append("|");
        positionBuilder.append(task.getDeconstructs().getYPos().map(String::valueOf).orElse("undefined"));
        positionBuilder.append("|");
        positionBuilder.append(task.getDeconstructs().getZPos().map(String::valueOf).orElse("undefined"));
        positionBuilder.append(")");
        ASTValuedTag jointConnectionPositionTag = TagsMill.valuedTagBuilder().setName(taskJointConnection3DPositionTagName).setValue(positionBuilder.toString()).build();
        tagger.addTag(activitySymbol, jointConnectionPositionTag);
      }

      /*ASTValuedTag jointConnectionTypeTag = TagsMill.valuedTagBuilder().setName(taskJointConnectionTypeTagName).setValue(task.getDeconstructs().getJointConnectionKind().getJointConnectionType().name()).build();
      tagger.addTag(cu.getProcess().getLane(0).getEnclosingScope().resolveAtomicActivity(grammarTaskName).get(), jointConnectionTypeTag);*/
    }

    String result = TagsMill.prettyPrint(tagUnit, true);
    if (writeToFile) {
      writeTagDefinitionFile(processModel.getGemId(), result);
    }

    return result;
  }

  private ASTWorkflowCompilationUnit getWorkflowDSLCompilationUnit(String encoding) throws IOException {
    ASTWorkflowCompilationUnit ast = WorkflowMill.parser().parse_String(encoding).get();
    WorkflowMill.init();
    WorkflowMill.scopesGenitorDelegator().createFromAST(ast);
    return ast;
  }

  private File writeTagDefinitionFile(long gemId, String content) {
    if (!createOutputDirectory(STANDARD_OUTPUT_DIR)) {
      throw new IllegalArgumentException("Cannot save the tags of process model with GemId " + gemId + ", since the the output directory cannot be created");
    }
    String fullFilePath = Paths.get(STANDARD_OUTPUT_DIR + "/" + STANDARD_OUTPUT_FILE_NAME_PREFIX + gemId + "." + tag_definition_file_extension).normalize().toAbsolutePath().toString();

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullFilePath))) {
      writer.write(content);
    }
    catch (IOException e) {
      System.err.println("Cannot write the tags of process model with GemId " + gemId + " into a file: " + e.getMessage());
    }
    File file = new File(fullFilePath);

    return file;
  }

  private boolean createOutputDirectory(String pathToDirectory) {
    if (pathToDirectory.isEmpty()) {
      return false;
    }

    File outputDirectory = new File(pathToDirectory);
    if (!outputDirectory.exists()) {
      try {
        return outputDirectory.mkdir();
      } catch (SecurityException se) {
        return false;
      }
    }

    return true;
  }
}
