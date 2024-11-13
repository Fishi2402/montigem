package gradle.module;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Bundling;
import org.gradle.api.attributes.Category;
import org.gradle.api.attributes.LibraryElements;
import org.gradle.api.attributes.Usage;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;


public class UMLPPluginModule implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    // Create SourceSet
    SourceSet moduleGenTimeSourceSet = project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().create("moduleGenTime");

    // Create the models jar
    Configuration outgoingModels = project.getConfigurations().create("umlpOutgoingModels");
    outgoingModels.setCanBeConsumed(true);
    outgoingModels.setCanBeResolved(false);
    outgoingModels.setDescription("Published models of the module");

    addModelsJarAttributes(outgoingModels, project);

    TaskProvider<Jar> modelsjarTask = project.getTasks().register("moduleModelsJar", Jar.class, jar -> {
      jar.setGroup("umlp");
      jar.getArchiveClassifier().set("models");
      jar.from(project.getLayout().getProjectDirectory().dir("models"));
    });
    project.getTasks().named(BasePlugin.ASSEMBLE_TASK_NAME).configure(it -> it.dependsOn(modelsjarTask));
    project.getArtifacts().add(outgoingModels.getName(), modelsjarTask);

    // Same thing for the module gen time

    Configuration moduleGenTime = project.getConfigurations().create("moduleGenTime");
    moduleGenTime.setCanBeConsumed(true);
    moduleGenTime.setCanBeResolved(false);
    moduleGenTime.setDescription("Published generation modifications of the module");

    addModuleGenJarJarAttributes(moduleGenTime, project);

    TaskProvider<Jar> genTimejarTask = project.getTasks().register("moduleGenTimeJar", Jar.class, jar -> {
      jar.setGroup("umlp");
      jar.getArchiveClassifier().set("module-generator-time");
      jar.from(project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().getByName("moduleGenTime").getOutput());
    });
    project.getTasks().named(BasePlugin.ASSEMBLE_TASK_NAME).configure(it -> it.dependsOn(genTimejarTask));
    project.getArtifacts().add(moduleGenTime.getName(), genTimejarTask);


  }

  public static void addModelsJarAttributes(Configuration config, Project project) {
    config.attributes(it -> {
      it.attribute(Category.CATEGORY_ATTRIBUTE, project.getObjects().named(Category.class, Category.LIBRARY));
      it.attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, "montigem-module-models"));
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
      it.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.getObjects().named(LibraryElements.class, LibraryElements.JAR));
    });
  }

  public static void addModuleGenJarJarAttributes(Configuration config, Project project) {
    config.attributes(it -> {
      it.attribute(Category.CATEGORY_ATTRIBUTE, project.getObjects().named(Category.class, Category.LIBRARY));
      it.attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, "montigem-module-gentime"));
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
      it.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.getObjects().named(LibraryElements.class, LibraryElements.JAR));
    });
  }
}
