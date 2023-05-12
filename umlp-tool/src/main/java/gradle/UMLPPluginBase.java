/* (c) https://github.com/MontiCore/monticore */
package gradle;

import gradle.inputset.GuiVariabilityHandlerTask;
import gradle.inputset.UMLPGuiGenTask;
import gradle.merge.UMLPMergeTask;
import gradle.stat.StatisticListener;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public abstract class UMLPPluginBase implements Plugin<Project> {
    @Override
    public void apply(Project project) {
      project.getExtensions().getExtraProperties().set("UMLPTask", gradle.UMLPTask.class);
      project.getExtensions().getExtraProperties().set("UMLPMergeTask", UMLPMergeTask.class);
      project.getExtensions().getExtraProperties().set("UMLPGuiGenTask", UMLPGuiGenTask.class);
      project.getExtensions().getExtraProperties().set("GuiVariabilityHandlerTask", GuiVariabilityHandlerTask.class);

      StatisticListener.registerOnce(project);
    }
}
