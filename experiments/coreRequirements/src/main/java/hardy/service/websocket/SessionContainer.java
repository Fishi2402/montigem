/* (c) https://github.com/MontiCore/monticore */

package hardy.service.websocket;

import javax.websocket.Session;
import java.util.Objects;
import java.util.Optional;

public class SessionContainer {
  private String userHash;
  private Session session;
  private String messageType;

  public SessionContainer(String userHash, Session session) {
    this.userHash = userHash;
    this.session = session;
    this.messageType = MessageType.ALL.toString();
  }

  public SessionContainer(String userHash, Session session, String messageType) {
    this.userHash = userHash;
    this.session = session;
    this.messageType = messageType;
  }

  public String getUserHash() {
    return userHash;
  }

  public void setUserHash(String userHash) {
    this.userHash = userHash;
  }

  public Session getSession() {
    return session;
  }

  public void setSession(Session session) {
    this.session = session;
  }

  public String getMessageType() {
    return messageType;
  }

  public void setMessageType(String messageType) {
    this.messageType = messageType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SessionContainer that = (SessionContainer) o;
    return userHash.equals(that.userHash) &&
      session.equals(that.session);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userHash, session);
  }

  public boolean equalsSession(Session s) {
    if (s == null || this.session == null) {
      return false;
    }
    return session.equals(s);
  }

  public String handleIncomingMessage(String msg) {
    return MessageHandlerManager.getInstance().handleIncomingMessage(messageType, msg, Optional.of(this)).orElse("no answer");
  }

}

