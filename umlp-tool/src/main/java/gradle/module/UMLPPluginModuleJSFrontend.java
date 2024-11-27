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
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;


public class UMLPPluginModuleJSFrontend implements Plugin<Project> {
  @Override
  public void apply(Project project) {
    // Create the models jar

    Configuration outgoingDomainSymbols = project.getConfigurations().create("umlpOutgoingDomainSymbols");
    outgoingDomainSymbols.setCanBeConsumed(true);
    outgoingDomainSymbols.setCanBeResolved(false);
    outgoingDomainSymbols.setDescription("Published domain Symbols of the module");

    addDomainSymbolsJarAttributes(outgoingDomainSymbols, project);

    TaskProvider<Jar> jarTask = project.getTasks().register("createDomainSymbolsJar", Jar.class, jar -> {
      jar.setGroup("umlp");
      jar.getArchiveClassifier().set("domainsymbols");
      jar.from(project.getTasks().named("createDomainSymbols"));
    });
   project.getTasks().named(BasePlugin.ASSEMBLE_TASK_NAME).configure(it->it.dependsOn(jarTask));

   // Add createDomainSymbolsJar-jar as artifact to the config
   project.getArtifacts().add(outgoingDomainSymbols.getName(), jarTask);
   var javaComponent = (AdhocComponentWithVariants)project.getComponents().getByName("java");
   javaComponent.addVariantsFromConfiguration(outgoingDomainSymbols, ConfigurationVariantDetails::mapToOptional);


  }

  public static void addDomainSymbolsJarAttributes(Configuration config, Project project) {
    config.attributes( it -> {
      it.attribute(Category.CATEGORY_ATTRIBUTE, project.getObjects().named(Category.class, Category.LIBRARY));
      it.attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, "montigem-module-domain-symbols"));
      it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.getObjects().named(Bundling.class, Bundling.EXTERNAL));
      it.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, project.getObjects().named(LibraryElements.class, LibraryElements.JAR));
    });
  }
}
