package gradle.specific;

import java.util.Objects;

public abstract class UMLPFrontendCIImageTask extends UMLPFrontendImageTask {

  public UMLPFrontendCIImageTask() {
    getFullImageName().convention(getProject().provider(() ->
        Objects.requireNonNull(System.getenv("CI_REGISTRY_IMAGE"), "CI_REGISTRY_IMAGE not set")
            + "/frontend:latest"
    ));
    getUseRegistry().convention(true);
    getRegistryUser().convention(System.getenv("CI_REGISTRY_USER"));
    getRegistryPassword().convention(System.getenv("CI_REGISTRY_PASSWORD"));
    dependsOn(getProject().getTasks().findByName("build"));
  }
}
