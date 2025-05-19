package gradle.module;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.attributes.Bundling;
import org.gradle.api.attributes.Category;
import org.gradle.api.attributes.LibraryElements;
import org.gradle.api.attributes.Usage;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.ConfigurationVariantDetails;
import org.gradle.api.component.SoftwareComponentFactory;
import org.gradle.api.internal.artifacts.dsl.LazyPublishArtifact;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.internal.tasks.TaskDependencyFactory;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;

import javax.inject.Inject;

public class UMLPPluginModule extends ALazyPublishingPlugin implements Plugin<Project> {

  @Inject
  public UMLPPluginModule(SoftwareComponentFactory softwareComponentFactory, TaskDependencyFactory taskDependencyFactory) {
    super(softwareComponentFactory, taskDependencyFactory);
  }

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
      jar.from(project.getLayout().getProjectDirectory().dir("models")).include("**/*umlp");
    });
    project.getTasks().named(BasePlugin.ASSEMBLE_TASK_NAME).configure(it -> it.dependsOn(modelsjarTask));
    project.getArtifacts().add(outgoingModels.getName(), modelsjarTask);

    // Tell gradle, that the jar tasks builds a publication
    var modelsPublication = createPublishedArtifact(modelsjarTask, project);

    // Add a variant
    var javaComponent = (AdhocComponentWithVariants)project.getComponents().getByName("java");
    javaComponent.addVariantsFromConfiguration(outgoingModels, ConfigurationVariantDetails::mapToOptional);

    // Add the publication as an artifact
    outgoingModels.getOutgoing().getArtifacts().add(modelsPublication);



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
    javaComponent.addVariantsFromConfiguration(moduleGenTime, ConfigurationVariantDetails::mapToOptional);

    // Tell gradle, that the jar tasks builds a publication
    var genTimePublication = createPublishedArtifact(genTimejarTask, project);
    // Add the publication as an artifact
    moduleGenTime.getOutgoing().getArtifacts().add(genTimePublication);
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
