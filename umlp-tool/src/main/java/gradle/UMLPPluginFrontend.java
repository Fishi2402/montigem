/* (c) https://github.com/MontiCore/monticore */
package gradle;

import gradle.specific.FrontendSetup;
import gradle.specific.NPMAuthCreate;
import gradle.specific.UMLPBackendTask;
import gradle.specific.UMLPFrontendTask;
import gradle.specific.UMLPGuiGenTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.TaskProvider;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

/***
 * A opinionated plugin which handles frontend generation.
 * with only a single line "id 'de.rwth.se.umlp-tool-frontend'" in build.gradle some default-task for the frontend are created
 */
public abstract class UMLPPluginFrontend implements Plugin<Project> {

  final String UMLP_AUTH_TASK_NAME = "generateNPMAuth";
  final String UMLP_FRONTEND_SETUP_TASK_NAME = "frontendSetup";
  final String UMLP_FRONTEND_DOMAIN_TASK_NAME = "genDomain";
  final String UMLP_FRONTEND_GUI_TASK_NAME = "genGUI";

  final String UMLP_GROUP_NAME = "umlp";

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(UMLPPluginBase.class);
    project.getPluginManager().apply("base");

    Configuration guiTypes = project.getConfigurations().create("guiTypes");
    // TODO: Dependency als default hinzufügen: "de.monticore.lang:guidsl:${project.properties['mc_version']}:default-types"

    // TODO: Was, wenn der "config" Ordner nicht existiert?
    TaskProvider<?> authTask = createAuthTask(project);

    TaskProvider<?> frontendSetupTask = createFrontendSetupTask(project);
    frontendSetupTask.get().dependsOn(authTask);

    TaskProvider<UMLPFrontendTask> frontendDomainTask = createFrontendDomainTask(project);
    frontendDomainTask.get().dependsOn(frontendSetupTask);

    TaskProvider<?> frontendGuiTask = createFrontendGuiTask(project,guiTypes, frontendDomainTask);
    frontendGuiTask.get().dependsOn(frontendSetupTask);

  }

  private TaskProvider<UMLPGuiGenTask> createFrontendGuiTask(final Project project, final Configuration guiTypes, TaskProvider<UMLPFrontendTask> frontendDomainTask) {
    final TaskProvider<UMLPGuiGenTask> result = project.getTasks().register(UMLP_FRONTEND_GUI_TASK_NAME, UMLPGuiGenTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);

      umlpTask.getInput().from(Path.of(project.getProjectDir().getAbsolutePath(), "..", "models"));
      umlpTask.getModelDir().set(Path.of(project.getProjectDir().getAbsolutePath(), "..", "models").toFile());

      umlpTask.getSymbolDir().from(guiTypes);
      umlpTask.getSymbolDir().from(frontendDomainTask.get().getSymbolOutDir());


      // TODO: von FrontendSetup Ordner übernehmen
      umlpTask.getOutputDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "frontendGen", "build", "appgui").toFile());

      umlpTask.getSymbolOutDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui").toFile());
      umlpTask.getHwcDir().set(Path.of(project.getProjectDir().getAbsolutePath(), "src","app").toFile());
    });
    return result;
  }

  private TaskProvider<UMLPFrontendTask> createFrontendDomainTask(final Project project) {
    final TaskProvider<UMLPFrontendTask> result = project.getTasks().register(UMLP_FRONTEND_DOMAIN_TASK_NAME, UMLPFrontendTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);

      umlpTask.getInput().from(Path.of(project.getProjectDir().getAbsolutePath(), "..", "models"));

      // TODO: von FrontendSetup Ordner übernehmen
      umlpTask.getOutputDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "frontendGen", "build", "appdomain").toFile());

      umlpTask.getSymbolOutDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols").toFile());
      umlpTask.getHwcDir().set(Path.of(project.getProjectDir().getAbsolutePath(), "src","app").toFile());
    });
    return result;
  }

  private TaskProvider<NPMAuthCreate> createAuthTask(final Project project) {
    final TaskProvider<NPMAuthCreate> result = project.getTasks().register(UMLP_AUTH_TASK_NAME, NPMAuthCreate.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);

      umlpTask.getNpmrcFile().set(Path.of(
          project.getProjectDir().getAbsolutePath(), "config", ".npmrc").toFile());
      /*setIfPathExists(f -> umlpTask.getNpmrcFile().set(f), Path.of(
          project.getProjectDir().getAbsolutePath(), "config", ".npmrc"));
       */
    });
    return result;
  }

  private TaskProvider<FrontendSetup> createFrontendSetupTask(final Project project) {
    final TaskProvider<FrontendSetup> result = project.getTasks().register(UMLP_FRONTEND_SETUP_TASK_NAME, FrontendSetup.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);

      umlpTask.getInput().set(Path.of(project.getProjectDir().getAbsolutePath(), "config").toFile());
      umlpTask.getOutput().set(Path.of(project.getBuildDir().getAbsolutePath(), "frontendGen/").toFile());
    });
    return result;

  }

  protected TaskProvider<UMLPBackendTask> createUMLPBackendTask(final Project project) {
    final TaskProvider<UMLPBackendTask> result = project.getTasks().register(UMLP_AUTH_TASK_NAME, UMLPBackendTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);

      setIfPathExists(umlpTask::setInput, Path.of(
          project.getProjectDir().getAbsolutePath(), "src", "models"));
      setIfPathExists(x -> umlpTask.getHwcDir().set(x), Path.of(
          project.getProjectDir().getAbsolutePath(), "src", "main", "java"));
      setIfPathExists(x -> umlpTask.getTmplDir().set(x), Path.of(
          project.getProjectDir().getAbsolutePath(), "src", "templates"));

      // Always set output dir (Gradle creates it, if not already exists)
      umlpTask.getOutputDir().set(Path.of(
          project.getBuildDir().getAbsolutePath(), "umlp", "backend").toFile());
    });
    return result;
  }

  private void setIfPathExists(Consumer<File> add, Path path) {
    File f = path.toFile();
    if (f.exists()) {
      add.accept(f);
    }
  }
}
