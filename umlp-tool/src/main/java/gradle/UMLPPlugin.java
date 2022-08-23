package gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public abstract class UMLPPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
      project.getExtensions().getExtraProperties().set("UMLPTask", gradle.UMLPTask.class);
    }
}
