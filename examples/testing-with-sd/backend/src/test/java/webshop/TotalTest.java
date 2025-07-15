package webshop;

import org.junit.jupiter.api.Test;
import umlp.backendrte.common.NotificationScope;
import umlp.test_rte.ClientSetup;
import umlp.test_rte.monitor.MethodMonitor;
import umlp.test_rte.monitor.StackEventProcessor;
import umlp.test_rte.monitor.model.ExpectedEvent;
import umlp.test_rte.monitor.model.MatchingStrategy;
import umlp.test_rte.monitor.model.action.ActivityBarEndAction;
import umlp.test_rte.monitor.model.action.MethodCallAction;
import umlp.test_rte.monitor.model.action.ReturnAction;
import umlp.test_rte.monitor.model.ocl.OCLCondition;
import umlp.test_rte.monitor.model.ocl.OCLVariableDeclaration;
import webshop.test_infrastructure.WebshopSQLDBTestSetup;

import java.util.Optional;

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

  Order o;
  Item item;
  int total1;
  int total2;

  Order createO() {
    return WebshopManager.orderBuilder().build().orElseThrow();
  }

  Item createItem() {
    return WebshopManager.itemBuilder().amount(2).perUnitCost(3).build().orElseThrow();
  }

  @SuppressWarnings({"SimplifiableAssertion"})
  @Test
  void testSequence() {
    //make sure the name of the processor variable is unique throughout the test
    var processor = new StackEventProcessor();
    //load objects and their matching strategies
    o = createO();
    processor.objectMatchingStrategies().put(o, MatchingStrategy.FREE);
    item = createItem();
    processor.objectMatchingStrategies().put(item, MatchingStrategy.FREE);

    //the pre-interaction section is straightforward
    total1 = o.getTotal();
    processor.objectMatchingStrategies().put(total1, MatchingStrategy.FREE); //always tell processor about new introduced objects

    assertTrue(total1 == 0, "total1 == 0"); //simple assertTrue calls for condition checking

    //set processor active
    MethodMonitor.setActiveStackEventProcessor(processor);

    processor.load(
        new ExpectedEvent(o, o, new MethodCallAction("addItem", Optional.of(new Object[]{item}), true), new OCLCondition(() -> o.getTotal() == 0, "o.getTotal() == 0")),
        new ExpectedEvent(o, o, new MethodCallAction("addItem", Optional.of(new Object[]{item, NotificationScope.ALL}), false)),
        new ExpectedEvent(o, o, new ReturnAction(), new OCLVariableDeclaration(() -> total2 = o.getTotal()), new OCLCondition(() -> total2 == 6, "total2 == 6")),
        new ExpectedEvent(o, o, new ReturnAction()),
        new ExpectedEvent(o, o, new ActivityBarEndAction())
                  );
    //execute trigger
    o.addItem(item);
    processor.assertAllExpectedEventsToBeProcessed();
  }
}
