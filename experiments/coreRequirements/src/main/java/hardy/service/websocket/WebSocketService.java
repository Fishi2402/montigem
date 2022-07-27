/* (c) https://github.com/MontiCore/monticore */

package hardy.service.websocket;

import org.springframework.stereotype.Controller;

/**
 * example usage
 * <pre>{@code
 *  ws://localhost:8080/macoco-be/websocket/<JWT>/CommandManager
 * }</pre>
 */
@Controller
@ServerEndpoint(value = "/websocket/{token}/{messageType}/{usage}")
public class WebSocketService { // TODO SVa: split or move to app

  private final static int SESSION_MAX_TIMEOUT = 3_600_000; // 1 hour

  // if the session should be closed on any error
  public static final boolean SHOULD_CLOSE_ON_ERROR = false;

  /**
   * gets called when a new connection is opened
   *
   * @param session
   * @param token
   * @param messageType
   */
  @OnOpen
  public void onOpen(Session session, @PathParam("token") String token,
      @PathParam("messageType") String messageType, @PathParam("usage") String usage) {
    JWToken jwt = JWToken.fromString(token, ShiroJWTFilter.getSecret());

    boolean hasError = false;
    try {
      // try to validate given token
      jwt.validate(ShiroJWTFilter.getSecret());
    }
    catch (ClaimJwtException | SignatureException e) {
      // any error in the jwt (wrong signature, ...)
      Log.debug("MAB0x32F6: Claim not valid", e, getClass().getName());
      hasError = true;
    }

    if (hasError) {
      try {
        if (session.isOpen()) {
          // close the session immediately on wrong token
          session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "wrong token"));
        }
      }
      catch (IOException e) {
        Log.debug("MAB0x32F7: Couldn't close session: " + e, getClass().getName());
        return;
      }
    }

    // add the jwt to the session
    session.getUserProperties().put("jwt", jwt);
    session.getUserProperties().put("usage", UsageList.valueOf(usage));

    session.getUserProperties().put("context", ThreadContext.getResources());
    session.setMaxIdleTimeout(SESSION_MAX_TIMEOUT);

    if (!setUserSession(session)) {
      try {
        if (session.isOpen()) {
          session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "cannot create user session"));
        }
        return;
      }
      catch (IOException e) {
        Log.debug("MAB0x32F7: Couldn't close session: " + e, getClass().getName());
        return;
      }
    }

    // get userId
    long userId = -1;
    Optional<MacocoUser> userOpt = daoLib.getMacocoUserDAO()
        .findByUsername(getJWT(session).getUsername(), getResource(session));
    if (!userOpt.isPresent()) {
      Log.error("MAB0x32F8: Couldn't find user for session");
    }
    else {
      userId = userOpt.get().getId();
    }

    // add the session to the session list
    WebSocketSessionManager.getInstance()
        .addSession(session, getResource(session), userId, getUserHash(session), messageType);
    logoutSubject();
  }

  /**
   * gets called when either side is closing the session
   *
   * @param session
   */
  @OnClose
  public void onClose(Session session) {
    WebSocketSessionManager.getInstance().removeSession(session, getResource(session));
  }

  /**
   * gets called if any error occured
   *
   * @param session
   * @param throwable
   */
  @OnError
  public void onError(Session session, Throwable throwable) {
    if (isJWTError(throwable)) {
      try {
        // close the session immediately on wrong token
        Log.info("Error in session " + session.getId(), getClass().getName());
        if (session.isOpen()) {
          session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "wrong token"));
        }
      }
      catch (IOException e) {
        Log.debug("MAB0x32F7: Couldn't close session: " + e, getClass().getName());
        return;
      }
    }
    else {
      String resource = getResource(session);
      if (getResource(session) == null) {
        resource = "Resource not yet defined";
      }

      Log.info("Error in session " + session.getId() + " of " + resource + ": " + throwable.toString(), getClass().getName());

    }

    if (isClosedByClient(throwable)) {
      WebSocketSessionManager.getInstance().removeSession(session, getResource(session));
    }
    else {
      if (SHOULD_CLOSE_ON_ERROR) {
        WebSocketSessionManager.getInstance().removeSession(session, getResource(session));
        try {
          if (session.isOpen()) {
            session.close(new CloseReason(
                CloseReason.CloseCodes.CLOSED_ABNORMALLY,
                "An exception occured: " + throwable)
            );
          }
        }
        catch (IOException e) {
          Log.warn("Couldn't close session " + session.getId() + ": " + e);
        }
      }
    }
  }

  /**
   * check if there is an error with the jwt
   *
   * @param throwable
   * @return
   */
  private boolean isJWTError(Throwable throwable) {
    return throwable instanceof SignatureException || throwable instanceof ClaimJwtException
        || throwable instanceof NullPointerException;
  }

  /**
   * Check if the client closed the session,
   * based on a given Throwable
   * for example destroyed the session
   *
   * @param throwable
   * @return
   */
  private boolean isClosedByClient(Throwable throwable) {
    int count = 0;
    Throwable root = throwable;
    // find the root cause (max 20 recursive calls)
    while (root.getCause() != null && count < 20 && root.getCause() != root) {
      root = root.getCause();
      count++;
    }
    return root instanceof EOFException;
  }

  /**
   * gets called when a message is received
   *
   * @param msg
   * @param session contains the previous created session
   */
  @OnMessage
  public void onMessage(String msg, Session session) {
    Log.trace("Incoming message in session " + session.getId() + ": " + msg, getClass().getName());

    // set current session for thread
    if (!setUserSession(session)) {
      try {
        if (session.isOpen()) {
          session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "cannot create user session"));
        }
        return;
      }
      catch (IOException ex) {
        Log.warn("Couldn't close session " + session.getId() + ": " + ex);
      }
    }

    // call the provided message handler
    WebSocketSessionManager.getInstance()
        .handleIncomingMessage(session, getResource(session), msg)
        .ifPresent(result -> session.getAsyncRemote().sendText(result));
    logoutSubject();
  }

  /**
   * create the environment for a authenticated user
   *
   * @param session
   */
  private boolean setUserSession(Session session) {
    try {
      JWToken jwt = getJWT(session);
      setContext(session);
      SecurityHelper.createSubjectAndLogin(jwt.getUsername(), jwt.getResource(), daoLib);
    }
    catch (UnavailableSecurityManagerException e) {
      Log.debug("MAB0x32F8: Could not initialize user: " + e, getClass().getName());
      try {
        if (session.isOpen()) {
          session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY,
              "An exception occured: " + e.toString()));
        }
      }
      catch (IOException ex) {
        Log.warn("Couldn't close session " + session.getId() + ": " + e.toString());
      }
      return false;
    }
    catch (UnknownSessionException e) {
      Log.debug("MAB0x32F8: Could not initialize user: " + e, getClass().getName());
      try {
        if (session.isOpen()) {
          session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY,
              "An exception occured (user session not found): " + e.toString()));
        }
      }
      catch (IOException ex) {
        Log.warn("Couldn't close session " + session.getId() + ": " + e.toString());
      }
      return false;
    }
    catch (NamingException e) {
      Log.debug("MAB0x32F8: Could not initialize user: " + e, getClass().getName());
      try {
        if (session.isOpen()) {
          session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY,
              "An exception occured: " + e.toString()));
        }
      }
      catch (IOException ex) {
        Log.warn("Couldn't close session " + session.getId() + ": " + e.toString());
      }
      return false;
    }
    return true;
  }

  private void logoutSubject() {
    SecurityUtils.getSubject().logout();
  }

  public static void setContext(Session session) {
    //noinspection unchecked
    Map<Object, Object> context = (Map<Object, Object>) session.getUserProperties().get("context");
    ThreadContext.setResources(context);
  }

  /**
   * get resource/instance for given session
   *
   * @param session
   * @return
   */
  private String getResource(Session session) {
    return getJWT(session) != null ? getJWT(session).getResource() : null;
  }

  /**
   * @param session
   * @return
   */
  private String getUsername(Session session) {
    return getResource(session) + getJWT(session).getUsername();
  }

  /**
   * get the hash of the username, e.g. for logging
   *
   * @param session
   * @return
   */
  private String getUserHash(Session session) {
    return new Sha1Hash(getUsername(session)).toString();
  }
}
