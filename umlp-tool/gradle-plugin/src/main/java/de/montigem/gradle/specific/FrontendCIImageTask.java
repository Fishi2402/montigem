package de.montigem.gradle.specific;

import java.util.Objects;

public abstract class FrontendCIImageTask extends FrontendImageTask {

  public FrontendCIImageTask() {
    getFullImageName().convention(getProject().provider(() -> {
        String result = System.getProperty("umlp.deploy.frontend.to.image");
        if(result == null) {
         result = Objects.requireNonNull(System.getenv("CI_REGISTRY_IMAGE"), "CI_REGISTRY_IMAGE not set")
              + "/frontend:latest";
        }
        return result;
    }));
    getUseRegistry().convention(true);
    // Use the "same" properties as the targets of the backend
    getRegistryUser().convention(getProject().provider(() -> getPropertyOrEnv("jib.to.auth.username", "CI_REGISTRY_USER")));
    getRegistryPassword().convention(getProject().provider(() -> getPropertyOrEnv("jib.to.auth.password", "CI_REGISTRY_PASSWORD")));
    dependsOn(getProject().getTasks().findByName("build"));
  }

  // Future work: Copy from jib task?
  protected String getPropertyOrEnv(String propertyName, String envName) {
    String property = System.getProperty(propertyName);
    if (property == null)
      return System.getenv(envName);
    return property;
  }
}
