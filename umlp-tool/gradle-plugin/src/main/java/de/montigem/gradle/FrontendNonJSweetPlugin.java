/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle;

import de.monticore.gradle.common.CommonMCTask;
import de.montigem.gradle.specific.CreateSymlinkTask;
import de.montigem.gradle.specific.GUI2JavaTask;
import de.montigem.gradle.specific.SetupFrontendTask;
import de.montigem.gradle.specific.UMLP2TSTask;
import org.gradle.api.NamedDomainObjectProvider;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.Directory;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static de.montigem.gradle.FrontendBasePlugin.CREATE_SYMLINK_TASK;
import static de.montigem.gradle.FrontendBasePlugin.GENERATE_TASK;
import static de.montigem.gradle.FrontendBasePlugin.GUI2JAVA_TASK;
import static de.montigem.gradle.FrontendBasePlugin.GUI_TYPES_CONFIG;
import static de.montigem.gradle.FrontendBasePlugin.SETUP_FRONTEND_TASK;
import static de.montigem.gradle.FrontendBasePlugin.UMLP2TS_TASK;
import static de.montigem.gradle.FrontendBasePlugin.UMLP_GROUP_NAME;
import static de.montigem.gradle.FrontendBasePlugin.getModels;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_IMPORT_MAP_CONFIG;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_UMLP_MODELS_CONFIG;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_UMLP_SYMBOLS_CONFIG;

/**
 * The specific gradle setup for non-jsweet projects
 */
@Deprecated
public abstract class FrontendNonJSweetPlugin implements Plugin<Project> {

  public static final String SYNC_UMLP2TS_TASK = "sync_umlp2ts";
  public static final String SYNC_GUI2JAVA_TASK = "sync_gui2java";

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(FrontendBasePlugin.class);

    Provider<RegularFile> hwcLink = project.getLayout().getBuildDirectory().file("app/hwcsrc");
    Provider<RegularFile> intermediateHWCLink = project.getLayout().getBuildDirectory().file("hwcsrc");

    TaskProvider<UMLP2TSTask> umlp2ts = createUMLP2TSTask(project);
    TaskProvider<GUI2JavaTask> gui2java = createGUI2JavaTask(project);
    gui2java.configure(task ->
        task.getSymbolPath().from(umlp2ts.flatMap(UMLP2TSTask::getSymbolOutDir))
    );

    TaskProvider<CreateSymlinkTask> createSymlinkTask =
        project.getTasks().named(CREATE_SYMLINK_TASK, CreateSymlinkTask.class);

    gui2java.configure(task -> {
      task.getHwcDir().from(intermediateHWCLink);
      task.getGUIGenJSweet().set(false);
      task.dependsOn(umlp2ts, createSymlinkTask);
    });

    TaskProvider<SetupFrontendTask> setupFrontend = project.getTasks().named(SETUP_FRONTEND_TASK, SetupFrontendTask.class);
    // Sync Output of a task to the correct directory. This done because "npm serve" cannot
    // handle the intermediate state while the UMLPTasks are running and Hot-Reload aborts
    Provider<Directory> syncUMLP2TSOut = setupFrontend.flatMap(
        t -> t.getOutput().map(dir -> dir.dir("umlp2ts"))
    );
    TaskProvider<Sync> syncUMLP2TSTask = project.getTasks().register(SYNC_UMLP2TS_TASK, Sync.class, task -> {
      task.setGroup(UMLP_GROUP_NAME);
      task.from(umlp2ts.flatMap(CommonMCTask::getOutputDir));
      task.into(syncUMLP2TSOut);
    });

    Provider<Directory> syncGUI2JavaOut = setupFrontend.flatMap(
        t -> t.getOutput().map(dir -> dir.dir("gui2java"))
    );
    TaskProvider<Sync> syncGUI2JavaTask = project.getTasks().register(SYNC_GUI2JAVA_TASK, Sync.class, task -> {
      task.setGroup(UMLP_GROUP_NAME);
      task.from(gui2java.flatMap(CommonMCTask::getOutputDir));
      task.into(syncGUI2JavaOut);
      task.exclude("**/*.java");
    });
    project.getTasks().named(GENERATE_TASK).configure(task ->
        task.dependsOn(syncUMLP2TSTask, syncGUI2JavaTask)
    );

    createSymlinkTask.configure(task -> {
      task.getLinks().put(
          hwcLink.get().getAsFile(),
          project.getLayout().getProjectDirectory().dir("src/app").getAsFile()
      );

      task.getLinks().put(
          intermediateHWCLink.get().getAsFile(),
          project.getLayout().getProjectDirectory().dir("src/app").getAsFile()
      );
    });

  }

  protected TaskProvider<UMLP2TSTask> createUMLP2TSTask(final Project project) {
    var hwc = project.provider(() -> {
      File f = project.file("src/app");
      return f.exists() ? project.getLayout().getProjectDirectory().dir("src/app") : project.files();
    });

    return project.getTasks().register(UMLP2TS_TASK, UMLP2TSTask.class, task -> {
      task.setGroup(UMLP_GROUP_NAME);
      task.getInput().from(getModels(project));
      task.getOcl().from(FrontendBasePlugin.getOCLPath(project));
      task.getHwcDir().from(hwc);

      // It is important that the directory-depth of the intermediate-output is the same as the final location (after sync!)
      // Because the frontend uses relative paths to include HWC Code
      task.getOutputDir().set(project.getLayout().getBuildDirectory().dir("umlp2ts"));
      // Always create symbols & tag files
      task.getSymbolOutDir().set(project.getLayout().getBuildDirectory().dir("umlp2sym"));

      // We require the modules with actual models here, as the symbols are not enough:
      /*
      umlp2ts with A extends B (where B is only a symbol import)
       0xA7003x57269 get for AstNode can't return a value. Attribute is empty.

      at de.monticore.cdbasis._symboltable.CDTypeSymbolTOP.getAstNode(CDTypeSymbolTOP.java:1)
      at de.monticore.cd._symboltable.CDSymbolTables.getTransitiveSuperTypes(CDSymbolTables.java:86)
       */

      task.getSymbolPath().from(project.getConfigurations().named(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG));
      task.getSymbolPath().from(project.getConfigurations().named(MONTIGEM_MODULE_IMPORT_MAP_CONFIG));

      task.getModelDir().from(project.getConfigurations().named(MONTIGEM_MODULE_UMLP_MODELS_CONFIG));
    });
  }

  private TaskProvider<GUI2JavaTask> createGUI2JavaTask(Project project) {
    // Add Configuration for TypeSymbols.
    NamedDomainObjectProvider<Configuration> guiTypes = project.getConfigurations().named(GUI_TYPES_CONFIG);

    return project.getTasks().register(GUI2JAVA_TASK, GUI2JavaTask.class, task -> {
      task.setGroup(UMLP_GROUP_NAME);
      task.getInput().from(getModels(project));
      task.getModelDir().from(getModels(project));
      task.getSymbolPath().from(guiTypes);

      // Further configuration is done in gradle.FrontendWithJSweetPlugin and gradle.FrontendNonJSweetPlugin
      // It is important that the directory-depth of the intermediate-output is the same as the final location (after sync!)
      // Because the frontend uses relative paths to include HWC Code
      task.getOutputDir().set(project.getLayout().getBuildDirectory().dir("gui2java"));
      task.getSymbolOutDir().set(project.getLayout().getBuildDirectory().dir("gui2sym"));

      // load module symbols
      task.getSymbolPath().from(project.getConfigurations().named(MONTIGEM_MODULE_UMLP_SYMBOLS_CONFIG));
      task.getSymbolPath().from(project.getConfigurations().named(MONTIGEM_MODULE_IMPORT_MAP_CONFIG));
      task.getSymbolPath().from(project.getConfigurations().named(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG));
    });
  }

}
