/* (c) https://github.com/MontiCore/monticore */
package gradle;

import de.se_rwth.commons.logging.Log;
import gradle.common.UMLPAllFilesTask;
import gradle.common.UMLPSingleFileTask;
import gradle.specific.UMLPJava2TypeScriptConfigTask;
import gradle.specific.merge.UMLPMergeTask;
import gradle.specific.serialize.SerializeCDSymbolsTask;
import gradle.specific.serialize.SerializeGUISymbolsTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Bundling;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * Sets up all tasks with their required Tool-classpath.
 */
public abstract class UMLPPluginDep implements Plugin<Project> {


  public static final String UMLP_CONFIG_TOOL = "umlpTool";

  @Override
  public void apply(Project project) {
    // add a new configuration in which the umlp-tool (.jar) is loaded
    //  (this avoids loading the tool+all its dependencies into the shared plugin-classpath)
    var toolConfig = project.getConfigurations().maybeCreate(UMLP_CONFIG_TOOL);
    // Load the buildInfo.properties file (containing the current version)
    Properties buildInfo = new Properties();
    try (InputStream is = UMLPPluginDep.class.getResourceAsStream("/buildInfo.properties")) {
      buildInfo.load(is);
    } catch (Exception e) {
      Log.error("Cannot load /buildInfo.properties from classpath", e);
    }
    String version = Objects.requireNonNull(
        buildInfo.getProperty("version"),
        "Cannot find version in buildInfo.properties"
    );
    // Add the dependencies to the toolConfig
    toolConfig.setCanBeResolved(true);
    toolConfig.defaultDependencies(dependencies -> {
      dependencies.add(project.getDependencies().create("umlp:umlp-tool:" + version));
      dependencies.add(project.getDependencies().create("de.monticore.lang:cd4analysis:" + version + ":cdmerge"));
      dependencies.add(project.getDependencies().create("de.monticore.lang:od:" + version));
    });
    toolConfig.attributes(it -> {
      // Do not use the shadowed bundling variant
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
    });

    // And add the config to the classpath of various tasks
    project.getTasks().withType(UMLPSingleFileTask.class).configureEach(t -> t.getExtraClasspathElements().from(toolConfig));
    project.getTasks().withType(UMLPAllFilesTask.class).configureEach(t -> t.getExtraClasspathElements().from(toolConfig));
    project.getTasks().withType(SerializeCDSymbolsTask.class).configureEach(t -> t.getClassPath().from(toolConfig));
    project.getTasks().withType(SerializeGUISymbolsTask.class).configureEach(t -> t.getClassPath().from(toolConfig));
    project.getTasks().withType(UMLPMergeTask.class).configureEach(t -> t.getClassPath().from(toolConfig));
    project.getTasks().withType(UMLPJava2TypeScriptConfigTask.class).configureEach(t -> t.getClassPath().from(toolConfig));
  }


  public static Configuration getToolConfig(Project project) {
    return project.getConfigurations().getByName(UMLP_CONFIG_TOOL);
  }
}
