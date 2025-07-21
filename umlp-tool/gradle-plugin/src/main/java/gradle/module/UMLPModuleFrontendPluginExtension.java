package gradle.module;

import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;

public class UMLPModuleFrontendPluginExtension {
  private final List<Project> libs = new ArrayList<>();

  public List<Project> getLibs() {
    return libs;
  }
}
