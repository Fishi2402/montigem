package gradle.module;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Bundling;
import org.gradle.api.attributes.Category;
import org.gradle.api.attributes.LibraryElements;
import org.gradle.api.attributes.Usage;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.ConfigurationVariantDetails;
import org.gradle.api.component.SoftwareComponentFactory;
import org.gradle.api.internal.tasks.TaskDependencyFactory;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;

import javax.inject.Inject;

import java.util.Objects;

import static gradle.module.UMLPPluginModuleFrontend.addDomainSymbolsJarAttributes;
import static gradle.module.UMLPPluginModuleFrontend.addGuiSymbolsJarAttributes;
import static gradle.module.UMLPPluginModuleFrontend.addImportMapJarAttributes;

public class UMLPPluginModule extends ALazyPublishingPlugin implements Plugin<Project> {

  public static final String UMLP2SYM_CONF = "umlp2symConf";
  public static final String GUI2SYM_CONF = "gui2symConf";
  public static final String GUI2JAVA_CONF = "gui2javaConf";

  @Inject
  public UMLPPluginModule(SoftwareComponentFactory softwareComponentFactory, TaskDependencyFactory taskDependencyFactory) {
    super(softwareComponentFactory, taskDependencyFactory);
  }

  @Override
  public void apply(Project project) {
    project.getPlugins().apply("base");
    project.getPlugins().apply("java-library");
    project.getPlugins().apply("maven-publish");

    Configuration moduleGenTimeCompile = project.getConfigurations().create("moduleGenTimeCompile");
    Project umlpTool = project.findProject(":umlp:umlp-tool");
    if (umlpTool != null) {
      project.getDependencies().add(moduleGenTimeCompile.getName(), umlpTool);
    }
    if (project.hasProperty("mc_version")) {
      String mcVersion = project.property("mc_version").toString();
      project.getDependencies().add(moduleGenTimeCompile.getName(), "de.monticore.lang:cd4analysis:" + mcVersion);
      project.getDependencies().add(moduleGenTimeCompile.getName(), "de.monticore:monticore-runtime:" + mcVersion);
      project.getDependencies().add(moduleGenTimeCompile.getName(), "de.monticore:monticore-grammar:" + mcVersion);
    }

    // Create SourceSet
    project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().create("moduleGenTime");

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
    var javaComponent = (AdhocComponentWithVariants) project.getComponents().getByName("java");
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

    Project frontend = project.findProject(project.getPath() + ":frontend");
    if (frontend != null) {
      Configuration umlp2symConf = project.getConfigurations().create(UMLP2SYM_CONF, conf -> {
        conf.setCanBeConsumed(true);
        addDomainSymbolsJarAttributes(conf, project);
      });
      project.getDependencies().add(umlp2symConf.getName(), frontend);
      javaComponent.addVariantsFromConfiguration(umlp2symConf, ConfigurationVariantDetails::mapToOptional);

      Configuration gui2symConf = project.getConfigurations().create(GUI2SYM_CONF, conf -> {
        conf.setCanBeConsumed(true);
        addGuiSymbolsJarAttributes(conf, project);
      });
      project.getDependencies().add(gui2symConf.getName(), frontend);
      javaComponent.addVariantsFromConfiguration(gui2symConf, ConfigurationVariantDetails::mapToOptional);

      Configuration gui2javaConf = project.getConfigurations().create(GUI2JAVA_CONF, conf -> {
        conf.setCanBeConsumed(true);
        addImportMapJarAttributes(conf, project);
      });
      project.getDependencies().add(gui2javaConf.getName(), frontend);
      javaComponent.addVariantsFromConfiguration(gui2javaConf, ConfigurationVariantDetails::mapToOptional);
    }

    Project backend = project.findProject(project.getPath() + ":backend");
    if (backend != null) {
      project.getDependencies().add("api", backend);
    }
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
