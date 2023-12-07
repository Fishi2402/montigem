/* (c) https://github.com/MontiCore/monticore */
package gradle;

import gradle.specific.FrontendSetup;
import gradle.specific.NPMAuthCreate;
import gradle.specific.UMLPBackendTask;
import gradle.specific.UMLPFrontendTask;
import gradle.specific.UMLPGuiGenTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.tasks.Jar;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

/***
 * A opinionated plugin which handles frontend generation.
 * with only a single line "id 'de.rwth.se.umlp-tool-frontend'" in build.gradle some default-task for the frontend are created
 */
public abstract class UMLPPluginFrontend implements Plugin<Project> {

  final String UMLP_FRONTEND_SETUP_TASK_NAME = "frontendSetup";
  final String UMLP_FRONTEND_DOMAIN_TASK_NAME = "genDomain";
  final String UMLP_FRONTEND_GUI_TASK_NAME = "genGUI";
  final String UMLP_GROUP_NAME = "umlp";

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(UMLPPluginBase.class);
    project.getPluginManager().apply("base");

    // Add Configuration for TypeSymbols.
    Configuration guiTypes = project.getConfigurations().create("guiTypes");
    // TODO: Dependency als default hinzufügen: "de.monticore.lang:guidsl:${project.properties['mc_version']}:default-types"

    //Create Tasks
    TaskProvider<FrontendSetup> frontendSetupTask = createFrontendSetupTask(project);
    TaskProvider<UMLPFrontendTask> frontendDomainTask = createFrontendDomainTask(project);
    TaskProvider<UMLPGuiGenTask> frontendGuiTask = createFrontendGuiTask(project,guiTypes, frontendDomainTask);


    // Sync Output of UMLP-Task to the correct directory. This done because "npm serve" cannot handle the intermediate state while the UMLPTasks are running
    // and Hot-Reload aborts
    TaskProvider<?> syncDomainTask = project.getTasks().register("SYNC_" + UMLP_FRONTEND_DOMAIN_TASK_NAME, Sync.class, syncTask -> {
      syncTask.from(frontendDomainTask.get().getOutputDir());
      syncTask.into(Path.of(frontendSetupTask.get().getOutput().getAsFile().get().getAbsolutePath(), "build", "appdomain").toFile());
      syncTask.dependsOn(frontendGuiTask,frontendDomainTask,frontendSetupTask); // I want that the sync tasks runs after all generate tasks
    });
    TaskProvider<?> syncGuiTask = project.getTasks().register("SYNC_" + UMLP_FRONTEND_GUI_TASK_NAME, Sync.class, syncTask -> {
      syncTask.from(frontendGuiTask.get().getOutputDir());
      syncTask.into(Path.of(frontendSetupTask.get().getOutput().getAsFile().get().getAbsolutePath(), "build", "appgui").toFile());
      syncTask.dependsOn(frontendGuiTask,frontendDomainTask,syncDomainTask,frontendSetupTask); // I want that the sync tasks runs after all generate tasks
    });


    // Lifecycle task "generate"
    Task generateTask = project.getTasks().create("generate");
    generateTask.dependsOn(frontendGuiTask,syncDomainTask,syncGuiTask);

    // Lifecycle task "continuous". Intended use is with "--continuous" flag of gradle & HotReload
    Task continuousTask = project.getTasks().create("continuous");
    continuousTask.dependsOn(generateTask);
  }

  private TaskProvider<UMLPGuiGenTask> createFrontendGuiTask(final Project project, final Configuration guiTypes, TaskProvider<UMLPFrontendTask> frontendDomainTask) {
    return project.getTasks().register(UMLP_FRONTEND_GUI_TASK_NAME, UMLPGuiGenTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);

      umlpTask.getInput().from(Path.of(project.getProjectDir().getAbsolutePath(), "..", "models"));
      umlpTask.getModelDir().set(Path.of(project.getProjectDir().getAbsolutePath(), "..", "models").toFile());

      umlpTask.getSymbolDir().from(guiTypes);
      umlpTask.getSymbolDir().from(frontendDomainTask.get().getSymbolOutDir());

      setIfPathExists(x -> umlpTask.getHwcDir().set(x), Path.of(
          project.getProjectDir().getAbsolutePath(), "src", "app"));

      // It is important that the directory-depth of the intermediate-output is the the same as the final location (after sync!)
      // Because the frontend uses relative paths to include HWC Code
      umlpTask.getOutputDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "umlp_frontend", "intermediate", "appgui").toFile());
      umlpTask.getSymbolOutDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui").toFile());
    });
  }

  private TaskProvider<UMLPFrontendTask> createFrontendDomainTask(final Project project) {
    return project.getTasks().register(UMLP_FRONTEND_DOMAIN_TASK_NAME, UMLPFrontendTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);

      umlpTask.getInput().from(Path.of(project.getProjectDir().getAbsolutePath(), "..", "models"));

      setIfPathExists(x -> umlpTask.getHwcDir().set(x), Path.of(
          project.getProjectDir().getAbsolutePath(), "src", "app"));

      // It is important that the directory-depth of the intermediate-output is the the same as the final location (after sync!)
      // Because the frontend uses relative paths to include HWC Code
      umlpTask.getOutputDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "umlp_frontend", "intermediate","appdomain").toFile());
      umlpTask.getSymbolOutDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui_domain").toFile());
    });
  }

  private TaskProvider<FrontendSetup> createFrontendSetupTask(final Project project) {
    return project.getTasks().register(UMLP_FRONTEND_SETUP_TASK_NAME, FrontendSetup.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);

      setIfPathExists(x -> umlpTask.getInput().set(x), Path.of(
          project.getProjectDir().getAbsolutePath(), "config"));

      umlpTask.getOutput().set(Path.of(project.getBuildDir().getAbsolutePath(), "frontendGen").toFile());
    });

  }

  private void setIfPathExists(Consumer<File> add, Path path) {
    File f = path.toFile();
    if (f.exists()) {
      add.accept(f);
    }
  }
}
