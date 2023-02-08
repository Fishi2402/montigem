/* (c) https://github.com/MontiCore/monticore */
package gradle;

import gradle.merge.UMLPMergeTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public abstract class UMLPPluginBackend implements Plugin<Project> {
    @Override
    public void apply(Project project) {
      project.getPluginManager().apply(UMLPPluginBase.class);
      project.getPluginManager().apply("java");
    }
}
