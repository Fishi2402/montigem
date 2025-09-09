/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle;

import de.monticore.gradle.common.CommonMCTask;
import de.montigem.gradle.specific.CreateSymlinkTask;
import de.montigem.gradle.specific.GUI2JavaTask;
import de.montigem.gradle.specific.SetupFrontendTask;
import de.montigem.gradle.specific.Symlink;
import de.montigem.gradle.specific.UMLP2TSTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskProvider;

import java.io.File;

import static de.montigem.gradle.FrontendBasePlugin.CREATE_SYMLINK_TASK;
import static de.montigem.gradle.FrontendBasePlugin.GUI2JAVA_TASK;
import static de.montigem.gradle.FrontendBasePlugin.SETUP_FRONTEND_TASK;
import static de.montigem.gradle.FrontendBasePlugin.SYNC_GUI2JAVA_TASK;
import static de.montigem.gradle.FrontendBasePlugin.SYNC_UMLP2TS_TASK;
import static de.montigem.gradle.FrontendBasePlugin.UMLP2TS_TASK;
import static de.montigem.gradle.FrontendBasePlugin.UMLP_GROUP_NAME;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_IMPORT_MAP_CONFIG;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_UMLP_MODELS_CONFIG;

/**
 * The specific gradle setup for non-jsweet projects
 */
public abstract class FrontendNonJSweetPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(FrontendBasePlugin.class);

    TaskProvider<UMLP2TSTask> umlp2ts = createUMLP2TSTask(project);
    TaskProvider<GUI2JavaTask> gui2java = project.getTasks().named(GUI2JAVA_TASK, GUI2JavaTask.class);
    TaskProvider<Sync> syncUMLP2TSTask = project.getTasks().named(SYNC_UMLP2TS_TASK, Sync.class);
    TaskProvider<Sync> syncGUI2JavaTask = project.getTasks().named(SYNC_GUI2JAVA_TASK, Sync.class);

    TaskProvider<SetupFrontendTask> setupFrontendTask = project.getTasks().named(SETUP_FRONTEND_TASK, SetupFrontendTask.class);

    TaskProvider<CreateSymlinkTask> createSymlinkTask =
        project.getTasks().named(CREATE_SYMLINK_TASK, CreateSymlinkTask.class);

    var hwc = project.provider(() -> {
      File f = project.file("src/app");
      return f.exists() ? project.getLayout().getProjectDirectory().dir("src/app") : project.files();
    });
    gui2java.configure(task -> {
      task.getHwcDir().from(hwc);
      task.getGUIGenJSweet().set(false);
      task.dependsOn(umlp2ts);

      /*
      // // Load the symbols from the .jars in the domain symbols path
      umlpTask.getSymbolDir().from(project.getConfigurations().named(UMLP_MODULES_DOMAIN_SYMBOLS_CONFIG));
      umlpTask.getSymbolDir().from(project.getConfigurations().named(UMLP_MODULES_IMPORT_MAP_CONFIG));
      umlpTask.getSymbolDir().from(project.getConfigurations().named(UMLP_MODULES_GUI_SYMBOLS_CONFIG));
      /**/
    });

    syncUMLP2TSTask.configure(task -> {
      task.from(umlp2ts.flatMap(CommonMCTask::getOutputDir));
      task.dependsOn(umlp2ts, setupFrontendTask);
    });

    syncGUI2JavaTask.configure(umlpTask -> {
      umlpTask.from(gui2java.flatMap(CommonMCTask::getOutputDir));
      umlpTask.dependsOn(gui2java, setupFrontendTask);
    });

    /*
    project.getTasks().named("generate").configure(generateTask -> {
      generateTask.dependsOn(gui2java);
      // generateTask.dependsOn(unpackModulesTask);
    });
    /**/

    createSymlinkTask.configure(task -> {
      Symlink hwcLink = project.getObjects().newInstance(Symlink.class);
      hwcLink.getLinkFile().set(project.getLayout().getBuildDirectory().file("app/hwcsrc"));
      hwcLink.getTargetFile().set(project.getLayout().getProjectDirectory().dir("src/app"));
      task.getLinks().add(hwcLink);

      Symlink umlp_frontend = project.getObjects().newInstance(Symlink.class);
      umlp_frontend.getLinkFile().set(project.getLayout().getBuildDirectory().file("build/umlp_frontend"));
      umlp_frontend.getTargetFile().set(project.getLayout().getBuildDirectory().dir("umlp_frontend"));
      task.getLinks().add(umlp_frontend);

      /*
      if (task.getSymlinkMap().isPresent()) {
        File hwcSrcLink = project.getBuildDir().toPath().resolve("app/hwcsrc").toFile();
        File hwcTS = project.getProjectDir().toPath().resolve("src/app").toFile();
        if (hwcTS.exists()) {
          task.getSymlinkMap().put(hwcSrcLink, hwcTS);
        }

        File angularTemplateResolveLink = project.getBuildDir().toPath().resolve("build/umlp_frontend").toFile();
        File angularTemplateResolveTarget = project.getBuildDir().toPath().resolve("umlp_frontend").toFile();
        task.getSymlinkMap().put(angularTemplateResolveLink, angularTemplateResolveTarget);
      }
      /**/
    });

  }

  protected TaskProvider<UMLP2TSTask> createUMLP2TSTask(final Project project) {
    var hwc = project.provider(() -> {
      File f = project.file("src/app");
      return f.exists() ? project.getLayout().getProjectDirectory().dir("src/app") : project.files();
    });

    return project.getTasks().register(UMLP2TS_TASK, UMLP2TSTask.class, task -> {
      task.setGroup(UMLP_GROUP_NAME);
      task.getInput().from(FrontendBasePlugin.getModels(project));
      task.getOcl().from(FrontendBasePlugin.getOCLPath(project));
      task.getHwcDir().from(hwc);

      // It is important that the directory-depth of the intermediate-output is the same as the final location (after sync!)
      // Because the frontend uses relative paths to include HWC Code
      task.getOutputDir().set(FrontendBasePlugin.getIntermediateUMLP2TS(project));
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

}
