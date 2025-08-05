package de.montigem.gradle.module;

import de.montigem.gradle.MGBasePlugin;
import de.montigem.gradle.specific.UMLP2SymTask;
import de.se_rwth.commons.logging.Log;
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
import org.gradle.api.file.Directory;
import org.gradle.api.internal.tasks.TaskDependencyFactory;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;

import javax.inject.Inject;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_IMPORT_MAP_CONFIG;
import static de.montigem.gradle.module.ModuleFrontendPlugin.addGuiSymbolsJarAttributes;
import static de.montigem.gradle.module.ModuleFrontendPlugin.addImportMapJarAttributes;
import static de.montigem.gradle.module.ModuleFrontendPlugin.addUMLPSymbolsJarAttributes;
import static de.montigem.gradle.module.ModuleFrontendPlugin.inRTE;

public class ModulePlugin extends ALazyPublishingPlugin implements Plugin<Project> {

  public static final String UMLP2SYM_TASK = "umlp2sym";
  public static final String UMLP2SYM_CONFIG = "umlp2symConfig";
  public static final String GUI2SYM_CONFIG = "gui2symConfig";
  public static final String GUI2JAVA_CONFIG = "gui2javaConfig";
  public static final String MODULE_GENERATOR_EXTENSION_COMPILE_CONFIG = "moduleGeneratorExtensionCompile";

  @Inject
  public ModulePlugin(SoftwareComponentFactory softwareComponentFactory, TaskDependencyFactory taskDependencyFactory) {
    super(softwareComponentFactory, taskDependencyFactory);
  }

  @Override
  public void apply(Project project) {
    project.getPlugins().apply("base");
    project.getPlugins().apply("java-library");
    project.getPlugins().apply("maven-publish");
    project.getPlugins().apply(MGBasePlugin.class);

    Configuration moduleGenerationExtensionCompile = project.getConfigurations().create(MODULE_GENERATOR_EXTENSION_COMPILE_CONFIG);
    moduleGenerationExtensionCompile.setCanBeResolved(true);
    moduleGenerationExtensionCompile.setCanBeConsumed(false);
    if (project.hasProperty("mc_version")) {
      String mcVersion = project.property("mc_version").toString();
      project.getDependencies().add(moduleGenerationExtensionCompile.getName(), "de.monticore:monticore-runtime:" + mcVersion);
      project.getDependencies().add(moduleGenerationExtensionCompile.getName(), "de.monticore:monticore-grammar:" + mcVersion);
      project.getDependencies().add(moduleGenerationExtensionCompile.getName(), "umlp:umlp-tool:" + mcVersion);
    }
    Project backend = project.findProject(project.getPath() + ":backend");
    if (backend != null) {
      project.getDependencies().add("api", backend);
      SourceSet mainSourceSet = project.getExtensions().getByType(SourceSetContainer.class).getByName("main");

      project.getTasks().named("jar", Jar.class).configure(jar -> {
        jar.from(mainSourceSet.getOutput());
      });
    }

    var javaComponent = (AdhocComponentWithVariants) project.getComponents().getByName("java");

    Directory models = project.getLayout().getProjectDirectory().dir("models");

    Set<File> umlp;
    boolean umlpPresent;
    if (models.getAsFile().exists()) {
      umlp = models.getAsFileTree().matching(spec -> spec.include("**/*.umlp")).getFiles();
      umlpPresent = !umlp.isEmpty();
    }
    else {
      umlpPresent = false;
      umlp = new HashSet<>();
    }

    // Create the models jar
    Configuration umlpModels = project.getConfigurations().create("umlpModels");
    umlpModels.setCanBeConsumed(true);
    umlpModels.setCanBeResolved(false);
    umlpModels.setDescription("Published models of the module");

    addUMLPModelsJarAttributes(umlpModels, project);

    TaskProvider<Jar> umlpModelsJar = project.getTasks().register("umlpModelsJar", Jar.class, jar -> {
      jar.setGroup("umlp");
      jar.getArchiveClassifier().set("models");
      jar.from(umlp);

      jar.onlyIf(t -> umlpPresent);
    });
    project.getTasks().named(BasePlugin.ASSEMBLE_TASK_NAME).configure(it -> it.dependsOn(umlpModelsJar));
    if (umlpPresent) {
      project.getArtifacts().add(umlpModels.getName(), umlpModelsJar);
      // Tell gradle, that the jar tasks builds a publication
      var umlpModelsPublication = createPublishedArtifact(umlpModelsJar, project);
      // Add a variant
      javaComponent.addVariantsFromConfiguration(umlpModels, ConfigurationVariantDetails::mapToOptional);
      // Add the publication as an artifact
      umlpModels.getOutgoing().getArtifacts().add(umlpModelsPublication);
    }

    // Same thing for the module gen time
    Configuration generatorExtension = project.getConfigurations().create("generatorExtension");
    generatorExtension.setCanBeConsumed(true);
    generatorExtension.setCanBeResolved(false);
    generatorExtension.setDescription("Published generation modifications of the module");

    addGeneratorExtensionJarAttributes(generatorExtension, project);

    SourceSet generatorExtensionSourceSet = project.getExtensions().getByType(JavaPluginExtension.class)
        .getSourceSets().create("generatorExtension");
    generatorExtensionSourceSet.setCompileClasspath(
        generatorExtensionSourceSet.getCompileClasspath().plus(moduleGenerationExtensionCompile)
    );
    TaskProvider<Jar> generatorExtensionJar = project.getTasks().register("generatorExtensionJar", Jar.class, jar -> {
      jar.onlyIf(t -> generatorExtensionSourceSet.getAllSource().getFiles().stream().anyMatch(File::exists));

      jar.setGroup("umlp");
      jar.getArchiveClassifier().set("module-generator-time");
      jar.from(generatorExtensionSourceSet.getOutput());
    });
    project.getTasks().named(BasePlugin.ASSEMBLE_TASK_NAME).configure(it -> it.dependsOn(generatorExtensionJar));
    project.afterEvaluate(p -> {
      Jar jar = generatorExtensionJar.get();
      if (jar.getOnlyIf().isSatisfiedBy(jar)) {
        project.getArtifacts().add(generatorExtension.getName(), generatorExtensionJar);
        javaComponent.addVariantsFromConfiguration(generatorExtension, ConfigurationVariantDetails::mapToOptional);

        // Tell gradle, that the jar tasks builds a publication
        var genTimePublication = createPublishedArtifact(generatorExtensionJar, project);
        // Add the publication as an artifact
        generatorExtension.getOutgoing().getArtifacts().add(genTimePublication);
      }
      else {
        Log.info("Skipping artifact: no sources in source set", getClass().getName());
      }
    });

    TaskProvider<UMLP2SymTask> umlp2sym = project.getTasks().register(UMLP2SYM_TASK, UMLP2SymTask.class, task -> {
      task.getModelDir().from(models);
      task.getInput().from(models);
      if (!inRTE(project)) {
        task.getSymbolPath().from(project.getConfigurations().named(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG));
        task.getSymbolPath().from(project.getConfigurations().named(MONTIGEM_MODULE_IMPORT_MAP_CONFIG));
      }
      task.getSymbolOutDir().set(project.getLayout().getBuildDirectory().dir("umlp2sym"));
      task.getOutputDir().set(project.getLayout().getBuildDirectory().dir("umlp2sym"));

      task.onlyIf(t -> umlpPresent);
    });
    var umlp2symOut = umlp2sym
        .flatMap(UMLP2SymTask::getSymbolOutDir)
        .map(dir -> dir.getAsFile().exists() ? dir.getAsFile() : Collections.emptyList());

    TaskProvider<Jar> umlp2symJar = umlp2symInit(umlp2sym, project, javaComponent);
    umlp2symJar.configure(task -> {
      task.dependsOn(umlp2sym);
      task.from(
          umlp2sym.flatMap(UMLP2SymTask::getOutputDir),
          spec -> spec.include("**/*sym", "**/*tags")
      );

      task.onlyIf(t -> umlpPresent);
    });

    Project frontend = project.findProject(project.getPath() + ":frontend");
    if (frontend == null) {
      // TODO: remove after renaming
      frontend = project.findProject(project.getPath() + ":client");
    }
    if (frontend != null) {
      Configuration gui2symConfig = project.getConfigurations().create(GUI2SYM_CONFIG, conf -> {
        conf.setCanBeConsumed(true);
        addGuiSymbolsJarAttributes(conf, project);
      });
      project.getDependencies().add(gui2symConfig.getName(), frontend);
      javaComponent.addVariantsFromConfiguration(gui2symConfig, ConfigurationVariantDetails::mapToOptional);

      Configuration gui2javaConfig = project.getConfigurations().create(GUI2JAVA_CONFIG, conf -> {
        conf.setCanBeConsumed(true);
        addImportMapJarAttributes(conf, project);
      });
      project.getDependencies().add(gui2javaConfig.getName(), frontend);
      javaComponent.addVariantsFromConfiguration(gui2javaConfig, ConfigurationVariantDetails::mapToOptional);
    }

  }

  public static void addUMLPModelsJarAttributes(Configuration config, Project project) {
    config.attributes(it -> {
      it.attribute(Category.CATEGORY_ATTRIBUTE, project.getObjects().named(Category.class, Category.LIBRARY));
      it.attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, "montigem-module-models"));
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
      it.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.getObjects().named(LibraryElements.class, LibraryElements.JAR));
    });
  }

  public static void addGeneratorExtensionJarAttributes(Configuration config, Project project) {
    config.attributes(it -> {
      it.attribute(Category.CATEGORY_ATTRIBUTE, project.getObjects().named(Category.class, Category.LIBRARY));
      it.attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, "montigem-module-gentime"));
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
      it.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.getObjects().named(LibraryElements.class, LibraryElements.JAR));
    });
  }

  protected TaskProvider<Jar> umlp2symInit(
      TaskProvider<UMLP2SymTask> umlp2sym, Project project, AdhocComponentWithVariants javaComponent
  ) {
    TaskProvider<Jar> umlp2symJar = project.getTasks().register("umlp2symJar", Jar.class, task -> {
      task.getArchiveClassifier().set("domainsymbols");
    });
    project.getTasks().named(org.gradle.api.plugins.BasePlugin.ASSEMBLE_TASK_NAME).configure(it -> it.dependsOn(umlp2symJar));

    project.afterEvaluate(p -> {
      if (umlp2sym.get().getOnlyIf().isSatisfiedBy(umlp2sym.get())) {
        // Create the domain symbols jar
        Configuration umlp2symConfig = project.getConfigurations().create(UMLP2SYM_CONFIG);
        umlp2symConfig.setCanBeConsumed(true);
        umlp2symConfig.setCanBeResolved(false);
        umlp2symConfig.setDescription("Published domain Symbols of the module");

        addUMLPSymbolsJarAttributes(umlp2symConfig, project);

        // Add createDomainSymbolsJar-jar as artifact to the config
        project.getArtifacts().add(umlp2symConfig.getName(), umlp2symJar);
        javaComponent.addVariantsFromConfiguration(umlp2symConfig, ConfigurationVariantDetails::mapToOptional);

        // Tell gradle, that the jar tasks builds a publication
        // and add the publication to the outgoing artifacts
        umlp2symConfig.getOutgoing().getArtifacts().add(
            createPublishedArtifact(umlp2symJar, project)
        );
      }
    });

    return umlp2symJar;
  }

}
