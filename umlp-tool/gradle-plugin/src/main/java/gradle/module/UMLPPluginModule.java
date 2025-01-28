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

public class UMLPPluginModule implements Plugin<Project> {

  final SoftwareComponentFactory softwareComponentFactory;
  final TaskDependencyFactory taskDependencyFactory;

  @Inject
  public UMLPPluginModule(SoftwareComponentFactory softwareComponentFactory, TaskDependencyFactory taskDependencyFactory) {
    this.softwareComponentFactory = softwareComponentFactory;
    this.taskDependencyFactory = taskDependencyFactory;
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
      jar.from(project.getLayout().getProjectDirectory().dir("models"));
    });
    project.getTasks().named(BasePlugin.ASSEMBLE_TASK_NAME).configure(it -> it.dependsOn(modelsjarTask));
    project.getArtifacts().add(outgoingModels.getName(), modelsjarTask);

    // Add a variant
    var javaComponent = (AdhocComponentWithVariants)project.getComponents().getByName("java");
    javaComponent.addVariantsFromConfiguration(outgoingModels, ConfigurationVariantDetails::mapToOptional);



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

  /**
   * Create a lazy {@link org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact} from a given
   * {@link Jar} task.
   * Provides compatibility between gradle 7 and 8
   */
  protected PublishArtifact createPublishedArtifact(TaskProvider<Jar> grammarsJarTask, Project project) {
    // the LazyPublishArtifact constructor has changed between gradle versions:
    try {
      // Gradle 7.4.2
      return new LazyPublishArtifact(grammarsJarTask, null, ((ProjectInternal) project).getFileResolver());
    } catch (NoSuchMethodError incompatibleVersion) {
      try {
        // Gradle 8 - the TaskDependencyFactory parameter was added
        return LazyPublishArtifact.class.getConstructor(org.gradle.api.provider.Provider.class, String.class, FileResolver.class, TaskDependencyFactory.class)
            .newInstance(grammarsJarTask, null, ((ProjectInternal) project).getFileResolver(), taskDependencyFactory);
      } catch (ReflectiveOperationException e) {
        throw new IllegalStateException("Incompatible LazyPublishArtifact constructor in gradle " + project.getGradle().getGradleVersion(), e);
      }
    }
  }
}
