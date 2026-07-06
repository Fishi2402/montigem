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
    getRegistryUser().convention(getProject().provider(() -> System.getProperty("jib.to.auth.username")));
    getRegistryPassword().convention(getProject().provider(() -> System.getProperty("jib.to.auth.password")));
    dependsOn(getProject().getTasks().findByName("build"));
  }
}
