package coveragedashboard.ingestion;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageLine;
import coveragedashboard.CoverageLineBuilder;
import coveragedashboard.CoverageMetrics;
import coveragedashboard.CoverageModule;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.CoverageSourceFileBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class CoverageModuleAggregator {

  public CoverageModule groupFilesInModules(List<CoverageSourceFile> flatFiles){

    // Create root module
    CoverageModule root = getEmptyModule("");



    for(CoverageSourceFile file : flatFiles){
      Path path = Paths.get(file.getPath());
      Path parentPath = path.getParent();

      CoverageModule currentModule = root;
      if(parentPath != null){
        // walk down path and create submodules on the way if necessary
        for(int i = 0; i < parentPath.getNameCount(); i++){
          String name = parentPath.getName(i).toString();
          currentModule = getOrCreateSubModule(currentModule, name);
        }
      }

      currentModule.addSourceFiles(file);
    }

    // roll up metrics from bottom to top
    calculateMetricsRecursively(root);

    return root;
  }

  public CoverageModule groupDependentFiles(int level, List<CoverageSourceFile> flatFiles){
    CoverageModule root = getEmptyModule("");

    List<CoverageSourceFile> sourceFiles = new ArrayList<CoverageSourceFile>();

    // At first, get all files for the correct level
    List<CoverageSourceFile> contributors = new ArrayList<>();
    for(CoverageSourceFile file : flatFiles){
      contributors.addAll(getGeneratedByAtLevel(file, level));
    }

    // Group them by there path
    Map<String, List<CoverageSourceFile>> groupedFiles = contributors.stream()
        .collect(Collectors.groupingBy(CoverageSourceFile::getPath));

    // Merge metrics for each of those files
    List<CoverageSourceFile> aggregatedFiles = groupedFiles.entrySet().stream()
        .map(entry -> mergeCoverageData(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());

    return root;
  }

  private CoverageSourceFile mergeCoverageData(String path, List<CoverageSourceFile> files){
    CoverageSourceFileBuilder mergedBuilder = CoverageDashboardManager.coverageSourceFileBuilder();
    mergedBuilder.setPath(path);
    mergedBuilder.setName(files.get(0).getName());
    mergedBuilder.setPhysicalPath(files.get(0).getPhysicalPath());
    mergedBuilder.setCategory(files.get(0).getCategory());

    // Now line-by-line, is covered if at least one of the source files covers it
    Map<Integer, CoverageLineBuilder> mergedLinesBuilder = new HashMap<>();
    for(CoverageSourceFile file : files){
      for(CoverageLine line : file.getLinesList()){
        int num = line.getLineNumber();
        CoverageLineBuilder builder = mergedLinesBuilder.get(num);
        if(builder == null){
          builder = CoverageDashboardManager.coverageLineBuilder()
              .lineNumber(num)
              .coveredInstructions(line.getCoveredInstructions())
              .missedInstructions(line.getMissedInstructions())
              .coveredBranches(line.getCoveredBranches())
              .missedBranches(line.getMissedBranches());
          mergedLinesBuilder.put(num, builder);
        }else{
          builder.coveredInstructions(builder.getCoveredInstructions() + line.getCoveredInstructions());
          builder.missedInstructions(builder.getMissedInstructions() + line.getMissedInstructions());
          builder.coveredBranches(builder.getCoveredBranches() + line.getCoveredBranches());
          builder.missedBranches(builder.getMissedBranches() + line.getMissedBranches());
        }
      }
    }
    Set<CoverageLine> mergedLines = new HashSet<>();
    for(CoverageLineBuilder lineBuilder : mergedLinesBuilder.values()){
      mergedLines.add(lineBuilder.build().get());
    }

    CoverageMetrics metrics = calculateMetrics(mergedLines);
    mergedBuilder.metrics(metrics);
    mergedBuilder.lines(mergedLines);
    return mergedBuilder.build().get();
  }

  private List<CoverageSourceFile> getGeneratedByAtLevel(CoverageSourceFile file, int level){
    if(file == null || level < 1){
      return Collections.emptyList();
    }

    List<CoverageSourceFile> currentLevel = Collections.singletonList(file);
    for(int step = 1; step <= level; step++){
      List<CoverageSourceFile> nextLevel = new ArrayList<>();
      for(CoverageSourceFile current : currentLevel){
        if(current.getGeneratedByList() != null){
          nextLevel.addAll(current.getGeneratedByList());
        }
      }
      if(nextLevel.isEmpty()){
        return Collections.emptyList();
      }
      currentLevel = nextLevel;
    }
    return currentLevel;
  }

  private CoverageMetrics calculateMetrics(Set<CoverageLine> lines){
    int coveredInstructions = 0;
    int missedInstructions = 0;
    int coveredBranches = 0;
    int missedBranches = 0;
    int coveredLines = 0;
    int missedLines = 0;

    for (CoverageLine line : lines) {
      coveredInstructions += line.getCoveredInstructions();
      missedInstructions += line.getMissedInstructions();

      coveredBranches += line.getCoveredBranches();
      missedBranches += line.getMissedBranches();

      if (line.getCoveredInstructions() > 0) {
        coveredLines++;
      } else {
        missedLines++;
      }
    }

    return CoverageDashboardManager.coverageMetricsBuilder()
        .coveredInstructions(coveredInstructions)
        .missedInstructions(missedInstructions)
        .coveredBranches(coveredBranches)
        .missedBranches(missedBranches)
        .coveredLines(coveredLines)
        .missedLines(missedLines)
        .build().get();
  }

  private void calculateMetricsRecursively(CoverageModule node){
    int coveredInstructions = 0;
    int missedInstructions = 0;
    int coveredBranches = 0;
    int missedBranches = 0;
    int coveredLines = 0;
    int missedLines = 0;

    // Add metrics from files on this module
    for(CoverageSourceFile file : node.getSourceFilesList()){
      coveredInstructions += file.getMetrics().getCoveredInstructions();
      missedInstructions += file.getMetrics().getMissedInstructions();
      coveredBranches += file.getMetrics().getCoveredBranches();
      missedBranches += file.getMetrics().getMissedBranches();
      coveredLines += file.getMetrics().getCoveredLines();
      missedLines += file.getMetrics().getMissedLines();
    }

    // Get metrics of submodules
    for(CoverageModule subModule : node.getSubModulesList()){
      calculateMetricsRecursively(subModule);
      coveredInstructions += subModule.getMetrics().getCoveredInstructions();
      missedInstructions += subModule.getMetrics().getMissedInstructions();
      coveredBranches += subModule.getMetrics().getCoveredBranches();
      missedBranches += subModule.getMetrics().getMissedBranches();
      coveredLines += subModule.getMetrics().getCoveredLines();
      missedLines += subModule.getMetrics().getMissedLines();
    }

    // Update own metrics
    node.getMetrics().setCoveredInstructions(coveredInstructions);
    node.getMetrics().setMissedInstructions(missedInstructions);
    node.getMetrics().setCoveredBranches(coveredBranches);
    node.getMetrics().setMissedBranches(missedBranches);
    node.getMetrics().setCoveredLines(coveredLines);
    node.getMetrics().setMissedLines(missedLines);
  }

  private CoverageModule getOrCreateSubModule(CoverageModule currentModule, String name){
    // Check it if already exists as child
    for(CoverageModule child : currentModule.getSubModulesList()){
      if(child.getName().equals(name)){
        return child;
      }
    }
    // Create new submodule, add to parent and return it
    CoverageModule subModule = getEmptyModule(name);
    currentModule.addSubModules(subModule);
    return subModule;
  }

  private CoverageModule getEmptyModule(String name){
    return CoverageDashboardManager.coverageModuleBuilder()
        .name(name)
        .metrics(CoverageDashboardManager.coverageMetricsBuilder()
            .coveredInstructions(0)
            .missedInstructions(0)
            .coveredBranches(0)
            .missedBranches(0)
            .coveredLines(0)
            .missedLines(0)
            .build().get()
        ).build().get();
  }


}
