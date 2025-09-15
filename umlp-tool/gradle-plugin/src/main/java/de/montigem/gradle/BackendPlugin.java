/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle;

import de.monticore.gradle.common.CommonMCTask;
import de.montigem.gradle.specific.UMLP2JavaTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.AbstractCompile;
import org.gradle.jvm.tasks.Jar;
import org.gradle.language.jvm.tasks.ProcessResources;

import java.io.File;

import static de.montigem.gradle.FrontendBasePlugin.UMLP_GROUP_NAME;
import static de.montigem.gradle.FrontendBasePlugin.getBooleanProjectProperty;
import static de.montigem.gradle.FrontendBasePlugin.getModels;
import static de.montigem.gradle.FrontendBasePlugin.isLocal;
import static de.montigem.gradle.FrontendBasePlugin.mcVersion;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG;

/***
 * A opinionated plugin which handles backend generation.
 * with only a single line "id 'de.rwth.se.montigem-backend'" in build.gradle
 * a UMLPTask is created and output is added to java sourceset.
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

    TaskProvider<UMLP2JavaTask> umlp2java = createUMLP2JavaTask(project);
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
    project.getTasks().register("generate", task -> task.dependsOn(umlp2java));

    // Lifecycle task "continuous". Intended use is with "--continuous" flag of gradle & HotReload
    project.getTasks().register("continuous", task -> task.dependsOn(project.getTasks().withType(Jar.class)));

    // Add dependency to jsweet-core, to import the annotations (such as @Erased)
    // Only during compilation of the backend
    project.getDependencies().add("compileOnly", "org.jsweet:jsweet-core:6.3.0");

    // apply last, since the test infrastructure tasks import properties from backend tasks
    project.getPluginManager().apply("de.rwth.se.montigem-testing");
  }

  protected static TaskProvider<UMLP2JavaTask> createUMLP2JavaTask(Project project) {
    Directory srcJava = project.getLayout().getProjectDirectory().dir("src/main/java");
    Directory srcTpl = project.getLayout().getProjectDirectory().dir("src/templates");
    return project.getTasks().register(UMLP2JAVA_TASK, UMLP2JavaTask.class, task -> {
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
      task.getModelDir().from(project.getConfigurations().getByName(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG));
    });
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
