package coveragedashboard.ingestion;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageModule;
import coveragedashboard.CoverageSourceFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
