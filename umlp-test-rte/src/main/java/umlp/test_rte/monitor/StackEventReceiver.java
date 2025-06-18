package umlp.test_rte.monitor;

import umlp.test_rte.monitor.event.StackEvent;

/**
 * Receives and processes {@link StackEvent}s
 */
public final class StackEventReceiver {
  private static final StackEventReceiver INSTANCE = new StackEventReceiver();

  private StackEventReceiver() {}

  /* TODO this is intended to replace the StackEventQueue
  *   the idea is for the instance to be loaded with expected events and then check them off as they come in
  *  */
}
