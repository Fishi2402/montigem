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
 * Sets up all tasks with their required Tool-classpath
 */
public abstract class UMLPPluginDep implements Plugin<Project> {


  public static final String UMLP_CONFIG_TOOL = "umlpTool";

  @Override
    public void apply(Project project) {
      var toolConfig = project.getConfigurations().maybeCreate(UMLP_CONFIG_TOOL);
      Properties buildInfo = new Properties();
      try(InputStream is = UMLPPluginDep.class.getResourceAsStream("/buildInfo.properties")) {
        buildInfo.load(is);
      } catch (Exception e){
        Log.error("Can not load /buildInfo.properties from classpath", e);
      }
      String version = Objects.requireNonNull(
          buildInfo.getProperty("version"),
          "Can not find version in buildInfo.properties"
      );

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
