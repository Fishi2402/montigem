package hardy.service.websocket;

public enum MessageType {
  ALL;

  @Override
  public String toString() {
    return this.name();
  }
}
