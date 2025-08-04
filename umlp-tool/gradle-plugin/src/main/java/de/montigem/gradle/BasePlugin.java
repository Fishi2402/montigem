/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle;

import de.montigem.gradle.common.CheckDirEqualityTask;
import de.montigem.gradle.module.ModuleFrontendPlugin;
import de.montigem.gradle.module.ModulePlugin;
import de.montigem.gradle.specific.CD2GUITask;
import de.montigem.gradle.specific.CreateNpmrcTask;
import de.montigem.gradle.specific.FrontendSetupTask;
import de.montigem.gradle.specific.GUI2JavaTask;
import de.montigem.gradle.specific.GUI2SymTask;
import de.montigem.gradle.specific.GUIVariabilityTask;
import de.montigem.gradle.specific.UMLP2SymTask;
import de.montigem.gradle.specific.UMLP2JavaTask;
import de.montigem.gradle.specific.UMLP2TSTask;
import de.montigem.gradle.specific.OD2DataTask;
import de.montigem.gradle.specific.merge.MergeTask;
import de.montigem.gradle.specific.serialize.CD2SymTask;
import de.montigem.gradle.stat.StatisticListener;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Base Plugin. Only adds the Tasks, module config, and the StatisticListener.
 * Nothing else is done! See other Plugins (e.g. BackendPlugin.java) for alternatives
 */
public abstract class BasePlugin implements Plugin<Project> {

  public static final String UMLP_MODULES_CONFIG = "umlpModules";
  // The following configs are all defined with filtering attributes
  public static final String UMLP_MODULES_GEN_CONFIG = "umlpModulesGen";
  public static final String UMLP_MODULES_MODELS_CONFIG = "umlpModulesModels";
  public static final String UMLP_MODULES_DOMAIN_SYMBOLS_CONFIG = "umlpModulesDomainSymbols";
  public static final String UMLP_MODULES_IMPORT_MAP_CONFIG = "umlpModulesImportMap";
  public static final String UMLP_MODULES_GUI_SYMBOLS_CONFIG = "umlpModulesGuiSymbols";

  public static final String GUI_LIB_NAME = "guiLib";

  @Override
  public void apply(Project project) {
    // Load dependencies
    project.getPluginManager().apply(DependencyPlugin.class);
    // Make the Tasks available without fully qualified name
    project.getExtensions().getExtraProperties().set("MergeTask", MergeTask.class);
    project.getExtensions().getExtraProperties().set("GUI2JavaTask", GUI2JavaTask.class);
    project.getExtensions().getExtraProperties().set("CreateNpmrcTask", CreateNpmrcTask.class);
    project.getExtensions().getExtraProperties().set("UMLP2JavaTask", UMLP2JavaTask.class);
    project.getExtensions().getExtraProperties().set("CD2GUITask", CD2GUITask.class);
    project.getExtensions().getExtraProperties().set("GUIVariabilityTask", GUIVariabilityTask.class);
    project.getExtensions().getExtraProperties().set("UMLP2TSTask", UMLP2TSTask.class);
    project.getExtensions().getExtraProperties().set("UMLPSymbolsTask", UMLP2SymTask.class);
    project.getExtensions().getExtraProperties().set(GUI2SymTask.class.getSimpleName(), GUI2SymTask.class);
    project.getExtensions().getExtraProperties().set("FrontendSetupTask", FrontendSetupTask.class);
    project.getExtensions().getExtraProperties().set("GUI2SymTask", de.montigem.gradle.specific.serialize.GUI2SymTask.class);
    project.getExtensions().getExtraProperties().set("CD2SymTask", CD2SymTask.class);
    project.getExtensions().getExtraProperties().set("CheckDirEqualityTask", CheckDirEqualityTask.class);
    project.getExtensions().getExtraProperties().set("OD2DataTask", OD2DataTask.class);

    // Module system
    var umlpModulesC = project.getConfigurations().maybeCreate(UMLP_MODULES_CONFIG);
    umlpModulesC.setCanBeResolved(false);

    var umlpModulesGenC = project.getConfigurations().maybeCreate(UMLP_MODULES_GEN_CONFIG);
    ModulePlugin.addModuleGenJarJarAttributes(umlpModulesGenC, project);
    umlpModulesGenC.extendsFrom(umlpModulesC);

    var umlpModulesModelsC = project.getConfigurations().maybeCreate(UMLP_MODULES_MODELS_CONFIG);
    ModulePlugin.addModelsJarAttributes(umlpModulesModelsC, project);
    umlpModulesModelsC.extendsFrom(umlpModulesC);

    var umlpModulesDSC = project.getConfigurations().maybeCreate(UMLP_MODULES_DOMAIN_SYMBOLS_CONFIG);
    umlpModulesDSC.setCanBeConsumed(false);
    ModuleFrontendPlugin.addDomainSymbolsJarAttributes(umlpModulesDSC, project);
    umlpModulesDSC.extendsFrom(umlpModulesC);

    var umlpModulesIMC = project.getConfigurations().maybeCreate(UMLP_MODULES_IMPORT_MAP_CONFIG);
    umlpModulesIMC.setCanBeConsumed(false);
    ModuleFrontendPlugin.addImportMapJarAttributes(umlpModulesIMC, project);
    umlpModulesIMC.extendsFrom(umlpModulesC);

    var umlpModulesGSC = project.getConfigurations().maybeCreate(UMLP_MODULES_GUI_SYMBOLS_CONFIG);
    umlpModulesGSC.setCanBeConsumed(false);
    ModuleFrontendPlugin.addGuiSymbolsJarAttributes(umlpModulesGSC, project);
    umlpModulesGSC.extendsFrom(umlpModulesC);

    project.getPlugins().withId("java", plugin -> {
      project.getConfigurations().getByName("implementation").extendsFrom(umlpModulesC);
    });
    project.getPlugins().withId("java-library", plugin -> {
      project.getConfigurations().getByName("api").extendsFrom(umlpModulesC);
    });

    // Add guiLib configuration
    project.getConfigurations().maybeCreate(GUI_LIB_NAME);
    if (!getBooleanProjectProperty(project, "useLocalDependencies")) {
      project.getDependencies().add(GUI_LIB_NAME, "umlp.umlp-rte:client:" + FrontendPlugin.getStringProjectProperty(project, "mc_version", "") + ":guisymbols");
    }

    StatisticListener.registerOnce(project);

    this.failIfMontiCorePresent(project);
  }

  protected void failIfMontiCorePresent(Project project) {
    // If a plugin with one of MontiCores IDs is already present -> fail
    if (project.getPlugins().hasPlugin("de.monticore.generator") || project.getPlugins().hasPlugin("de.monticore.generator-withtr") || project.getPlugins().hasPlugin("monticore")) {
      doFailBecauseMontiCorePresent();
    }

    project.getPlugins().whenPluginAdded(plugin -> {
      // When a plugin with one of MontiCores Plugin-Classnames is added -> fail
      if (Arrays.asList("de.monticore.MCPlugin", "de.monticore.gradle.MCGeneratorPlugin", "de.monticore.gradle.MCGeneratorWithTRPlugin").contains(plugin.getClass().getName())) {
        doFailBecauseMontiCorePresent();
      }
    });
  }

  protected void doFailBecauseMontiCorePresent() {
    // The incompatibility stems from us using the monticore-runtime in two different versions!
    // (for example, the TemplateController has changed, resulting in NoSuchMethodErrors etc.)
    throw new GradleException("The MontiCore Generator & MontiGem Plugins are not compatible. \n" +
        "Please use the MontiCore plugin in a separate subproject and use that as a dependency.");
  }

  @NotNull
  public static Boolean getBooleanProjectProperty(Project project, String propertyName) {
    return FrontendPlugin.getBooleanProjectProperty(project, propertyName);
  }
}
