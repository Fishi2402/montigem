package demorec;

public class LocalPhoto extends LocalPhotoTOP {
  @Override
  public String getUrl() {
    return "umlp/api/images/"+this.getFilename();
  }
}
