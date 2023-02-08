/* (c) https://github.com/MontiCore/monticore */
package gradle;

import gradle.merge.UMLPMergeTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public abstract class UMLPPluginBase implements Plugin<Project> {
    @Override
    public void apply(Project project) {
      project.getExtensions().getExtraProperties().set("UMLPTask", gradle.UMLPTask.class);
      project.getExtensions().getExtraProperties().set("UMLPMergeTask", UMLPMergeTask.class);
    }
}
