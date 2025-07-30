package gradle.module;

import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class UMLPModuleFrontendPluginExtension {
  private final List<Project> libs = new ArrayList<>();
  private final Property<String> libScope;

  @Inject
  public UMLPModuleFrontendPluginExtension(ObjectFactory objects) {
    this.libScope = objects.property(String.class);
    this.libScope.convention("@montigem");
  }

  public List<Project> getLibs() {
    return libs;
  }

  public Property<String> getLibScope() {
    return libScope;
  }
}
