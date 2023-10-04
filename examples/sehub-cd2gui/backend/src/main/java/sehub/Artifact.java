package sehub;

public class Artifact extends ArtifactTOP {

  @Override
  public String getType() {
    return (this.getClass().getName());
  }
}
