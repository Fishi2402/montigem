package coveragedashboard.ingestion;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageLine;
import coveragedashboard.CoverageMetrics;
import coveragedashboard.CoverageModule;
import coveragedashboard.CoverageSourceFile;
import coveragedashboard.CoverageSourceFileBuilder;
import coveragedashboard.MeasuredLine;
import coveragedashboard.MeasuredLineBuilder;
import coveragedashboard.UnmappedLineBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
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

  public CoverageModule groupDependentFilesByExtension(String extension, List<CoverageSourceFile> flatFiles){
    String cleanExt = extension.startsWith(".") ? extension.toLowerCase() : "." + extension.toLowerCase();
    return groupDependentFiles(file -> file.getName() != null
        && file.getName().toLowerCase().endsWith(cleanExt), flatFiles);
  }

  public CoverageModule groupDependentFiles(Predicate<CoverageSourceFile> filter, List<CoverageSourceFile> flatFiles){
    List<CoverageSourceFile> contributors = new ArrayList<>();
    for(CoverageSourceFile file : flatFiles){
      Set<CoverageSourceFile> visited = new HashSet<>();
      collectTransitiveGeneratedBy(file, filter, contributors, visited);
    }

    // Group them by their path
    Map<String, List<CoverageSourceFile>> groupedFiles = contributors.stream()
        .collect(Collectors.groupingBy(CoverageSourceFile::getPath));

    // Merge metrics for each of those files
    List<CoverageSourceFile> aggregatedFiles = groupedFiles.entrySet().stream()
        .map(entry -> mergeCoverageData(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());

    return this.groupFilesInModules(aggregatedFiles);
  }

  private void collectTransitiveGeneratedBy(
      CoverageSourceFile file,
      Predicate<CoverageSourceFile> filter,
      List<CoverageSourceFile> result,
      Set<CoverageSourceFile> visited){
    if(file == null || file.getGeneratedByList() == null){
      return;
    }
    for(CoverageSourceFile gen : file.getGeneratedByList()){
      if(gen != null && visited.add(gen)){
        if(filter.test(gen)){
          result.add(gen);
        }
        collectTransitiveGeneratedBy(gen, filter, result, visited);
      }
    }
  }

  private CoverageSourceFile mergeCoverageData(String path, List<CoverageSourceFile> files){
    CoverageSourceFileBuilder mergedBuilder = CoverageDashboardManager.coverageSourceFileBuilder();
    mergedBuilder.setPath(path);
    mergedBuilder.setName(files.get(0).getName());
    mergedBuilder.setPhysicalPath(files.get(0).getPhysicalPath());
    mergedBuilder.setCategory(files.get(0).getCategory());

    // Now line-by-line, is covered if at least one of the source files covers it
    Map<Integer, MeasuredLineBuilder> mergedLinesBuilder = new HashMap<>();
    Map<Integer, UnmappedLineBuilder> unmappedLinesBuilder = new HashMap<>();
    for(CoverageSourceFile file : files){
      for(CoverageLine line : file.getLinesList()){
        int num = line.getLineNumber();

        if(line instanceof MeasuredLine measuredLine){
          // Check if there is already any known information for this
          MeasuredLineBuilder builder = mergedLinesBuilder.get(num);
          if(builder == null){
            builder = CoverageDashboardManager.measuredLineBuilder()
                .lineNumber(num)
                .coveredInstructions(measuredLine.getCoveredInstructions())
                .missedInstructions(measuredLine.getMissedInstructions())
                .coveredBranches(measuredLine.getCoveredBranches())
                .missedBranches(measuredLine.getMissedBranches());
            mergedLinesBuilder.put(num, builder);
          }else{
            // Line is covered if at least one contributing file covers it
            boolean prevCovered = builder.getCoveredInstructions() > 0 || builder.getCoveredBranches() > 0;
            boolean prevMissed = builder.getMissedInstructions() > 0 || builder.getMissedBranches() > 0;
            boolean nowCovered = measuredLine.getCoveredInstructions() > 0 || measuredLine.getCoveredBranches() > 0;
            boolean nowMissed = measuredLine.getMissedInstructions() > 0 || measuredLine.getMissedBranches() > 0;

            if (prevCovered || nowCovered) {
              builder.coveredInstructions(1);
              builder.missedInstructions(0);
              builder.coveredBranches(0);
              builder.missedBranches(0);
            } else if (prevMissed || nowMissed) {
              builder.coveredInstructions(0);
              builder.missedInstructions(1);
              builder.coveredBranches(0);
              builder.missedBranches(0);
            }
          }

          // Remove unmapped information
          unmappedLinesBuilder.remove(num);
        }else{
          // If the line is not a MeasuredLine, it's an unmapped line
          unmappedLinesBuilder.putIfAbsent(num, CoverageDashboardManager
              .unmappedLineBuilder().lineNumber(num));
        }
      }
    }
    Set<CoverageLine> mergedLines = new HashSet<>();
    for(MeasuredLineBuilder lineBuilder : mergedLinesBuilder.values()){
      mergedLines.add(lineBuilder.build().get());
    }
    for(UnmappedLineBuilder builder : unmappedLinesBuilder.values()){
      mergedLines.add(builder.build().get());
    }

    CoverageMetrics metrics = calculateMetrics(mergedLines);
    mergedBuilder.metrics(metrics);
    mergedBuilder.lines(mergedLines);
    return mergedBuilder.build().get();
  }

  private CoverageMetrics calculateMetrics(Set<CoverageLine> lines){
    int coveredInstructions = 0;
    int missedInstructions = 0;
    int coveredBranches = 0;
    int missedBranches = 0;
    int coveredLines = 0;
    int partialLines = 0;
    int missedLines = 0;
    int unmappedLines = 0;

    for (CoverageLine line : lines) {
      if(line instanceof MeasuredLine measuredLine) {
        coveredInstructions += measuredLine.getCoveredInstructions();
        missedInstructions += measuredLine.getMissedInstructions();

        coveredBranches += measuredLine.getCoveredBranches();
        missedBranches += measuredLine.getMissedBranches();

        int cov = measuredLine.getCoveredInstructions() + measuredLine.getCoveredBranches();
        int miss = measuredLine.getMissedInstructions() + measuredLine.getMissedBranches();
        if (cov > 0 && miss > 0) {
          partialLines++;
        } else if (cov > 0) {
          coveredLines++;
        } else {
          missedLines++;
        }
      }else{
        unmappedLines++;
      }
    }

    return CoverageDashboardManager.coverageMetricsBuilder()
        .coveredInstructions(coveredInstructions)
        .missedInstructions(missedInstructions)
        .coveredBranches(coveredBranches)
        .missedBranches(missedBranches)
        .coveredLines(coveredLines)
        .partialLines(partialLines)
        .missedLines(missedLines)
        .unmappedLines(unmappedLines)
        .build().get();
  }

  private void calculateMetricsRecursively(CoverageModule node){
    int coveredInstructions = 0;
    int missedInstructions = 0;
    int coveredBranches = 0;
    int missedBranches = 0;
    int coveredLines = 0;
    int partialLines = 0;
    int missedLines = 0;
    int unmappedLines = 0;

    // Add metrics from files on this module
    for(CoverageSourceFile file : node.getSourceFilesList()){
      coveredInstructions += file.getMetrics().getCoveredInstructions();
      missedInstructions += file.getMetrics().getMissedInstructions();
      coveredBranches += file.getMetrics().getCoveredBranches();
      missedBranches += file.getMetrics().getMissedBranches();
      coveredLines += file.getMetrics().getCoveredLines();
      partialLines += file.getMetrics().getPartialLines();
      missedLines += file.getMetrics().getMissedLines();
      unmappedLines += file.getMetrics().getUnmappedLines();
    }

    // Get metrics of submodules
    for(CoverageModule subModule : node.getSubModulesList()){
      calculateMetricsRecursively(subModule);
      coveredInstructions += subModule.getMetrics().getCoveredInstructions();
      missedInstructions += subModule.getMetrics().getMissedInstructions();
      coveredBranches += subModule.getMetrics().getCoveredBranches();
      missedBranches += subModule.getMetrics().getMissedBranches();
      partialLines += subModule.getMetrics().getPartialLines();
      coveredLines += subModule.getMetrics().getCoveredLines();
      missedLines += subModule.getMetrics().getMissedLines();
      unmappedLines += subModule.getMetrics().getUnmappedLines();
    }

    // Update own metrics
    node.getMetrics().setCoveredInstructions(coveredInstructions);
    node.getMetrics().setMissedInstructions(missedInstructions);
    node.getMetrics().setCoveredBranches(coveredBranches);
    node.getMetrics().setMissedBranches(missedBranches);
    node.getMetrics().setCoveredLines(coveredLines);
    node.getMetrics().setPartialLines(partialLines);
    node.getMetrics().setMissedLines(missedLines);
    node.getMetrics().setUnmappedLines(unmappedLines);
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
            .partialLines(0)
            .missedLines(0)
            .unmappedLines(0)
            .build().get()
        ).build().get();
  }


}
