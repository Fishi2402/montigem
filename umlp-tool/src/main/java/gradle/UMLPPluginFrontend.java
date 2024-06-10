/* (c) https://github.com/MontiCore/monticore */
package gradle;

import gradle.specific.FrontendSetup;
import gradle.specific.UMLPFrontendCIImageTask;
import gradle.specific.UMLPFrontendLocalImageTask;
import gradle.specific.UMLPFrontendTask;
import gradle.specific.UMLPGuiGenTask;
import gradle.specific.UMLPJava2TypeScriptAdditionalsTask;
import gradle.specific.UMLPJava2TypeScriptConfigTask;
import gradle.specific.UMLPJava2TypeScriptTask;
import gradle.specific.UMLPSymbolsTask;
import gradle.specific.serialize.SerializeCDSymbolsTask;
import gradle.specific.serialize.SerializeGUISymbolsTask;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.MavenRepositoryContentDescriptor;
import org.gradle.api.attributes.Bundling;
import org.gradle.api.attributes.VerificationType;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

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

    // This plugin either applies the With or Non JSweet variant

    if (j2ts) {
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
