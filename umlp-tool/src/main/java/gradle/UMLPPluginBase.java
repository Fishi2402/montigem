/* (c) https://github.com/MontiCore/monticore */
package gradle;

import de.monticore.gradle.common.GradleErrorHook;
import gradle.common.CheckDirEqualityTask;
import gradle.module.UMLPPluginModule;
import gradle.module.UMLPPluginModuleJSFrontend;
import gradle.specific.FrontendSetup;
import gradle.specific.GuiVariabilityHandlerTask;
import gradle.specific.NPMAuthCreate;
import gradle.specific.UMLPBackendTask;
import gradle.specific.UMLPCdToGuiTask;
import gradle.specific.UMLPFrontendTask;
import gradle.specific.UMLPGuiGenTask;
import gradle.specific.UMLPOD2DataTask;
import gradle.specific.UMLPSymbolsTask;
import gradle.specific.merge.UMLPMergeTask;
import gradle.specific.serialize.SerializeCDSymbolsTask;
import gradle.specific.serialize.SerializeGUISymbolsTask;
import gradle.stat.StatisticListener;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.Arrays;

/**
 * Base Plugin. Only adds the Tasks and the StatisticListener.
 * Nothing else is done! See other Plugins (e.g. UMLPPluginBackend.java) for alternatives
 */
public abstract class UMLPPluginBase implements Plugin<Project> {

  public static final String UMLP_MODULES_CONFIG = "umlpModules";
  // The following configs are all defined with filtering attributes
  public static final String UMLP_MODULES_GEN_CONFIG = "umlpModulesGen";
  public static final String UMLP_MODULES_MODELS_CONFIG = "umlpModulesModels";
  public static final String UMLP_MODULES_DOMAIN_SYMBOLS_CONFIG = "umlpModulesDomainSymbols";
  public static final String UMLP_MODULES_FRONTEND_CONFIG = "umlpModulesFrontend";

  @Override
    public void apply(Project project) {
      // Make the Tasks available without fully qualified name
      project.getExtensions().getExtraProperties().set("UMLPMergeTask", UMLPMergeTask.class);
      project.getExtensions().getExtraProperties().set("UMLPGuiGenTask", UMLPGuiGenTask.class);
      project.getExtensions().getExtraProperties().set("NPMAuthCreate", NPMAuthCreate.class);
      project.getExtensions().getExtraProperties().set("UMLPBackendTask", UMLPBackendTask.class);
      project.getExtensions().getExtraProperties().set("UMLPCdToGuiTask", UMLPCdToGuiTask.class);
      project.getExtensions().getExtraProperties().set("GuiVariabilityHandlerTask", GuiVariabilityHandlerTask.class);
      project.getExtensions().getExtraProperties().set("UMLPFrontendTask", UMLPFrontendTask.class);
      project.getExtensions().getExtraProperties().set("UMLPSymbolsTask", UMLPSymbolsTask.class);
      project.getExtensions().getExtraProperties().set("FrontendSetup", FrontendSetup.class);
      project.getExtensions().getExtraProperties().set("SerializeGUISymbolsTask", SerializeGUISymbolsTask.class);
      project.getExtensions().getExtraProperties().set("SerializeCDSymbolsTask", SerializeCDSymbolsTask.class);
      project.getExtensions().getExtraProperties().set("CheckDirEqualityTask", CheckDirEqualityTask.class);
      project.getExtensions().getExtraProperties().set("UMLPOD2DataTask", UMLPOD2DataTask.class);

      // Module system
      var umlpModulesC = project.getConfigurations().maybeCreate(UMLP_MODULES_CONFIG);
      umlpModulesC.setCanBeResolved(false);
      var umlpModulesGenC = project.getConfigurations().maybeCreate(UMLP_MODULES_GEN_CONFIG);
      UMLPPluginModule.addModuleGenJarJarAttributes(umlpModulesGenC, project);
      umlpModulesGenC.extendsFrom(umlpModulesC);
      var umlpModulesModelsC = project.getConfigurations().maybeCreate(UMLP_MODULES_MODELS_CONFIG);
      UMLPPluginModule.addModelsJarAttributes(umlpModulesModelsC, project);
      umlpModulesModelsC.extendsFrom(umlpModulesC);

      var umlpModulesDSC = project.getConfigurations().maybeCreate(UMLP_MODULES_DOMAIN_SYMBOLS_CONFIG);
      umlpModulesDSC.setCanBeConsumed(false);
      UMLPPluginModuleJSFrontend.addDomainSymbolsJarAttributes(umlpModulesDSC, project);
      umlpModulesDSC.extendsFrom(umlpModulesC);

      var umlpModuleFrontend = project.getConfigurations().maybeCreate(UMLP_MODULES_FRONTEND_CONFIG);
      umlpModuleFrontend.setCanBeConsumed(false);
      UMLPPluginModuleJSFrontend.addBundledJSJarAttributes(umlpModuleFrontend, project);
      umlpModuleFrontend.extendsFrom(umlpModulesC);

      project.getPlugins().withId("java", plugin -> {
        project.getConfigurations().getByName("implementation").extendsFrom(umlpModulesC);
      });
      project.getPlugins().withId("java-library", plugin -> {
        project.getConfigurations().getByName("api").extendsFrom(umlpModulesC);
      });


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
}
