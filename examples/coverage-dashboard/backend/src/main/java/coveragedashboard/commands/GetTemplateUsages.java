package coveragedashboard.commands;

import coveragedashboard.CoverageDashboardManager;
import coveragedashboard.CoverageModule;
import coveragedashboard.CoveragePerspective;
import coveragedashboard.CoverageSourceFile;
import jsweet.lang.Erased;
import umlp.backendrte.command.ListResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GetTemplateUsages extends GetTemplateUsagesTOP {

  public GetTemplateUsages(long template, long directPerspective) {
    super(template, directPerspective);
  }

  @Override
  public ListResult<CoverageSourceFile, List<CoverageSourceFile>> doAction(){
    Optional<List<CoverageSourceFile>> result = getTemplateUsages(this.getTemplateGemId(), this.getDirectPerspectiveGemId());
    if(result.isEmpty())
      return ListResult.error(
          getId(), CoverageSourceFile.class.getName());
    return ListResult.of(getId(), result.get(), CoverageSourceFile.class.getName());
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  protected Optional<List<CoverageSourceFile>> getTemplateUsages(long template, long directPerspective){
    Optional<CoverageSourceFile> templateOpt = CoverageDashboardManager.coverageSourceFileOpt(template);
    if(templateOpt.isEmpty())
      return Optional.empty();
    Optional<CoveragePerspective> perspectiveOpt = CoverageDashboardManager.coveragePerspectiveOpt(directPerspective);
    if(perspectiveOpt.isEmpty())
      return Optional.empty();

    List<CoverageSourceFile> usages = new ArrayList<>();
    collectUsages(templateOpt.get(), perspectiveOpt.get().getRootModule(), usages);
    return Optional.of(usages);
  }

  @Erased
  private void collectUsages(CoverageSourceFile template, CoverageModule module, List<CoverageSourceFile> usages){
    if(module == null)
      return;

    Set<CoverageSourceFile> visited = new HashSet<>();
    Set<CoverageSourceFile> usedBy = new LinkedHashSet<>(); // used to ensure same order every time
    collectFromModule(template, module, visited, usedBy);
    usages.addAll(usedBy);
  }

  @Erased
  private void collectFromModule(CoverageSourceFile template, CoverageModule module, Set<CoverageSourceFile> visited, Set<CoverageSourceFile> usedBy){
    if(module == null)
      return;

    for(CoverageSourceFile file : module.getSourceFilesList())
      traverseAndFindUsage(file, template, visited, usedBy);

    for(CoverageModule subModule : module.getSubModulesList())
      collectFromModule(template, subModule, visited, usedBy);
  }

  @Erased
  private void traverseAndFindUsage(CoverageSourceFile current, CoverageSourceFile template, Set<CoverageSourceFile> visited, Set<CoverageSourceFile> usedBy){
    if(current == null || !visited.add(current) || current.getGeneratedByList() == null){
      return;
    }

    for(CoverageSourceFile source : current.getGeneratedByList()){
      if(matches(template, source))
        usedBy.add(current);

      traverseAndFindUsage(source, template, visited, usedBy);
    }
  }

  @Erased
  private boolean matches(CoverageSourceFile template, CoverageSourceFile source) {
    return template.getPath().replace("\\", "/").equals(source.getPath().replace("\\", "/"));
  }
}
