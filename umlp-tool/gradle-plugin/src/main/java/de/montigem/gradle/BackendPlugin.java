/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle;

import de.montigem.gradle.specific.UMLP2JavaTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.AbstractCompile;
import org.gradle.jvm.tasks.Jar;
import org.gradle.language.jvm.tasks.ProcessResources;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

import static de.montigem.gradle.BasePlugin.MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG;
import static de.montigem.gradle.FrontendBasePlugin.getBooleanProjectProperty;
import static de.montigem.gradle.FrontendBasePlugin.getStringProjectProperty;

/***
 * A opinionated plugin which handles backend generation.
 * with only a single line "id 'de.rwth.se.montigem-backend'" in build.gradle
 * a UMLPTask is created and output is added to java sourceset.
 */
public abstract class BackendPlugin implements Plugin<Project> {

  final static String UMLP_BACKEND_TASK_NAME = "generateUmlpBackend";
  static final String UMLP_GROUP_NAME = "umlp";

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(BasePlugin.class);
    project.getPluginManager().apply("java");

    boolean local = getBooleanProjectProperty(project, "useLocalDependencies");

    TaskProvider<UMLP2JavaTask> umlpTask = createUMLPBackendTask(project, UMLP_BACKEND_TASK_NAME, local);
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
    project.getPluginManager().apply("de.rwth.se.montigem-testing");
  }

  // Add generated files to SourceSets
  private void addToSourceSet(Project project, UMLP2JavaTask umlpTask) {
    JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
    SourceSet main = javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
    main.getJava().srcDir(umlpTask.getOutputDir());

    // Add generated resources. e.g. SQL-Schemata
    main.getResources().srcDir(umlpTask.getOutputDir().dir("resources"));
  }

  protected static TaskProvider<UMLP2JavaTask> createUMLPBackendTask(final Project project, String umlpBackendTaskName, boolean local) {
    TaskProvider<UMLP2JavaTask> umlp2java = project.getTasks().register(umlpBackendTaskName, UMLP2JavaTask.class, umlpTask -> {
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

      // Include montigemModuleUMLPModels
      umlpTask.getModelDir().from(project.getConfigurations().getByName(BasePlugin.MONTIGEM_MODULE_UMLP_MODELS_CONFIG));

    });

    // Add Symbols and Tags of RTE ClassDiagrams to SymbolPath
    if (local) {
      Project rteClient = project.project(":umlp-rte:client");
      project.getDependencies().add(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG, rteClient);
      umlp2java.configure(task -> {
        task.getSymbolPath().from(project.getConfigurations().named(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG));
      });
    }
    else {
      project.getDependencies().add(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG,
          "umlp:client:" + getStringProjectProperty(project, "mc_version", "")
      );
    }
    return umlp2java;
  }

  private static void setIfPathExists(Consumer<File> add, Path path) {
    File f = path.toFile();
    if (f.exists()) {
      add.accept(f);
    }
  }
}
