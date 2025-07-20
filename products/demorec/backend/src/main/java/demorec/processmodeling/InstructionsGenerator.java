package demorec.processmodeling;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import de.monticore.bpmn.workflow._ast.ASTWFTask;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.tagging.SimpleSymbolTagger;
import de.monticore.tagging.tags.TagsMill;
import de.monticore.tagging.tags._ast.ASTTag;
import de.monticore.tagging.tags._ast.ASTTagUnit;
import de.monticore.tagging.tags._ast.ASTValuedTag;
import demorec.DemoRecManager;
import demorec.EndEffector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class InstructionsGenerator {
  public final static String STANDARD_OUTPUT_DIR = "./src/main/java/demorec/processmodeling/generatedInstructions";
  final static String STANDARD_OUTPUT_FILE_NAME_PREFIX = "instructions_for_process_with_gemid_";
  final static String STANDARD_FILE_ENDING = "txt";
  HashMap<String, String> endEffectorTypes;
  String file_extension;

  public InstructionsGenerator() {
    file_extension = STANDARD_FILE_ENDING;

    // Categorize end effectors by their (typical) join connection type
    endEffectorTypes = new HashMap<>();
    endEffectorTypes.put("Screw driver", "screw");
    endEffectorTypes.put("Socket_24", "screw");
    endEffectorTypes.put("Manuell", "manual");
    endEffectorTypes.put("Pincers", "plug");
    endEffectorTypes.put("TBD", "undefined");
    endEffectorTypes.put("E_8", "screw");
    endEffectorTypes.put("Torx_30", "screw");
    endEffectorTypes.put("Hex_Wrench_13", "screw");
    endEffectorTypes.put("Hex_Wrench_10", "screw");
    endEffectorTypes.put("Hex_Driver_10", "screw");
    endEffectorTypes.put("Torx_25", "screw");
    endEffectorTypes.put("Draht", "electrical");
    endEffectorTypes.put("Pipe_Wrench", "pipe");
    for (EndEffector ee : DemoRecManager.getEndEffectorList()) {
      if (!endEffectorTypes.containsKey(ee.getName())) {
        endEffectorTypes.put(ee.getName(), "undefined");
      }
    }
  }

  public String generateInstructions(String workflowDSLEncoding, String tags, boolean writeToFile) {
    // 1) Parse Workflow DSL-encoded process
    ASTWorkflowCompilationUnit cu;
    try {
      cu = getWorkflowCompilationUnit(workflowDSLEncoding);
    }
    catch(IOException e) {
      System.err.println(e.getMessage());
      return null;
    }

    // 2) Parse tags
    SimpleSymbolTagger tagger = null;
    try {
      tagger = getTagger(tags);
    }
    catch(IOException e) {
      System.err.println(e.getMessage());
      return null;
    }

    // 3) Build the instructions for one path in the model by traversing the Workflow DSL-encoded model by a custom visitor
    WorkflowTraverser wfTraverser = WorkflowMill.traverser();
    HashMap<String, HashMap<String, String>> tagsMap = getTagsMap(cu, tagger);
    InstructionsPrinter visitor = new InstructionsPrinter(tagsMap, endEffectorTypes);
    wfTraverser.add4Workflow(visitor);
    cu.accept(wfTraverser);

    String instructions = visitor.getPrinter().getContent();
    if (writeToFile) {
      writeInstructionsFile(tagsMap.get(cu.getWFProcess().getName()).get(ProcessModelTagger.processModelGemIdTagName), instructions);
    }

    return instructions;
  }

  private HashMap<String, HashMap<String, String>> getTagsMap(ASTWorkflowCompilationUnit cu, SimpleSymbolTagger tagger) {
    HashMap<String, HashMap<String, String>> result = new HashMap<>();

    // Add tags for the process
    result.putIfAbsent(cu.getWFProcess().getName(), new HashMap<>());
    List<ASTTag> processTags = tagger.getTags(cu.getWFProcess().getSymbol());
    for (ASTTag tag : processTags) {
      if (tag instanceof ASTValuedTag) {
        ASTValuedTag valuedTag = (ASTValuedTag) tag;
        result.get(cu.getWFProcess().getName()).put(valuedTag.getName(), valuedTag.getValue());
      }
    }

    // Add tags for the tasks
    for (ASTFlowElement flowElement : cu.getWFProcess().getFlowElementList()) {
      if (flowElement instanceof ASTWFTask) {
        ASTWFTask task = (ASTWFTask) flowElement;
        List<ASTTag> tags = tagger.getTags(task.getSymbol());
        for (ASTTag tag : tags) {
          if (tag instanceof ASTValuedTag) {
            ASTValuedTag valuedTag = (ASTValuedTag) tag;
            result.putIfAbsent(task.getName(), new HashMap<>());
            result.get(task.getName()).put(valuedTag.getName(), valuedTag.getValue());
          }
        }
      }
    }
    return result;
  }

  private ASTWorkflowCompilationUnit getWorkflowCompilationUnit(String encoding) throws IOException {
    ASTWorkflowCompilationUnit ast = WorkflowMill.parser().parse_String(encoding).get();
    WorkflowMill.init();
    WorkflowMill.scopesGenitorDelegator().createFromAST(ast);
    return ast;
  }

  private SimpleSymbolTagger getTagger(String encoding) throws IOException {
    ASTTagUnit tagUnit = TagsMill.parser().parse_String(encoding).get();
    return new SimpleSymbolTagger(() -> Collections.singleton(tagUnit));
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

  private File writeInstructionsFile(String processGemId, String content) {
    if (!createOutputDirectory(STANDARD_OUTPUT_DIR)) {
      throw new IllegalArgumentException("Cannot save the execution instructions for the process with GemId '" + processGemId + "', since the the output directory cannot be created");
    }
    String fullFilePath = Paths.get(STANDARD_OUTPUT_DIR + "/" + STANDARD_OUTPUT_FILE_NAME_PREFIX + processGemId + "." + STANDARD_FILE_ENDING).normalize().toAbsolutePath().toString();

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullFilePath))) {
      writer.write(content);
    }
    catch (IOException e) {
      System.err.println("Cannot write the execution instructions for process model with GemId '" + processGemId + "' into a file: " + e.getMessage());
    }
    File file = new File(fullFilePath);

    return file;
  }
}
