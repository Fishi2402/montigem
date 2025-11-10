package demorec.processmodeling;

import demorec.ProcessModel;
import demorec.Task;
import demorec.processmodeling.wrapper.Gateway;
import demorec.processmodeling.wrapper.MergeTaskSplit;
import demorec.processmodeling.wrapper.Split;

import java.io.File;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ProcessModelExporter {
  public final static String STANDARD_OUTPUT_DIR = "./src/main/java/demorec/processmodeling/generatedModels";
  final static String STANDARD_ARTIFACT_FILE_ENDING = "wfm";
  final static String STANDARD_LANE_NAME = "process_lane";
  final static String STANDARD_OUTPUT_FILE_NAME_PREFIX = "process_model_of_process_with_gemid_";
  final static String[] forbiddenNameCharacters = {"^","<", ">", "{", "}", "\"", "\\", "/", "|", ";", ":", ".", ",", "~", "!", "?", "@", "#", "$", "%", "^", "=", "&", "*", "]", "(", ")", "[", "¿", "§", "«", "»", "ω", "⊙", "¤", "°", "℃", "℉", "€", "¥", "£", "¢", "¡", "®", "©", "-", "+"}; // The Workflow DSL does not like them within names of processes and tasks

  String file_extension;
  BPMNFactory factory;
  int gateway_counter;
  int flowblock_counter;

  public ProcessModelExporter() {
    this.file_extension = STANDARD_ARTIFACT_FILE_ENDING;
  }

  public String exportProcessModel(ProcessModel processModel, boolean writeToFile) {
    // 1) (Re-)Set the gateway counter
    this.gateway_counter = 0;
    this.flowblock_counter = 0;

    // 2) Initialize the BPMN factory
    String processName = generateProcessName(processModel);
    factory = new BPMNFactory(processName);
    factory.addLane(STANDARD_LANE_NAME);

    /*
      3) Model Synthesis Algorithm:
      "Draws" the structure of the process model
      - The model starts and ends in parallel gateways
      - Each task is surrounded by parallel gateways
     */
    HashMap<String, Split> drawnGateways = new HashMap<>();
    String initialGwName = generateGatewayName(BPMNFactory.SupportedGatewayDirection.SPLIT);
    Gateway initialGw = new Gateway(BPMNFactory.SupportedGatewayType.PARALLEL, initialGwName, BPMNFactory.SupportedGatewayDirection.SPLIT);
    drawnGateways.put(initialGwName, initialGw);
    LinkedHashMap<String, LinkedHashSet<String>> dependenciesMatrix = getDependenciesMatrix(processModel);

    for (Map.Entry<String, LinkedHashSet<String>> row : dependenciesMatrix.entrySet()) {
      factory.addTask(row.getKey(), "manual", processName);

      String mergeGwName = generateGatewayName(BPMNFactory.SupportedGatewayDirection.MERGE);
      String splitGwName = generateGatewayName(BPMNFactory.SupportedGatewayDirection.SPLIT);
      MergeTaskSplit gw = new MergeTaskSplit(row.getKey(), mergeGwName, splitGwName);
      drawnGateways.putIfAbsent(row.getKey(), gw);

      if (drawnGateways.keySet().containsAll(row.getValue())) {
        if (row.getValue().isEmpty()) { // No dependencies? Must be a start activity
          drawnGateways.get(initialGwName).addSuccessor(gw);
        }
        else {
          for (String dependencyId : row.getValue()) {
            drawnGateways.get(dependencyId).addSuccessor(gw);
            drawnGateways.get(row.getKey()).addPredecessor(drawnGateways.get(dependencyId));
          }
        }
      }
    }

    // 4) Connect all of the final tasks' following gateways towards a new, final gateway
    String finalGwName = generateGatewayName(BPMNFactory.SupportedGatewayDirection.MERGE);
    Gateway finalGw = new Gateway(BPMNFactory.SupportedGatewayType.PARALLEL, finalGwName, BPMNFactory.SupportedGatewayDirection.MERGE);
    drawnGateways.put(finalGwName, finalGw);

    for (Map.Entry<String, Split> row : drawnGateways.entrySet()) {
      if (row.getValue() instanceof MergeTaskSplit) {
        MergeTaskSplit gw = (MergeTaskSplit) row.getValue();
        factory.addOneSidedGateway(gw.getMerge(), BPMNFactory.SupportedGatewayType.PARALLEL, BPMNFactory.SupportedGatewayDirection.MERGE);
        factory.addOneSidedGateway(gw.getSplit(), BPMNFactory.SupportedGatewayType.PARALLEL, BPMNFactory.SupportedGatewayDirection.SPLIT);
      }
      else if (row.getValue() instanceof Split) {
        factory.addOneSidedGateway(row.getKey(), BPMNFactory.SupportedGatewayType.PARALLEL, BPMNFactory.SupportedGatewayDirection.SPLIT);
      }

      if (row.getValue() != finalGw && row.getValue().getSuccessors().isEmpty()) {
        row.getValue().addSuccessor(finalGw);
        finalGw.addPredecessor(row.getValue());
      }
    }

    // 5) Import the generated process model into the Workflow DSL
    importToWorkflowDSL(initialGw, finalGw);

    // 6) Write the Workflow DSL - encoded model into an artifact
    String workflowDSLEncoding = factory.buildString();
    if (writeToFile) {
      if (processModel.getUnderlyingProcess() != null) {
        writeWorkflowDSLFile(String.valueOf(processModel.getUnderlyingProcess().getGemId()), workflowDSLEncoding, true);
      }
      else {
        writeWorkflowDSLFile("", workflowDSLEncoding, false);
      }
    }

    return workflowDSLEncoding;
  }

  private static String removeForbiddenNameCharacters(String name) {
    name = name.replaceAll("\\s", "_"); // Replace each whitespace with an underscore
    for (String forbiddenCharacter : forbiddenNameCharacters) {
      name = name.replace(forbiddenCharacter, "");
    }
    return name;
  }

  public static String generateProcessName(ProcessModel pm) {
    if (pm.getName().isEmpty()) {
      return "anonymous_process_" + System.currentTimeMillis();
    }
    return removeForbiddenNameCharacters(pm.getName().toLowerCase());
  }

  public static String generateTaskName(Task task) {
    if (task.getName().isEmpty()) {
      return "anonymous_task_" + System.currentTimeMillis();
    }
    return removeForbiddenNameCharacters(task.getName().toLowerCase());
  }

  public String generateGatewayName(BPMNFactory.SupportedGatewayDirection direction) {
    gateway_counter++;
    return "and_gw_" + gateway_counter + "_" + direction.name().toLowerCase();
  }

  public String generateFlowBlockIdentifier() {
    flowblock_counter++;
    return "fb_" + flowblock_counter;
  }

  public List<Task> topologicallySortTasks(ProcessModel pm) throws Exception {
    // Using Kahn's algorithm for topological sorting
    // Thanks goes out to ChatGPT :)

    // Initialize in-degree map and adjacency list
    Map<Task, Integer> inDegree = new HashMap<>();
    Map<Task, List<Task>> graph = new HashMap<>();

    for (Task task : pm.getSubTasksList()) {
      inDegree.put(task, 0);
      graph.put(task, new ArrayList<>());
    }

    // Populate in-degree and adjacency list
    for (Task task : pm.getSubTasksList()) {
      for (Task dependency : task.getDependsOnList()) {
        graph.get(dependency).add(task); // Add obj as successor of pregenitor
        inDegree.put(task, inDegree.get(task) + 1); // Increment in-degree of obj
      }
    }

    // Add objects with in-degree 0 to the queue
    Queue<Task> queue = new LinkedList<>();
    for (Map.Entry<Task, Integer> entry : inDegree.entrySet()) {
      if (entry.getValue() == 0) {
        queue.offer(entry.getKey());
      }
    }

    // Process the queue
    List<Task> sortedOrder = new ArrayList<>();
    while (!queue.isEmpty()) {
      Task current = queue.poll();
      sortedOrder.add(current);

      // For each successor, decrease in-degree by 1
      for (Task successor : graph.get(current)) {
        inDegree.put(successor, inDegree.get(successor) - 1);
        // If in-degree becomes 0, add to queue
        if (inDegree.get(successor) == 0) {
          queue.offer(successor);
        }
      }
    }

    // Check for cycles
    if (sortedOrder.size() != pm.getSubTasksList().size()) {
      throw new Exception("Cycle detected! Topological sorting not possible");
    }

    return sortedOrder;
  }

  public LinkedHashMap<String, LinkedHashSet<String>> getDependenciesMatrix(ProcessModel processModel) {
    LinkedHashMap<String, LinkedHashSet<String>> matrix = new LinkedHashMap<>();
    ArrayList<Task> sortedTasks = null;

    try {
      sortedTasks = new ArrayList<>(topologicallySortTasks(processModel));
    }
    catch (Exception e) {
      System.err.println(e.getMessage());
    }

    for (Task task : sortedTasks) {
      LinkedHashSet<String> dependencies = new LinkedHashSet<>();
      for (Task dependency : task.getDependsOnList()) {
        dependencies.add(generateTaskName(dependency));
      }
      matrix.put(generateTaskName(task), dependencies);
    }

    return matrix;
  }

  private void importToWorkflowDSL(Split root, Split finalGw) {
    // Do the actual import into the Workflow DSL
    List<List<Split>> allPaths = dfs(root, finalGw, new HashMap<>());
    ArrayList<ArrayList<String>> blockElements = new ArrayList<>();
    ArrayList<String> flowElements = new ArrayList<>();
    flowElements.add(root.getId());

    for (List<Split> path : allPaths) {
      path.remove(0); // Remove initial gateway
      path.remove(path.size() - 1); // Remove final gateway

      ArrayList<String> branch = new ArrayList<>();
      for (Split split : path) {
        MergeTaskSplit gw = (MergeTaskSplit) split;
        branch.addAll(Arrays.asList(gw.getMerge(), gw.getId(), gw.getSplit()));
      }
      if (!branch.isEmpty()) {
        blockElements.add(branch);
      }
    }

    if (!blockElements.isEmpty()) {
      String flowBlockId = generateFlowBlockIdentifier();
      factory.addFlowBlock(flowBlockId, blockElements);
      flowElements.add(flowBlockId);
    }
    flowElements.add(finalGw.getId());
    factory.addProcessSequenceFlow(flowElements);
  }

  private List<List<Split>> dfs(Split currentNode, Split finalGw, Map<Split, List<List<Split>>> memo) {
    // Return a path containing only the end node, when reaching it
    if (currentNode == finalGw) {
      List<List<Split>> paths = new ArrayList<>();
      List<Split> singlePath = new ArrayList<>();
      singlePath.add(finalGw);
      paths.add(singlePath);
      return paths;
    }

    // Check if the paths from the current node are already memoized
    if (memo.containsKey(currentNode)) {
      return memo.get(currentNode);
    }

    List<List<Split>> allPaths = new ArrayList<>();

    // Perform DFS for each successor of the current node
    for (Split successor : currentNode.getSuccessors()) {
      List<List<Split>> successorPaths = dfs(successor, finalGw, memo);
      for (List<Split> path : successorPaths) {
        List<Split> newPath = new ArrayList<>();
        newPath.add(currentNode);
        newPath.addAll(path);
        allPaths.add(newPath);
      }
    }

    // Memoize the computed paths from the current node
    memo.put(currentNode, allPaths);
    return allPaths;
  }

  private void writeWorkflowDSLFile(String gemId, String content, boolean isAnonymousProcess) {
    if (!createOutputDirectory(STANDARD_OUTPUT_DIR)) {
      throw new IllegalArgumentException("Cannot save the Workflow DSL-encoding of process model of process with GemId " + gemId + ", since the the output directory cannot be created");
    }

    String fullFilePath = "";
    if (isAnonymousProcess) {
      fullFilePath = Paths.get(STANDARD_OUTPUT_DIR + "/" + "process_model_of_anonymous_process_" + System.currentTimeMillis() + "." + file_extension).normalize().toAbsolutePath().toString();
    }
    else {
      fullFilePath = Paths.get(STANDARD_OUTPUT_DIR + "/" + STANDARD_OUTPUT_FILE_NAME_PREFIX + gemId + "." + file_extension).normalize().toAbsolutePath().toString();
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullFilePath))) {
      writer.write(content);
    }
    catch (IOException e) {
      System.err.println("Cannot write the workflow DSL-encoded model of process model of process with GemId " + gemId + " into a file: " + e.getMessage());
    }
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
