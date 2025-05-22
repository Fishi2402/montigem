package gradle.module;

import gradle.UMLPPluginFrontendBase;
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
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;

import javax.inject.Inject;


public class UMLPPluginModuleJSFrontend extends ALazyPublishingPlugin implements Plugin<Project> {

  @Inject
  public UMLPPluginModuleJSFrontend(SoftwareComponentFactory softwareComponentFactory, TaskDependencyFactory taskDependencyFactory) {
    super(softwareComponentFactory, taskDependencyFactory);
  }

  @Override
  public void apply(Project project) {
    var javaComponent = (AdhocComponentWithVariants) project.getComponents().getByName("java");
    {
      // Create the domain symbols jar
      Configuration outgoingDomainSymbols = project.getConfigurations().create("umlpOutgoingDomainSymbols");
      outgoingDomainSymbols.setCanBeConsumed(true);
      outgoingDomainSymbols.setCanBeResolved(false);
      outgoingDomainSymbols.setDescription("Published domain Symbols of the module");

      addDomainSymbolsJarAttributes(outgoingDomainSymbols, project);

      TaskProvider<Jar> jarTask = project.getTasks().register("createDomainSymbolsJar", Jar.class, jar -> {
        jar.setGroup("umlp");
        jar.getArchiveClassifier().set("domainsymbols");
        jar.from(project.getTasks().named("createDomainSymbols")).include("**/*sym");
        jar.dependsOn(project.getTasks().named("createDomainSymbols"));
      });
      project.getTasks().named(BasePlugin.ASSEMBLE_TASK_NAME).configure(it -> it.dependsOn(jarTask));

      // Add createDomainSymbolsJar-jar as artifact to the config
      project.getArtifacts().add(outgoingDomainSymbols.getName(), jarTask);
      javaComponent.addVariantsFromConfiguration(outgoingDomainSymbols, ConfigurationVariantDetails::mapToOptional);

      // Tell gradle, that the jar tasks builds a publication
      var publ = createPublishedArtifact(jarTask, project);
      // and add the publication to the outgoing artifacts
      outgoingDomainSymbols.getOutgoing().getArtifacts().add(publ);
    }

    // TODO: import map should be in domain symbols jar
    //  remove when using symbols Jar for module system without models jar is enough
    {
      // Create the import map jar
      Configuration outgoingImportMap = project.getConfigurations().create("umlpOutgoingImportMap");
      outgoingImportMap.setCanBeConsumed(true);
      outgoingImportMap.setCanBeResolved(false);
      outgoingImportMap.setDescription("Published domain import map of the module");

      addImportMapJarAttributes(outgoingImportMap, project);

      TaskProvider<Jar> jarTask = project.getTasks().register("createImportMapJar", Jar.class, jar -> {
        jar.setGroup("umlp");
        jar.getArchiveClassifier().set("importmap");
        jar.from(project.getTasks().named("genDomain")).include("**/*import.map");
        jar.dependsOn(project.getTasks().named("genDomain"));
        jar.setIncludeEmptyDirs(false);
      });
      project.getTasks().named(BasePlugin.ASSEMBLE_TASK_NAME).configure(it -> it.dependsOn(jarTask));

      // Add jar as artifact to the config
      project.getArtifacts().add(outgoingImportMap.getName(), jarTask);
      javaComponent.addVariantsFromConfiguration(outgoingImportMap, ConfigurationVariantDetails::mapToOptional);

      // Tell gradle, that the jar tasks builds a publication
      var publ = createPublishedArtifact(jarTask, project);
      // and add the publication to the outgoing artifacts
      outgoingImportMap.getOutgoing().getArtifacts().add(publ);
    }

    {
      // Create the gui jar
      Configuration outgoingGuiSymbols = project.getConfigurations().create("umlpOutgoingGuiSymbols");
      outgoingGuiSymbols.setCanBeConsumed(true);
      outgoingGuiSymbols.setCanBeResolved(false);
      outgoingGuiSymbols.setDescription("Published gui Symbols of the module");

      addGuiSymbolsJarAttributes(outgoingGuiSymbols, project);

      TaskProvider<Jar> jarTask = project.getTasks().register("createGuiSymbolsJar", Jar.class, jar -> {
        jar.setGroup("umlp");
        jar.getArchiveClassifier().set("guisymbols");
        jar.from(project.getTasks().named(UMLPPluginFrontendBase.UMLP_FRONTEND_GUI_TASK_NAME))
            .include("**/*sym", "**/*import.map");
        jar.dependsOn(project.getTasks().named(UMLPPluginFrontendBase.UMLP_FRONTEND_GUI_TASK_NAME));
        jar.setIncludeEmptyDirs(false);
        jar.doLast(task -> {
          ((Jar) task).getArchiveFile().get().getAsFile().toPath().toFile().listFiles(); // or inspect with zip tools
        });
      });
      project.getTasks().named(BasePlugin.ASSEMBLE_TASK_NAME).configure(it -> it.dependsOn(jarTask));

      // Add createGuiSymbolsJar-jar as artifact to the config
      project.getArtifacts().add(outgoingGuiSymbols.getName(), jarTask);
      javaComponent.addVariantsFromConfiguration(outgoingGuiSymbols, ConfigurationVariantDetails::mapToOptional);

      // Tell gradle, that the jar tasks builds a publication
      var publ = createPublishedArtifact(jarTask, project);
      // and add the publication to the outgoing artifacts
      outgoingGuiSymbols.getOutgoing().getArtifacts().add(publ);
    }

    /// Also prepare publishing of

    /*
    Configuration outgoingBundlesJS = project.getConfigurations().create("umlpOutgoingFrontendBundle");
    outgoingBundlesJS.setCanBeConsumed(true);
    outgoingBundlesJS.setCanBeResolved(false);
    outgoingBundlesJS.setDescription("Published frontend source");

    addBundledJSJarAttributes(outgoingBundlesJS, project);

    TaskProvider<Jar> bundleTask = project.getTasks().register("packFrontendAsZip", Jar.class, jar -> {
      jar.setGroup("umlp");
      jar.getArchiveClassifier().set("bundlejs");
    });
    project.getTasks().named(BasePlugin.ASSEMBLE_TASK_NAME).configure(it->it.dependsOn(bundleTask));

    // Add outgoingBundlesJS-jar as artifact to the config
    project.getArtifacts().add(outgoingBundlesJS.getName(), bundleTask);
    javaComponent.addVariantsFromConfiguration(outgoingBundlesJS, ConfigurationVariantDetails::mapToOptional);
    /**/

    // Tell gradle, that the jar tasks builds a publication
    // var bundleArtifact = createPublishedArtifact(jarTask, project);
    // and add the publication to the outgoing artifacts
    // outgoingBundlesJS.getOutgoing().getArtifacts().add(bundleArtifact);

    // artifacts & addVariant is for now done in the module-frontends build.gradle
  }

  public static void addDomainSymbolsJarAttributes(Configuration config, Project project) {
    config.attributes( it -> {
      it.attribute(Category.CATEGORY_ATTRIBUTE, project.getObjects().named(Category.class, Category.LIBRARY));
      it.attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, "montigem-module-domain-symbols"));
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
      it.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.getObjects().named(LibraryElements.class, LibraryElements.JAR));
    });
  }

  public static void addGuiSymbolsJarAttributes(Configuration config, Project project) {
    config.attributes( it -> {
      it.attribute(Category.CATEGORY_ATTRIBUTE, project.getObjects().named(Category.class, Category.LIBRARY));
      it.attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, "montigem-module-gui-symbols"));
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
      it.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.getObjects().named(LibraryElements.class, LibraryElements.JAR));
    });
  }

  public static void addImportMapJarAttributes(Configuration config, Project project) {
    config.attributes(it -> {
      it.attribute(Category.CATEGORY_ATTRIBUTE, project.getObjects().named(Category.class, Category.LIBRARY));
      it.attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, "montigem-module-import-map"));
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
      it.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.getObjects().named(LibraryElements.class, LibraryElements.JAR));
    });
  }

  /*
  public static void addBundledJSJarAttributes(Configuration config, Project project) {
    config.attributes( it -> {
      it.attribute(Category.CATEGORY_ATTRIBUTE, project.getObjects().named(Category.class, Category.LIBRARY));
      it.attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, "montigem-module-frontend-ts-bundle"));
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
      it.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.getObjects().named(LibraryElements.class, LibraryElements.JAR));
    });
  }
  /**/
}
