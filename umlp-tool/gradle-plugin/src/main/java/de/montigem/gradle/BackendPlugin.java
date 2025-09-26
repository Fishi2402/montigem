/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle;

import de.monticore.gradle.common.CommonMCTask;
import de.montigem.gradle.specific.UMLP2JavaTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.Directory;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.AbstractCompile;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.logging.TestLogEvent;
import org.gradle.jvm.tasks.Jar;
import org.gradle.language.jvm.tasks.ProcessResources;

import static de.montigem.gradle.FrontendBasePlugin.UMLP_GROUP_NAME;
import static de.montigem.gradle.FrontendBasePlugin.getModels;
import static de.montigem.gradle.FrontendBasePlugin.isLocal;
import static de.montigem.gradle.FrontendBasePlugin.mcVersion;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG;
import static de.montigem.gradle.module.ModulePlugin.GENERATOR_EXTENSION_COMPILE_CONFIG;

/***
 * MontiGem app backend generation.
 * - Sets minimal dependencies
 * - Adds tasks:
 *   - `umlp2java`
 *   - `generate`
 *   - `continuos`
 *   - `slowtest`
 */
public abstract class BackendPlugin implements Plugin<Project> {

  public final static String UMLP2JAVA_TASK = "umlp2java";

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(MGBasePlugin.class);
    project.getPluginManager().apply("java");

    var mainSourceSet = project.getExtensions().getByType(JavaPluginExtension.class)
        .getSourceSets().named(SourceSet.MAIN_SOURCE_SET_NAME);
    addRTEDependency(project);

    // set up generator extension source set for the cases when an app needs one
    Configuration generationExtensionCompile = project.getConfigurations().create(GENERATOR_EXTENSION_COMPILE_CONFIG);
    generationExtensionCompile.setCanBeResolved(true);
    generationExtensionCompile.setCanBeConsumed(false);
    project.getDependencies().add(generationExtensionCompile.getName(), "de.monticore:monticore-runtime:" + mcVersion(project));
    project.getDependencies().add(generationExtensionCompile.getName(), "de.monticore:monticore-grammar:" + mcVersion(project));
    project.getDependencies().add(generationExtensionCompile.getName(), "umlp:umlp-tool:" + mcVersion(project));

    SourceSet generatorExtensionSourceSet = project.getExtensions().getByType(JavaPluginExtension.class)
        .getSourceSets().create("generatorExtension");
    generatorExtensionSourceSet.setCompileClasspath(
        generatorExtensionSourceSet.getCompileClasspath().plus(generationExtensionCompile)
    );

    project.getTasks()
        .named(generatorExtensionSourceSet.getCompileJavaTaskName(), JavaCompile.class)
        .configure(task -> task.onlyIf(
            t -> generatorExtensionSourceSet.getJava().getSrcDirs().stream()
                .anyMatch(dir -> dir.exists() && dir.listFiles() != null && dir.listFiles().length > 0)
        ));

    Directory srcJava = project.getLayout().getProjectDirectory().dir("src/main/java");
    Directory srcTpl = project.getLayout().getProjectDirectory().dir("src/generatorExtension/resources");
    TaskProvider<UMLP2JavaTask> umlp2java = project.getTasks().register(UMLP2JAVA_TASK, UMLP2JavaTask.class, task -> {
      task.setGroup(UMLP_GROUP_NAME);

      task.getInput().from(getModels(project));
      if (srcJava.getAsFile().exists()) {
        task.getHandWrittenCodeDir().from(srcJava);
      }
      if (srcTpl.getAsFile().exists()) {
        task.getTmplDir().set(srcTpl);
      }
      task.getOutputDir().set(project.getLayout().getBuildDirectory().dir("umlp2java"));
      task.getSymbolOutDir().set(project.getLayout().getBuildDirectory().dir("umlp2sym"));
      task.getModelDir().from(project.getConfigurations().named(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG));
    });

    mainSourceSet.configure(sourceSet -> {
      project.getTasks().withType(AbstractCompile.class)
          .matching(t -> t.getName().equals(sourceSet.getCompileJavaTaskName()))
          .configureEach(compile -> compile.dependsOn(umlp2java));

      // Generate Backend must run before ProcessResources (e.g. for SQL schemata)
      project.getTasks().withType(ProcessResources.class)
          .matching(t -> t.getName().equals(sourceSet.getProcessResourcesTaskName()))
          .configureEach(procResMain -> procResMain.dependsOn(umlp2java));

      sourceSet.getJava().srcDir(umlp2java.flatMap(CommonMCTask::getOutputDir));
      // Add generated resources. e.g. SQL-Schemata
      sourceSet.getResources().srcDir(umlp2java.flatMap(t -> t.getOutputDir().dir("resources")));
    });

    // Lifecycle task "generate" which depends on Backend-Creation
    project.getTasks().register("generate", task -> {
      task.setGroup(UMLP_GROUP_NAME);
      task.dependsOn(umlp2java);
    });

    // Lifecycle task "runServer" which depends on Backend-Creation
    project.getTasks().register("runServer", task -> {
      task.setGroup("application");
      task.dependsOn(project.getTasks().named("bootRun"));
    });

    // Lifecycle task "continuous". Intended use is with "--continuous" flag of gradle & HotReload
    project.getTasks().register("continuous", task -> {
      task.setGroup("application");
      task.dependsOn(project.getTasks().withType(Jar.class));
    });

    // Add dependency to jsweet-core, to import the annotations (such as @Erased)
    // Only during compilation of the backend
    project.getDependencies().add("compileOnly", "org.jsweet:jsweet-core:6.3.0");

    project.getTasks().register("slowtest", Test.class, task -> {
      task.setGroup("verification");
      task.useJUnitPlatform(junitPlatform ->
          junitPlatform.includeTags("SlowTest")
      );
      task.shouldRunAfter(project.getTasks().named("test"));
    });

    project.getTasks().withType(Test.class).configureEach(test -> {
      test.testLogging(logging ->
          logging.events(
              TestLogEvent.PASSED,
              TestLogEvent.SKIPPED,
              TestLogEvent.FAILED,
              TestLogEvent.STANDARD_OUT,
              TestLogEvent.STANDARD_ERROR
          )
      );
      test.useJUnitPlatform();
    });

    // apply last, since the test infrastructure tasks import properties from backend tasks
    project.getPluginManager().apply("de.rwth.se.montigem-testing");
  }

  protected void addRTEDependency(Project project) {
    // Add Symbols and Tags of RTE ClassDiagrams to SymbolPath
    if (isLocal(project)) {
      Project rte = project.project(":umlp-rte");
      project.getDependencies().add(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG, rte);
    }
    else {
      project.getDependencies().add(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG, "umlp:umlp-rte:" + mcVersion(project));
    }
  }

}
