package webshop;

import org.junit.jupiter.api.Test;
import totaltest.TotalTestInstantiator;
import umlp.test_rte.ClientSetup;
import umlp.test_rte.monitor.StackEventQueue;
import umlp.test_rte.monitor.event.CallEvent;
import umlp.test_rte.monitor.event.ReturnVoidEvent;
import webshop.test_infrastructure.WebshopManagerImplMonitor;
import webshop.test_infrastructure.WebshopSQLDBTestSetup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TotalTest extends WebshopSQLDBTestSetup {

  public TotalTest() {
    super(ClientSetup.of(1));
  }

  @Override
  protected void initBackendManager() {
    var instance = new WebshopManagerImplMonitor();
    WebshopManager.setInstance(instance);
  }

  @Test
  void matchComplete() {
    var testInstances = new TotalTestInstantiator().instantiate();
    Order o = WebshopManager.orderBuilder().build().orElseThrow();
    Item item = testInstances.getItem();

    assertEquals(0, o.getTotal());

    StackEventQueue.clear();

    o.addItem(item);

    //here we would need to check every element of the queue in order -> we're not going to do this
    // for demo purposes
    //TODO link OCL conditions to expected events
    assertEquals(20, StackEventQueue.toQueue().size()); //currently 16 events since we're not monitoring managers yet
    assertEquals(new CallEvent(o, "addItem"), StackEventQueue.toQueue().getFirst());
    assertEquals(new ReturnVoidEvent(o,"addItem"), StackEventQueue.toQueue().getLast());

    assertEquals(6, o.getTotal());
  }

  @Test
  void matchVisible() {
    var testInstances = new TotalTestInstantiator().instantiate();
    Order o = WebshopManager.orderBuilder().build().orElseThrow();
    Item item = testInstances.getItem();

    assertEquals(0, o.getTotal());

    StackEventQueue.clear();

    o.addItem(item);

    //we filter the events first to only include those, which involved the visible variables (o in our case)
    var visibleObjects = List.<Object>of(o);

    var queue = StackEventQueue
        .toQueue()
        .stream()
        .filter(event -> visibleObjects.contains(event.object()))
        .collect(Collectors.toCollection(LinkedList::new));

    //now we perform a match complete on that queue
    //which we're also going to omit for demo purposes
    assertEquals(16, queue.size()); //8 calls in current implementation -> 16 events
    //TODO link OCL conditions to expected events
    assertEquals(new CallEvent(o, "addItem"), queue.getFirst());
    assertEquals(new ReturnVoidEvent(o, "addItem"), queue.getLast());

    assertEquals(6, o.getTotal());
  }

  @Test
  void matchFree() {
    var testInstances = new TotalTestInstantiator().instantiate();
    Order o = WebshopManager.orderBuilder().build().orElseThrow();
    Item item = testInstances.getItem();

    assertEquals(0, o.getTotal());

    StackEventQueue.clear();

    o.addItem(item);

    //TODO link OCL conditions to expected events
    //we can use a queue instead of a state machine
    var expectedEvents = new LinkedList<>(List.of(new CallEvent(o, "addItem"),
                                                  new ReturnVoidEvent(o, "addItem")));
    var eventTrace = new ArrayList<String>();

    for (var event : StackEventQueue.toQueue()) {
      if (event.equals(expectedEvents.peek())) {
        expectedEvents.pop();
        eventTrace.add("Expected: " + event);
      } else {
        eventTrace.add("Ignored: " + event);
      }
    }
    for (var expectedEvent : expectedEvents) {
      eventTrace.add("Missing: " + expectedEvent);
    }
    assertTrue(expectedEvents.isEmpty(),
               "Some events are missing: " + String.join("\n", eventTrace));

    assertEquals(6, o.getTotal());
  }
}
