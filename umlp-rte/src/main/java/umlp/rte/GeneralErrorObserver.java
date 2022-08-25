package umlp.rte;

// RTE-P

public interface GeneralErrorObserver {
  default void notifyConnectionError(String msg){ }
  default void notifyServerError(String msg){ }
  // TODO: pass collection of gemIds with sync errors?
  default void notifySyncError(String msg){ }
}
