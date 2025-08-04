package de.montigem.gradle.specific;

public abstract class FrontendLocalImageTask extends FrontendImageTask {

  public FrontendLocalImageTask() {
    getFullImageName().convention(getProject().provider(() ->
        (getProject().getParent() != null ? getProject().getParent().getName() : getProject().getName()) + "/frontend"
    ));
    getUseRegistry().convention(false);
    dependsOn(getProject().getTasks().findByName("build"));
  }
}
