package de.montigem.gradle.module;

import com.github.gradle.node.NodePlugin;
import com.github.gradle.node.npm.task.NpmTask;
import de.montigem.gradle.DependencyPlugin;
import de.montigem.gradle.MGBasePlugin;
import de.montigem.gradle.specific.CreateNpmrcTask;
import de.montigem.gradle.specific.GUI2JavaTask;
import de.montigem.gradle.specific.GUI2SymTask;
import de.montigem.gradle.specific.UMLP2SymTask;
import de.montigem.gradle.specific.UMLP2TSTask;
import de.montigem.gradle.specific.cd.CD2JavaTask;
import de.montigem.gradle.specific.serialize.CD2SymTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
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
import org.gradle.api.provider.Provider;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.plugins.PublishingPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static de.montigem.gradle.FrontendBasePlugin.GUI_TYPES_CONFIG;
import static de.montigem.gradle.FrontendBasePlugin.UMLP_GROUP_NAME;
import static de.montigem.gradle.FrontendBasePlugin.addClientRTEDependency;
import static de.montigem.gradle.FrontendBasePlugin.getBooleanProjectProperty;
import static de.montigem.gradle.FrontendBasePlugin.inRTE;
import static de.montigem.gradle.FrontendBasePlugin.mcVersion;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG;
import static de.montigem.gradle.MGBasePlugin.MONTIGEM_MODULE_IMPORT_MAP_CONFIG;


public class ModuleFrontendPlugin extends ALazyPublishingPlugin implements Plugin<Project> {

  public static final String GUI2SYM_TASK = "gui2sym";
  public static final String CD2SYM_TASK = "cd2sym";
  public static final String UMLP2SYM_TASK = "umlp2sym";
  public static final String UMLP2TS_TASK = "umlp2ts";
  public static final String CD2JAVA_TASK = "cd2java";
  public static final String GUI2JAVA_TASK = "gui2java";
  // java2ts and umlp2ts
  public static final String MODELS2TS_TASK = "models2ts";
  public static final String GUI2SYM_JAR_TASK = "gui2symJar";
  public static final String BUILD_LIB_TASK = "buildLib";

  @Inject
  public ModuleFrontendPlugin(SoftwareComponentFactory softwareComponentFactory, TaskDependencyFactory taskDependencyFactory) {
    super(softwareComponentFactory, taskDependencyFactory);
  }

  @Override
  public void apply(Project project) {
    project.getPluginManager().apply(NodePlugin.class);
    project.getPluginManager().apply("java-library");
    project.getPluginManager().apply("maven-publish");
    if (!inRTE(project)) {
      project.getPluginManager().apply(MGBasePlugin.class);
    }
    else {
      project.getPluginManager().apply(DependencyPlugin.class);
    }

    addClientRTEDependency(project);

    AdhocComponentWithVariants javaComponent = (AdhocComponentWithVariants) project.getComponents().getByName("java");
    // contains e.g. [ project(":umlp-rte:client:arrange"), project(":umlp-rte:client:basic") ]
    ModuleFrontendPluginExtension extension = project.getExtensions()
        .create("mglib", ModuleFrontendPluginExtension.class);

    // create .npmrc near build.gradle for `npm install`
    TaskProvider<CreateNpmrcTask> npmrc4download = project.getTasks()
        .register("npmrc4download", CreateNpmrcTask.class, task ->
            task.getNpmrcFile().set(project.getLayout().getProjectDirectory().file(".npmrc"))
        );
    TaskProvider<NpmTask> npmInstall = project.getTasks().named("npmInstall", NpmTask.class);
    npmInstall.configure(task -> task.dependsOn(npmrc4download));

    // default models folder
    Directory models = project.getLayout().getProjectDirectory().dir("../models");
    // default .umlp file(s)
    Set<File> umlp;
    // default name(s) of .umlp file(s)
    List<String> domainNames = new ArrayList<>();

    if (models.getAsFile().exists()) {
      umlp = models.getAsFileTree().matching(spec -> spec.include("**/*.umlp")).getFiles();
      if (!umlp.isEmpty()) {
        domainNames = umlp.stream()
            .map(File::getName)
            .map(name -> {
              int dotIndex = name.lastIndexOf('.');
              return (dotIndex != -1) ? name.substring(0, dotIndex) : name;
            })
            .collect(Collectors.toList());
      }
    }
    else {
      umlp = new HashSet<>();
    }
    List<String> domainNamesFinal = domainNames;

    var umlp2symOut = project.provider(() -> {
      Project parent = project.getParent();
      if (parent == null) {
        return project.files();
      }

      TaskProvider<UMLP2SymTask> task = parent.getTasks().named(UMLP2SYM_TASK, UMLP2SymTask.class);
      return task.flatMap(t -> t.getSymbolOutDir().map(dir -> {
        File f = dir.getAsFile();
        return f.exists() ? project.files(f) : project.files();
      }));
    });

    // Create the import map jar
    TaskProvider<Jar> umlp2impJar = umlp2impInit(project, javaComponent);
    TaskProvider<Jar> gui2symJar = gui2symInit(project, javaComponent);

    List<TaskProvider<NpmTask>> buildSubprojects = new ArrayList<>();
    List<TaskProvider<NpmTask>> publishSubprojects = new ArrayList<>();

    TaskProvider<Task> models2ts = project.getTasks().register(MODELS2TS_TASK);

    SourceSet mainSourceSet = project.getExtensions().getByType(SourceSetContainer.class).getByName(SourceSet.MAIN_SOURCE_SET_NAME);

    boolean local = getBooleanProjectProperty(project, "useLocalDependencies");
    if (local) {
      project.getDependencies().add("implementation",
          project.getDependencies().project(Map.of("path", ":umlp-rte:backend"))
      );
    }
    else {
      project.getDependencies().add("implementation", "umlp:umlp-rte:" + mcVersion(project));
    }

    project.afterEvaluate(p -> {
      for (Project subproject : extension.getLibs()) {
        subproject.getPluginManager().apply(NodePlugin.class);
        if (!inRTE(subproject)) {
          subproject.getPluginManager().apply(MGBasePlugin.class);
        }
        else {
          subproject.getPluginManager().apply(DependencyPlugin.class);
        }

        // java plugin is applied in build.gradle, disable compileJava for subprojects
        subproject.getTasks().matching(t -> t.getName().equals("compileJava"))
            .configureEach(t -> t.setEnabled(false));

        Configuration guiTypes = addProjectDependencies(subproject);
        addClientRTEDependency(subproject);

        Provider<String> libQName = extension.getLibScope().map(v -> v + "/" + subproject.getName());
        Provider<Directory> libBuildDir = p.getLayout().getBuildDirectory().dir("dist/" + subproject.getName());
        Provider<Directory> gui2javaDir = subproject.getLayout().getBuildDirectory().dir(subproject.provider(() -> "gui2java"));
        Provider<Directory> cd2javaDir = subproject.getLayout().getBuildDirectory().dir(subproject.provider(() -> "cd2java"));
        Provider<Directory> cd2tagsDir = subproject.getLayout().getBuildDirectory().dir(subproject.provider(() -> "cd2tags"));
        Provider<Directory> umlp2tsDir = subproject.getLayout().getBuildDirectory().dir(subproject.provider(() -> "umlp2ts"));

        Provider<Directory> srcTS = subproject.getLayout().getProjectDirectory().dir(subproject.provider(() -> "src/main/gui"));
        Provider<Directory> srcJava = subproject.getLayout().getProjectDirectory().dir(subproject.provider(() -> "src/main/java"));

        mainSourceSet.getJava().srcDir(srcJava);

        Provider<Boolean> guiPresent = srcTS
            .map(dir -> {
              File folder = dir.getAsFile();
              if (!folder.exists()) return false;

              Set<File> guiFiles = project.fileTree(folder, spec -> {
                spec.include("**/*.gui");
              }).getFiles();

              return !guiFiles.isEmpty();
            });

        TaskProvider<CD2SymTask> cd2sym = subproject.getTasks().register(CD2SYM_TASK, CD2SymTask.class, task -> {
          task.getInput().set(srcTS);
          // TODO: enable when CD2SymTask is replaced
        /*
        if (!inRTE(subproject) && local) {
          task.getSymbolPath().set(subproject.getConfigurations().getByName(GUI_LIB_NAME));
        }
        /**/
          task.getOutput().set(subproject.getLayout().getBuildDirectory().dir("cd2sym"));
        });

        gui2symJar.configure(task -> {
          task.from(
              cd2sym.flatMap(CD2SymTask::getOutput),
              spec -> spec.include("**/*sym", "**/*import.map", "**/*tags")
          );
          task.dependsOn(cd2sym);
        });

        TaskProvider<GUI2SymTask> gui2sym = subproject.getTasks().register(GUI2SYM_TASK, GUI2SymTask.class, task -> {
          task.getModelDir().set(srcTS);
          task.getInput().from(srcTS);
          task.getSymbolPath().from(guiTypes);
          task.getSymbolPath().from(umlp2symOut);
          task.getSymbolPath().from(cd2sym.flatMap(CD2SymTask::getOutput));
          if (!inRTE(project)) {
            task.getSymbolPath().from(subproject.getConfigurations().named(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG));
            task.getSymbolPath().from(subproject.getConfigurations().named(MONTIGEM_MODULE_IMPORT_MAP_CONFIG));
          }
          task.getOutputDir().set(subproject.getLayout().getBuildDirectory().dir("gui2sym"));
          task.getSymbolOutDir().set(subproject.getLayout().getBuildDirectory().dir("gui2sym"));

          task.dependsOn(cd2sym);
          task.onlyIf(t -> guiPresent.get());
        });
        gui2symJar.configure(task -> {
          task.from(
              gui2sym.flatMap(GUI2SymTask::getSymbolOutDir),
              spec -> spec.include("**/*sym", "**/*import.map", "**/*tags")
          );
          task.dependsOn(gui2sym);
        });

        TaskProvider<CD2JavaTask> cd2java = subproject.getTasks().register(CD2JAVA_TASK, CD2JavaTask.class);
        cd2java.configure(task -> {
          task.setGroup(UMLP_GROUP_NAME);
          task.getHwcDir().from(srcJava);
          task.getInput().from(
              srcTS.map(dir -> dir.getAsFileTree().matching(spec -> spec.include("**/*.cd")).getFiles())
          );
          task.getOutputDir().set(cd2javaDir);
          task.getSymbolOutDir().set(cd2tagsDir);
          task.getTslib().set(libQName);
        });

        mainSourceSet.getJava().srcDir(cd2java.flatMap(CD2JavaTask::getOutputDir));

        models2ts.configure(task -> task.dependsOn(cd2java));

        gui2symJar.configure(task -> {
          task.from(
              cd2java.flatMap(CD2JavaTask::getOutputDir),
              spec -> spec.include("**/*sym", "**/*import.map", "**/*tags")
          );
          task.from(
              cd2java.flatMap(CD2JavaTask::getSymbolOutDir),
              spec -> spec.include("**/*sym", "**/*import.map", "**/*tags")
          );
          task.dependsOn(cd2java);
        });

        TaskProvider<GUI2JavaTask> gui2java = subproject.getTasks().register(GUI2JAVA_TASK, GUI2JavaTask.class);
        gui2java.configure(task -> {
          task.dependsOn(cd2sym);
          if (!inRTE(project)) {
            task.getSymbolPath().from(subproject.getConfigurations().named(MONTIGEM_MODULE_GUI_SYMBOLS_CONFIG));
            task.getSymbolPath().from(subproject.getConfigurations().named(MONTIGEM_MODULE_IMPORT_MAP_CONFIG));
          }
          if (!domainNamesFinal.isEmpty()) {
            task.getDomainModels().set(domainNamesFinal);
          }
          task.getSymbolPath().from(umlp2symOut);
          task.getSymbolPath().from(guiTypes);
          task.getSymbolPath().from(cd2sym.flatMap(CD2SymTask::getOutput));
          task.getInput().from(srcTS);
          task.getModelDir().set(srcTS);
          task.getHwcDir().from(srcTS, srcJava);
          task.getOutputDir().set(gui2javaDir);
          /*
          task.getGuiGenJsweet().set(false);
          task.getGuiGenTSFuncionality().set(true);
          /**/
          task.getTslib().set(libQName);

          task.onlyIf(t -> guiPresent.get());
        });

        models2ts.configure(task -> task.dependsOn(gui2java));

        gui2symJar.configure(task -> {
          task.from(
              gui2java.flatMap(GUI2JavaTask::getOutputDir),
              spec -> spec.include("**/*sym", "**/*import.map", "**/*tags")
          );
          task.dependsOn(gui2java);
        });

        // TODO: remove after jsweet transition is complete and import mapping is reworked
        if (!umlp.isEmpty()) {
          TaskProvider<UMLP2TSTask> umlp2ts = subproject.getTasks().register(UMLP2TS_TASK, UMLP2TSTask.class);
          umlp2ts.configure(task -> {
            task.getInput().from(umlp);
            task.getHwcDir().from(srcTS, srcJava);
            task.getOutputDir().set(umlp2tsDir);
            task.getTslib().set(libQName);
          });

          gui2java.configure(task -> {
            task.getImportsDir().from(umlp2ts.flatMap(UMLP2TSTask::getOutputDir));
          });

          umlp2impJar.configure(task -> {
            task.dependsOn(umlp2ts);
            task.from(
                umlp2ts.flatMap(UMLP2TSTask::getOutputDir),
                spec -> spec.include("**/*import.map")
            );
          });
        }

        TaskProvider<NpmTask> buildLib = subproject.getTasks().register(BUILD_LIB_TASK, NpmTask.class, task -> {
          task.setGroup(UMLP_GROUP_NAME);
          task.dependsOn(npmInstall, gui2java);
          if (!inRTE(subproject) && local) {
            task.dependsOn(subproject.project(":umlp-rte:client").getTasks().named("build"));
          }
          task.getArgs().addAll(libQName.map(v -> List.of("run", "build", v)));
          task.getWorkingDir().set(project.getLayout().getProjectDirectory().getAsFile());
          task.getInputs().files(srcTS.map(dir -> dir.getAsFileTree().matching(pattern -> {
            pattern.exclude("**/build/**");
            pattern.exclude("**/*.spec.ts", "**/*.md");
          })));
          task.getInputs().files("angular.json", "tsconfig.json", "tsconfig.prod.json", "package.json");
          // e.g. `build/dist/login/`
          task.getOutputs().dir(libBuildDir);
        });
        buildSubprojects.add(buildLib);

        TaskProvider<CreateNpmrcTask> npmrc4publish = subproject.getTasks().register("npmrc4publish", CreateNpmrcTask.class, task -> {
          task.dependsOn(buildLib);
          task.getNpmrcFile().set(libBuildDir.map(dir -> dir.file(".npmrc")));
          task.getUnscoped().set(true);
        });

        TaskProvider<NpmTask> publishLib = subproject.getTasks().register("publishLib", NpmTask.class, task -> {
          task.setGroup(UMLP_GROUP_NAME);
          task.dependsOn(npmrc4publish);
          task.getArgs().set(List.of("publish"));
          // e.g. `build/dist/basic/`
          task.getWorkingDir().set(libBuildDir.map(dir -> dir.file(".")));
        });
        publishSubprojects.add(publishLib);
      }
    });

    TaskProvider<Task> build = project.getTasks().named("build");
    build.configure(task -> task.dependsOn(buildSubprojects));
    TaskProvider<Task> publish = project.getTasks().named("publish");
    publish.configure(task -> task.dependsOn(publishSubprojects));

    project.getPlugins().withType(PublishingPlugin.class, publishingPlugin -> {
      project.getExtensions().configure(PublishingExtension.class, publishing -> {
        publishing.getPublications().create("mavenJava", MavenPublication.class, publication -> {
          publication.from(javaComponent);
        });

        publishing.getRepositories().maven(mavenRepo -> {
          String releasesRepoUrl = "https://nexus.se.rwth-aachen.de/content/repositories/umlp-releases/";
          String snapshotsRepoUrl = "https://nexus.se.rwth-aachen.de/content/repositories/umlp-snapshots/";
          mavenRepo.setUrl(project.getVersion().toString().endsWith("SNAPSHOT") ? snapshotsRepoUrl : releasesRepoUrl);

          mavenRepo.credentials(credentials -> {
            credentials.setUsername((String) project.findProperty("mavenUser"));
            credentials.setPassword((String) project.findProperty("mavenPassword"));
          });
        });
      });
    });
  }

  public static void addUMLPSymbolsJarAttributes(Configuration config, Project project) {
    config.attributes(it -> {
      it.attribute(Category.CATEGORY_ATTRIBUTE, project.getObjects().named(Category.class, Category.LIBRARY));
      it.attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, "montigem-module-domain-symbols"));
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
      it.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.getObjects().named(LibraryElements.class, LibraryElements.JAR));
    });
  }

  protected TaskProvider<Jar> gui2symInit(Project project, AdhocComponentWithVariants javaComponent) {
    // Create the gui jar
    Configuration gui2symOutput = project.getConfigurations().create("gui2symOutput");
    gui2symOutput.setCanBeConsumed(true);
    gui2symOutput.setCanBeResolved(false);
    gui2symOutput.setDescription("Published GUI symbols of the module");

    addGuiSymbolsJarAttributes(gui2symOutput, project);

    TaskProvider<Jar> gui2symJar = project.getTasks().register(GUI2SYM_JAR_TASK, Jar.class, task -> {
      task.getArchiveClassifier().set("guisymbols");
      task.setIncludeEmptyDirs(false);
    });
    project.getTasks().named(org.gradle.api.plugins.BasePlugin.ASSEMBLE_TASK_NAME).configure(it -> it.dependsOn(gui2symJar));

    // Add createGuiSymbolsJar-jar as artifact to the config
    project.getArtifacts().add(gui2symOutput.getName(), gui2symJar);
    javaComponent.addVariantsFromConfiguration(gui2symOutput, ConfigurationVariantDetails::mapToOptional);

    // Tell gradle, that the jar tasks builds a publication
    // and add the publication to the outgoing artifacts
    gui2symOutput.getOutgoing().getArtifacts().add(createPublishedArtifact(gui2symJar, project));
    return gui2symJar;
  }

  public static void addGuiSymbolsJarAttributes(Configuration config, Project project) {
    config.attributes(it -> {
      it.attribute(Category.CATEGORY_ATTRIBUTE, project.getObjects().named(Category.class, Category.LIBRARY));
      it.attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, "montigem-module-gui-symbols"));
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
      it.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.getObjects().named(LibraryElements.class, LibraryElements.JAR));
    });
  }

  protected TaskProvider<Jar> umlp2impInit(Project project, AdhocComponentWithVariants javaComponent) {
    Configuration umlp2impOutput = project.getConfigurations().create("umlp2impOutput");
    umlp2impOutput.setCanBeConsumed(true);
    umlp2impOutput.setCanBeResolved(false);
    umlp2impOutput.setDescription("Published domain import map of the module");

    addImportMapJarAttributes(umlp2impOutput, project);

    TaskProvider<Jar> umlp2impJar = project.getTasks().register("umlp2impJar", Jar.class, task -> {
      task.setGroup("umlp");
      task.getArchiveClassifier().set("importmap");
      task.setIncludeEmptyDirs(false);
    });
    project.getTasks().named(org.gradle.api.plugins.BasePlugin.ASSEMBLE_TASK_NAME).configure(it -> it.dependsOn(umlp2impJar));

    // Add jar as artifact to the config
    project.getArtifacts().add(umlp2impOutput.getName(), umlp2impJar);
    javaComponent.addVariantsFromConfiguration(umlp2impOutput, ConfigurationVariantDetails::mapToOptional);

    // Tell gradle, that the jar tasks builds a publication
    // and add the publication to the outgoing artifacts
    umlp2impOutput.getOutgoing().getArtifacts().add(
        createPublishedArtifact(umlp2impJar, project)
    );
    return umlp2impJar;
  }

  public static void addImportMapJarAttributes(Configuration config, Project project) {
    config.attributes(it -> {
      it.attribute(Category.CATEGORY_ATTRIBUTE, project.getObjects().named(Category.class, Category.LIBRARY));
      it.attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, "montigem-module-import-map"));
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
      it.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.getObjects().named(LibraryElements.class, LibraryElements.JAR));
    });
  }

  private Configuration addProjectDependencies(Project project) {
    // repositories
    project.getRepositories().maven(repo -> {
      String mavenUser = (String) project.findProperty("mavenUser");
      String mavenPassword = (String) project.findProperty("mavenPassword");
      String url = (String) project.findProperty("repo");
      repo.getCredentials().setUsername(mavenUser);
      repo.getCredentials().setPassword(mavenPassword);
      repo.setUrl(url);
    });
    if (getBooleanProjectProperty(project, "useLocalRepo")) {
      project.getRepositories().mavenLocal();
    }
    project.getRepositories().mavenCentral();

    // Configurations
    var conf = project.getConfigurations().create(GUI_TYPES_CONFIG, config -> {
      config.setCanBeConsumed(false);
      config.setCanBeResolved(true);
      config.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, "guidsl-types"));
      config.getAttributes().attribute(Category.CATEGORY_ATTRIBUTE, project.getObjects().named(Category.class, "library"));
      config.getAttributes().attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.getObjects().named(LibraryElements.class, "jar"));
    });
    project.getDependencies().add(GUI_TYPES_CONFIG, "de.monticore.lang:guidsl:" + project.findProperty("mc_version"));
    return conf;
  }

}
