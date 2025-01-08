package monitor;

import monitor.event.StackEvent;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class StackEventQueue {

  private static final Queue<StackEvent> events = new ConcurrentLinkedQueue<>();

  public static void report(StackEvent event) {
    events.add(event);
  }

  public static Optional<StackEvent> poll() {
    return Optional.ofNullable(events.poll());
  }

  public static void clear() {
    events.clear();
  }
}
