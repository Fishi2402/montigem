/* (c) https://github.com/MontiCore/monticore */
package gradle;

import gradle.common.UMLPAllFilesTask;
import gradle.common.UMLPSingleFileTask;
import gradle.specific.UMLPJava2TypeScriptConfigTask;
import gradle.specific.merge.UMLPMergeTask;
import gradle.specific.serialize.SerializeCDSymbolsTask;
import gradle.specific.serialize.SerializeGUISymbolsTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

/**
 * Sets up all tasks with their required Tool-classpath
 */
public abstract class UMLPPluginDep implements Plugin<Project> {


  public static final String UMLP_CONFIG_TOOL = "umlpTool";

  @Override
    public void apply(Project project) {
      // We require java-library to differentiate between runtimeElements and shadowRuntimeElements
      project.getPluginManager().apply("java-library");

      // Load the actual UMLP-Tool as a dependency of the "umlpTool" configuration
      var toolConfig = project.getConfigurations().maybeCreate(UMLP_CONFIG_TOOL);
      toolConfig.setCanBeResolved(true);
      toolConfig.defaultDependencies(dependencies -> {
        dependencies.add(project.getDependencies().create("umlp:umlp-tool:7.7.0-SNAPSHOT"));
        dependencies.add(project.getDependencies().create("de.monticore.lang:cd4analysis:7.7.0-SNAPSHOT:cdmerge"));
        dependencies.add(project.getDependencies().create("de.monticore.lang:od:7.7.0-SNAPSHOT"));
      });

      // And add it to the classpath of various tasks
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
