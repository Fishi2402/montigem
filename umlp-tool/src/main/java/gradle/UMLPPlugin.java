package gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public abstract class UMLPPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        // project.setProperty("UMLPTask", gradle.UMLPTask.class );
    }
}
