/* (c) https://github.com/MontiCore/monticore */
package gradle;

import gradle.specific.FrontendSetup;
import gradle.specific.UMLPFrontendTask;
import gradle.specific.UMLPGuiGenTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskProvider;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

/***
 * A opinionated plugin which handles frontend generation.
 * with only a single line "id 'de.rwth.se.umlp-tool-frontend'" in build.gradle some default-task for the frontend are created
 */
public abstract class UMLPPluginFrontendNonJSweet implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(UMLPPluginFrontendBase.class);

    TaskProvider<UMLPFrontendTask> frontendDomainTask = createFrontendDomainTask(project);
    TaskProvider<UMLPGuiGenTask> frontendGuiTask = project.getTasks().named(UMLPPluginFrontendBase.UMLP_FRONTEND_GUI_TASK_NAME, UMLPGuiGenTask.class);
    TaskProvider<Sync> syncDomainTask = project.getTasks().named("SYNC_" + UMLPPluginFrontendBase.UMLP_FRONTEND_DOMAIN_TASK_NAME, Sync.class);
    TaskProvider<Sync> syncGuiTask = project.getTasks().named("SYNC_" + UMLPPluginFrontendBase.UMLP_FRONTEND_GUI_TASK_NAME, Sync.class);

    TaskProvider<FrontendSetup> frontendSetupTask = project.getTasks().named(UMLPPluginFrontendBase.UMLP_FRONTEND_SETUP_TASK_NAME, FrontendSetup.class);

    frontendGuiTask.configure(umlpTask -> {
      setIfPathExists(x -> umlpTask.getHwcDir().from(x), Path.of(project.getProjectDir().getAbsolutePath(), "src", "app"));
      umlpTask.getGuiGenJsweet().set(false);
    });

    // task dependencies
    frontendGuiTask.configure(umlpTask -> {
      // Ensure our symbols exist
      umlpTask.dependsOn(frontendDomainTask.get());
    });

    syncDomainTask.configure(umlpTask -> {
      umlpTask.from(frontendDomainTask.get().getOutputDir());
      umlpTask.dependsOn(frontendDomainTask.get(), frontendSetupTask.get());
    });

    syncGuiTask.configure(umlpTask -> {
      umlpTask.from(frontendGuiTask.get().getOutputDir());
      umlpTask.dependsOn(frontendGuiTask, frontendSetupTask);
    });

    project.getTasks().named("generate").configure(generateTask -> {
      generateTask.dependsOn(frontendGuiTask);
    });

  }

  protected TaskProvider<UMLPFrontendTask> createFrontendDomainTask(final Project project) {
    return project.getTasks().register(UMLPPluginFrontendBase.UMLP_FRONTEND_DOMAIN_TASK_NAME, UMLPFrontendTask.class, umlpTask -> {
      umlpTask.setGroup(UMLPPluginFrontendBase.UMLP_GROUP_NAME);
      umlpTask.getInput().from(UMLPPluginFrontendBase.getModelsPath(project));
      umlpTask.getOcl().from(UMLPPluginFrontendBase.getOCLPath(project));
      setIfPathExists(x -> umlpTask.getHwcDir().from(x), Path.of(project.getProjectDir().getAbsolutePath(), "src", "app"));

      // It is important that the directory-depth of the intermediate-output is the the same as the final location (after sync!)
      // Because the frontend uses relative paths to include HWC Code
      umlpTask.getOutputDir().set(UMLPPluginFrontendBase.getAppDomainPath(project).toFile());
      // Always create symbols & tag files
      umlpTask.getSymbolOutDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui_domain_domain__from_task_frontenddomain").toFile());

    });
  }

  private void setIfPathExists(Consumer<File> add, Path path) {
    File f = path.toFile();
    if (f.exists()) {
      add.accept(f);
    }
  }

}
