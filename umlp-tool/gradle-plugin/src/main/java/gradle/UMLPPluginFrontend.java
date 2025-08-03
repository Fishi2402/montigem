/* (c) https://github.com/MontiCore/monticore */
package gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/***
 * A opinionated plugin which handles frontend generation.
 * with only a single line "id 'de.rwth.se.umlp-tool-frontend'" in build.gradle some default-task for the frontend are created
 */
public abstract class UMLPPluginFrontend implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(UMLPPluginBase.class);
    project.getPluginManager().apply("base");

    boolean j2ts = getBooleanProjectProperty(project, "j2ts");
    // only temporarily
    boolean j2ts_vislib = getBooleanProjectProperty(project, "j2ts_vislib");

    // This plugin either applies the With or Non JSweet variant

    if (j2ts || j2ts_vislib) {
      project.getPluginManager().apply(UMLPPluginFrontendWithJSweet.class);
    }
    else {
      project.getPluginManager().apply(UMLPPluginFrontendNonJSweet.class);
    }

    // Do NOT configure tasks in here!!!
    // Configure common tasks in the gradle.UMLPPluginFrontendBase
    // or specific ones in the two other plugins!!!
  }

  public static Optional<String> getProjectProperty(Project project, String propertyName) {
    Object projectProperty = project.findProperty(propertyName);
    if (projectProperty == null) {
      return Optional.empty();
    }
    else {
      return Optional.of(projectProperty.toString());
    }
  }

  public static String getStringProjectProperty(Project project, String propertyName, String defaultValue) {
    return getProjectProperty(project, propertyName).orElse(defaultValue);
  }

  @NotNull
  public static Boolean getBooleanProjectProperty(Project project, String propertyName) {
    return getProjectProperty(project, propertyName).map(Boolean::parseBoolean).orElse(false);
  }
}
