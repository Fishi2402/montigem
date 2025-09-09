/* (c) https://github.com/MontiCore/monticore */
package de.montigem.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static de.montigem.gradle.FrontendBasePlugin.getBooleanProjectProperty;

/***
 * A opinionated plugin which handles frontend generation.
 * with only a single line "id 'de.rwth.se.montigem-frontend'" in build.gradle some default-task for the frontend are created
 */
public abstract class FrontendPlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(MGBasePlugin.class);
    project.getPluginManager().apply("base");

    boolean j2ts = getBooleanProjectProperty(project, "j2ts");

    // This plugin either applies the With or Non JSweet variant

    if (j2ts) {
      project.getPluginManager().apply(FrontendWithJSweetPlugin.class);
    }
    else {
      project.getPluginManager().apply(FrontendNonJSweetPlugin.class);
    }

    // Do NOT configure tasks in here!!!
    // Configure common tasks in the gradle.FrontendBasePlugin
    // or specific ones in the two other plugins!!!
  }

}
