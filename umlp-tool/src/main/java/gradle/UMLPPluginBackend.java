/* (c) https://github.com/MontiCore/monticore */
package gradle;

import gradle.specific.UMLPBackendTask;
import gradle.specific.serialize.SerializeCDSymbolsTask;
import gradle.specific.serialize.SerializeGUISymbolsTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.AbstractCompile;
import org.gradle.jvm.tasks.Jar;
import org.gradle.language.jvm.tasks.ProcessResources;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

import static gradle.UMLPPluginFrontendBase.getBooleanProjectProperty;

/***
 * A opinionated plugin which handles backend generation.
 * with only a single line "id 'de.rwth.se.umlp-tool-backend'" in build.gradle
 * a UMLPTask is created and output is added to java sourceset.
 */
public abstract class UMLPPluginBackend implements Plugin<Project> {

  final static String UMLP_BACKEND_TASK_NAME = "generateUmlpBackend";
  static final String UMLP_GROUP_NAME = "umlp";
  static final String GUI_LIB_NAME = "guiLib";

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(UMLPPluginBase.class);
    project.getPluginManager().apply("java");

    boolean local = getBooleanProjectProperty(project, "useLocalDependencies");
    System.out.println("useLocalDependencies is"+local);
    if (!local) {
      project.getConfigurations().register(GUI_LIB_NAME);
      System.out.println("Registered Configuration!");
      project.getDependencies().add(GUI_LIB_NAME, "umlp.umlp-rte:client:" +
          UMLPPluginFrontendBase.getStringProjectProperty(project, "mc_version", ""));
    }

    TaskProvider<UMLPBackendTask> umlpTask = createUMLPBackendTask(project, UMLP_BACKEND_TASK_NAME, local);
    // Generate Backend must run before JavaCompile
    project.getTasks().withType(AbstractCompile.class).configureEach(compile -> {
      compile.dependsOn(umlpTask);
    });
    // Generate Backend must run before ProcessResources (e.g. for SQL schemata)
    project.getTasks().withType(ProcessResources.class).configureEach(compile -> {
      compile.dependsOn(umlpTask);
    });

    addToSourceSet(project, umlpTask.get());

    // Lifecycle task "generate" which depends on Backend-Creation
    project.getTasks().register("generate", task -> task.dependsOn(umlpTask));

    // Lifecycle task "continuous". Intended use is with "--continuous" flag of gradle & HotReload
    project.getTasks().register("continuous", task -> task.dependsOn(project.getTasks().withType(Jar.class)));

    // Add dependency to jsweet-core, to import the annotations (such as @Erased)
    // Only during compilation of the backend
    // TODO: Use constant for jsweet-core
    project.getDependencies().add("compileOnly", "org.jsweet:jsweet-core:6.3.0");

    // apply last, since the test infrastructure tasks import properties from backend tasks
    project.getPluginManager().apply("de.rwth.se.umlp-tool-testing");
  }

  // Add generated files to SourceSets
  private void addToSourceSet(Project project, UMLPBackendTask umlpTask) {
    JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
    SourceSet main = javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
    main.getJava().srcDir(umlpTask.getOutputDir());

    // Add generated resources. e.g. SQL-Schemata
    main.getResources().srcDir(umlpTask.getOutputDir().dir("resources"));
  }

  protected static TaskProvider<UMLPBackendTask> createUMLPBackendTask(final Project project, String umlpBackendTaskName, boolean local) {
    final TaskProvider<UMLPBackendTask> result = project.getTasks().register(umlpBackendTaskName, UMLPBackendTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);

      setIfPathExists(umlpTask::setInput, Path.of(
          project.getProjectDir().getAbsolutePath(), "src", "models"));
      setIfPathExists(x -> umlpTask.getHwcDir().from(x), Path.of(
          project.getProjectDir().getAbsolutePath(), "src", "main", "java"));
      setIfPathExists(x -> umlpTask.getTmplDir().set(x), Path.of(
          project.getProjectDir().getAbsolutePath(), "src", "templates"));

      // Always set output dir (Gradle creates it, if not already exists)
      umlpTask.getOutputDir().set(Path.of(
          project.getBuildDir().getAbsolutePath(), "umlp", "backend").toFile());

      // Always create symbols
      umlpTask.getSymbolOutDir().set(Path.of(
          project.getBuildDir().getAbsolutePath(), "umlp", "backend_symbols").toFile());

      // Include umlpModulesModels
      umlpTask.getModelDir().from(project.getConfigurations().getByName(UMLPPluginBase.UMLP_MODULES_MODELS_CONFIG));

    });

    if (local) {
      Project umlpRteClientProject = project.findProject(":umlp-rte:client");
      boolean createGuiSymbolsPresent = !umlpRteClientProject.getTasksByName("createGuiSymbols", false).isEmpty();
      boolean createCDSymbolsPresent = !umlpRteClientProject.getTasksByName("createCDSymbols", false).isEmpty();
      if (createCDSymbolsPresent && createGuiSymbolsPresent) {
        SerializeGUISymbolsTask createGuiSymbols = (SerializeGUISymbolsTask) umlpRteClientProject.getTasks().getByName("createGuiSymbols");
        SerializeCDSymbolsTask createCDSymbols = (SerializeCDSymbolsTask) umlpRteClientProject.getTasks().getByName("createCDSymbols");
        result.get().dependsOn(createGuiSymbols, createCDSymbols);
        result.get().getSymbolPath().from(createGuiSymbols.getOutput(), createCDSymbols.getOutput());
      }
    }
    else {
      System.out.println("Trying to access guiLib config");
      Configuration guiLib = project.getConfigurations().getByName(GUI_LIB_NAME);
      result.get().getSymbolPath().from(
          project.provider(() -> guiLib.getFiles())
      );
    }
    return result;
  }

  private static void setIfPathExists(Consumer<File> add, Path path) {
    File f = path.toFile();
    if (f.exists()) {
      add.accept(f);
    }
  }
}
