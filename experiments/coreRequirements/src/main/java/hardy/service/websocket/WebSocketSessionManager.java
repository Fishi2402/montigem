/* (c) https://github.com/MontiCore/monticore */

package hardy.service.websocket;

import de.se_rwth.commons.logging.Log;
import umlp.command.Command;
import umlp.command.CommandObjectMapper;
import umlp.command.CommandResult;

import javax.websocket.Session;
import java.util.*;
import java.util.stream.Collectors;

/**
 * storage for all websocket sessions
 */
public class WebSocketSessionManager {

  public static final int MAX_TRIES = 10;

  private static WebSocketSessionManager INSTANCE;
  private final ThreadLocal<Session> currentSession = new ThreadLocal<>();

  // Collection of all connected clients
  private final Map<String, List<SessionContainer>> sessionList;

  private final Map<Long, String> userHashs = new HashMap<>();

  public WebSocketSessionManager() {
    sessionList = new HashMap<>();
  }

  public static WebSocketSessionManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new WebSocketSessionManager();
    } else {
      INSTANCE.removeClosedSessions();
    }
    return INSTANCE;
  }

  private void removeClosedSessions() {
    for (String resource : this.sessionList.keySet()) {
      List<SessionContainer> sessionContainers = INSTANCE.sessionList.get(resource);
      List<SessionContainer> toClose = sessionContainers.stream()
          .filter(s -> !s.getSession().isOpen()).collect(Collectors.toList());
      sessionContainers.removeAll(toClose);
    }
  }

  public Session getCurrentSession() {
    return currentSession.get();
  }

  /**
   * add a session to the list
   *
   * @param session  the session to store
   * @param resource the resource of the currenlty active user
   * @param userHash identify the user by its hash
   */
  public void addSession(Session session, String resource, long userId, String userHash, String messageType) {
    sessionList.putIfAbsent(resource, new ArrayList<>());
    sessionList.get(resource).add(new SessionContainer(userHash, session, messageType));
    Log.debug("New websocket session (" + session.getId() + ") in " + resource + " has been added", getClass().getName());

    this.userHashs.put(userId, userHash);
  }

  public void removeSession(Session session) {
    for (String resource : sessionList.keySet()) {
      removeSession(session, resource);
    }
  }

  public void removeSession(Session session, String resource) {
    List<SessionContainer> sessions = sessionList.get(resource);
    if (sessions != null) {
      ListIterator<SessionContainer> sessionsIterator = sessions.listIterator();
      while (sessionsIterator.hasNext()) {
        SessionContainer next = sessionsIterator.next();
        if (next.equalsSession(session)) {
          sessionsIterator.remove();
          break;
        }
      }
//      sessions.removeIf(s -> s.equalsSession(session));
      Log.debug("Websocket session (" + session.getId() + ") from " + resource + " has been removed", getClass().getName());
    }
  }

  public void removeSessionsForUser(String resource, String userHash) {
    List<SessionContainer> sessions = sessionList.get(resource);
    if (sessions != null) {
      Log.debug("Remove all sessions for user with hash " + userHash + " in " + resource, getClass().getName());

      ListIterator<SessionContainer> iter = sessions.listIterator();
      while (iter.hasNext()) {
        SessionContainer sc = iter.next();
        if (sc.getUserHash().equals(userHash)) {
          Log.debug("Websocket session (" + sc.getSession().getId() + ") from " + resource + " has been removed", getClass().getName());
          iter.remove();
        }
      }
    }
  }

  public Optional<SessionContainer> findSession(Session session) {
    for (String resource : sessionList.keySet()) {
      Optional<SessionContainer> s = findSession(session, resource);
      if (s.isPresent()) {
        return s;
      }
    }
    return Optional.empty();
  }

  public Optional<SessionContainer> findSession(Session session, String resource) {
    List<SessionContainer> sessions = sessionList.get(resource);
    if (sessions != null) {
      return sessions.stream().filter(s -> s.getSession() == session).findAny();
    }

    return Optional.empty();
  }

  public List<SessionContainer> getSessions(String resource, String messageType, String usage, long userId) {
    List<SessionContainer> sessionList = this.sessionList.getOrDefault(resource, Collections.emptyList());
    if (sessionList.isEmpty()) {
      return sessionList;
    }

    if (userId >= 0) {
      sessionList = sessionList.stream().filter(s -> s.getUserHash().equals(userHashs.get(userId)))
          .collect(Collectors.toList());
    }
    if (messageType != null) {
      sessionList = sessionList.stream().filter(s -> messageType.equals(s.getMessageType()))
          .collect(Collectors.toList());
    }
    if (usage != null && !usage.isEmpty()) {
      sessionList = sessionList.stream().filter(s ->
          {
            UsageList usageList;
            try {
              usageList = (UsageList) s.getSession().getUserProperties().getOrDefault("usage", Collections.singletonList("All"));
            }
            catch (ClassCastException ex) {
              usageList = UsageList.all();
            }
            return usageList.contains("All") || usageList.contains(usage);
          }
      ).collect(Collectors.toList());
    }
    return sessionList;
  }


  /*************** receiving ***************/
  public Optional<String> handleIncomingMessage(Session session, String resource, String msg) {
    currentSession.set(session);
    Optional<SessionContainer> s = findSession(session, resource);
    return s.map(o -> o.handleIncomingMessage(msg));
  }


  /*************** sending ***************/
  public void sendResult(SessionContainer sessionContainer, CommandResult<?> result) {
    sendMessage(sessionContainer, CommandObjectMapper.serializeResponse(result));
  }

  public void sendCommand(SessionContainer sessionContainer, Command<?> command) {
    sendMessage(sessionContainer, CommandObjectMapper.serializeCommand(command));
  }

  public void sendMessage(SessionContainer sessionContainer, String message) {
    IllegalStateException lastException = null;
    int tries = 0;
    while (tries < MAX_TRIES) {
      try {
        sessionContainer.getSession().getAsyncRemote().sendText(message);
        return;
      } catch (IllegalStateException e) {
        lastException = e;
        tries++;
      }
    }

    throw lastException;
  }

  public List<SessionContainer> getSessions(String resource) {
    return getSessions(resource, null, null);
  }

  public List<SessionContainer> getSessions(String resource, String messageType) {
    return getSessions(resource, messageType, null, -1);
  }

  public List<SessionContainer> getSessions(String resource, String messageType, String usage) {
    return getSessions(resource, messageType, usage, -1);
  }

  public List<SessionContainer> getSessionsForUser(String resource, long userId) {
    return getSessionsForUser(resource, null, userId);
  }

  public List<SessionContainer> getSessionsForUser(String resource, String messageType, long userId) {
    return getSessionsForUser(resource, messageType, null, userId);
  }

  public List<SessionContainer> getSessionsForUser(String resource, String messageType, String usage, long userId) {
    return getSessions(resource, messageType, usage, userId);
  }

  public void sendToSessions(String resource, String msg) {
    getSessions(resource).forEach(s -> s.getSession().getAsyncRemote().sendText(msg));
  }

  public void sendToSessions(String resource, String messageType, String msg) {
    getSessions(resource, messageType).forEach(s -> s.getSession().getAsyncRemote().sendText(msg));
  }

  public void sendToSessions(String resource, String messageType, String usage, String msg) {
    getSessions(resource, messageType, usage).forEach(s -> s.getSession().getAsyncRemote().sendText(msg));
  }

  public void sendToSessions(String resource, String messageType, String usage, String msg, long userId) {
    getSessions(resource, messageType, usage, userId).forEach(s -> s.getSession().getAsyncRemote().sendText(msg));
  }

  public void sendToUserSessions(String resource, String msg, long userId) {
    getSessionsForUser(resource, userId).forEach(s -> s.getSession().getAsyncRemote().sendText(msg));
  }

  public void sendToUserSessions(String resource, String messageType, String msg, long userId) {
    getSessionsForUser(resource, messageType, userId).forEach(s -> s.getSession().getAsyncRemote().sendText(msg));
  }

  public void sendToUserSessions(String resource, String messageType, String usage, String msg, long userId) {
    getSessionsForUser(resource, messageType, usage, userId).forEach(s -> s.getSession().getAsyncRemote().sendText(msg));
  }
}
