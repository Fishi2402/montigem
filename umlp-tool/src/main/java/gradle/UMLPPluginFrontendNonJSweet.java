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

import static gradle.UMLPPluginFrontendBase.UMLP_FRONTEND_DOMAIN_TASK_NAME;
import static gradle.UMLPPluginFrontendBase.UMLP_FRONTEND_GUI_TASK_NAME;
import static gradle.UMLPPluginFrontendBase.UMLP_FRONTEND_SETUP_TASK_NAME;
import static gradle.UMLPPluginFrontendBase.UMLP_GROUP_NAME;

/**
 * The specific gradle setup for non-jsweet projects
 */
public abstract class UMLPPluginFrontendNonJSweet implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(UMLPPluginFrontendBase.class);

    TaskProvider<UMLPFrontendTask> frontendDomainTask = createFrontendDomainTask(project);
    TaskProvider<UMLPGuiGenTask> frontendGuiTask = project.getTasks().named(UMLP_FRONTEND_GUI_TASK_NAME, UMLPGuiGenTask.class);
    TaskProvider<Sync> syncDomainTask = project.getTasks().named("SYNC_" + UMLP_FRONTEND_DOMAIN_TASK_NAME, Sync.class);
    TaskProvider<Sync> syncGuiTask = project.getTasks().named("SYNC_" + UMLP_FRONTEND_GUI_TASK_NAME, Sync.class);

    TaskProvider<FrontendSetup> frontendSetupTask = project.getTasks().named(UMLP_FRONTEND_SETUP_TASK_NAME, FrontendSetup.class);

    TaskProvider<Sync> unpackModulesTask = createUnpackModuleTask(project);

    frontendGuiTask.configure(umlpTask -> {
      setIfPathExists(x -> umlpTask.getHwcDir().from(x), Path.of(project.getProjectDir().getAbsolutePath(), "src", "app"));
      umlpTask.getGuiGenJsweet().set(false);

      // // Load the symbols from the .jars in the domain symbols path
      umlpTask.getSymbolDir().from(project.getConfigurations().getByName("umlpModulesDomainSymbols"));
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
      generateTask.dependsOn(unpackModulesTask);
    });

  }

  protected TaskProvider<UMLPFrontendTask> createFrontendDomainTask(final Project project) {
    return project.getTasks().register(UMLP_FRONTEND_DOMAIN_TASK_NAME, UMLPFrontendTask.class, umlpTask -> {
      umlpTask.setGroup(UMLP_GROUP_NAME);
      umlpTask.getInput().from(UMLPPluginFrontendBase.getModelsPath(project));
      umlpTask.getOcl().from(UMLPPluginFrontendBase.getOCLPath(project));
      setIfPathExists(x -> umlpTask.getHwcDir().from(x), Path.of(project.getProjectDir().getAbsolutePath(), "src", "app"));

      // It is important that the directory-depth of the intermediate-output is the the same as the final location (after sync!)
      // Because the frontend uses relative paths to include HWC Code
      umlpTask.getOutputDir().set(UMLPPluginFrontendBase.getAppDomainPath(project).toFile());
      // Always create symbols & tag files
      umlpTask.getSymbolOutDir().set(Path.of(project.getBuildDir().getAbsolutePath(), "symbols_gui_domain_domain__from_task_frontenddomain").toFile());

      // We require the modules actual models here, as the symbols are not enough:
      /*
      genDomain with A extends B (where B is only a symbol import)
       0xA7003x57269 get for AstNode can't return a value. Attribute is empty.

      at de.monticore.cdbasis._symboltable.CDTypeSymbolTOP.getAstNode(CDTypeSymbolTOP.java:1)
      at de.monticore.cd._symboltable.CDSymbolTables.getTransitiveSuperTypes(CDSymbolTables.java:86)
       */
      umlpTask.getModelDir().from(project.getConfigurations().getByName("umlpModulesModels"));
    });
  }


  protected TaskProvider<Sync> createUnpackModuleTask(final Project project) {
    // Download the frontends code and extract it into a dir
    // Make sure to add "umlp_modules/loginmodule-ts/template_placeholder/src/*" as a path to the tsconfig
    var conf = project.getConfigurations().getByName("umlpModuleFrontend");
    var t = project.getTasks().register("unpackModule", Sync.class);
    t.configure(task -> {
      task.setGroup("umlp");
      task.setDescription("Unpacks all umlp modules");
      for (var file : conf.getFiles())
        task.from(project.zipTree(file));
      task.into(project.getLayout().getBuildDirectory().dir("frontendGen/umlp_modules"));
    });
    return t;
  }

  private void setIfPathExists(Consumer<File> add, Path path) {
    File f = path.toFile();
    if (f.exists()) {
      add.accept(f);
    }
  }

}
