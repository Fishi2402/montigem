/* (c) https://github.com/MontiCore/monticore */
package gradle;

import gradle.common.CheckDirEqualityTask;
import gradle.specific.FrontendSetup;
import gradle.specific.GuiVariabilityHandlerTask;
import gradle.specific.NPMAuthCreate;
import gradle.specific.UMLPBackendTask;
import gradle.specific.UMLPCdToGuiTask;
import gradle.specific.UMLPFrontendTask;
import gradle.specific.UMLPGuiGenTask;
import gradle.specific.UMLPSymbolsTask;
import gradle.specific.UMLPTask;
import gradle.specific.merge.UMLPMergeTask;
import gradle.specific.serialize.SerializeCDSymbolsTask;
import gradle.specific.serialize.SerializeGUISymbolsTask;
import gradle.stat.StatisticListener;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Base Plugin. Only adds the Tasks and the StatisticListener.
 * Nothing else is done! See other Plugins (e.g. UMLPPluginBackend.java) for alternatives
 */
public abstract class UMLPPluginBase implements Plugin<Project> {

  public static final String UMLP_MODULES_CONFIG = "umlpModules";

  @Override
    public void apply(Project project) {
      // Make the Tasks available without fully qualified name
      project.getExtensions().getExtraProperties().set("UMLPTask", UMLPTask.class);
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

      // Module system
      project.getConfigurations().maybeCreate(UMLP_MODULES_CONFIG);

      StatisticListener.registerOnce(project);
    }
}
