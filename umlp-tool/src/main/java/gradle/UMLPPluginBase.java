/* (c) https://github.com/MontiCore/monticore */
package gradle;

import gradle.inputset.UMLPGuiGenTask;
import gradle.merge.UMLPMergeTask;
import gradle.stat.StatisticListener;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Base Plugin. Only adds the Tasks and the StatisticListener.
 * Nothing else is done! See other Plugins (e.g. UMLPPluginBackend.java) for alternatives
 */
public abstract class UMLPPluginBase implements Plugin<Project> {
    @Override
    public void apply(Project project) {
      // Make the Tasks available without fully qualified name
      project.getExtensions().getExtraProperties().set("UMLPTask", gradle.UMLPTask.class);
      project.getExtensions().getExtraProperties().set("UMLPMergeTask", UMLPMergeTask.class);
      project.getExtensions().getExtraProperties().set("UMLPGuiGenTask", UMLPGuiGenTask.class);
      project.getExtensions().getExtraProperties().set("NPMAuthCreate", gradle.NPMAuthCreate.class);

      StatisticListener.registerOnce(project);
    }
}
