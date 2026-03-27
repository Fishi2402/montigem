package demorec;

public class ExternalPhoto extends ExternalPhotoTOP {
  @Override
  public String getUrl() {
    return this.getExternalUrl();
  }
}
