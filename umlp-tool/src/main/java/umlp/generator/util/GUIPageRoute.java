package umlp.generator.util;

public record GUIPageRoute(String pageName, String path, int routingIdCount) {
  
  public String path() {
    return path.replace('\\', '/');
  }
  
}
