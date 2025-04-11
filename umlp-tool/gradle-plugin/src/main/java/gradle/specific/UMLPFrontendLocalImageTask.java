package gradle.specific;

public abstract class UMLPFrontendLocalImageTask extends UMLPFrontendImageTask {

  public UMLPFrontendLocalImageTask() {
    getFullImageName().convention(getProject().provider(() ->
        (getProject().getParent() != null ? getProject().getParent().getName() : getProject().getName()) + "/frontend"
    ));
    getUseRegistry().convention(false);
    dependsOn(getProject().getTasks().findByName("build"));
  }
}
