package webshop;

import org.junit.jupiter.api.Test;
import umlp.backendrte.common.NotificationScope;
import umlp.test_rte.ClientSetup;
import umlp.test_rte.monitor.InteractionProcessor;
import umlp.test_rte.monitor.MethodMonitor;
import umlp.test_rte.monitor.model.MatchingStrategy;
import umlp.test_rte.monitor.model.ocl.OCLCondition;
import umlp.test_rte.monitor.model.ocl.OCLVariableDeclaration;
import webshop.test_infrastructure.WebshopSQLDBTestSetup;

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

  @Override
  public void _cleanup() throws Exception {
    MethodMonitor.removeActiveProcessor();
    super._cleanup();
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
    var processor = new InteractionProcessor(MatchingStrategy.FREE);
    //load objects and their matching strategies
    o = createO();
    processor.declare(o, MatchingStrategy.FREE);
    item = createItem();
    processor.declare(item, MatchingStrategy.FREE);

    //the pre-interaction section is straightforward
    total1 = o.getTotal();
    processor.declare(total1,
                      MatchingStrategy.FREE); //always tell processor about new introduced objects

    assertTrue(total1 == 0, "total1 == 0"); //simple assertTrue calls for condition checking

    //set processor active
    MethodMonitor.setActiveProcessor(processor);

    processor
        .expectMethodCall(o, o, "addItem")
        .withParameters(item)
        .withActivityBar()
        .withOCL(OCLCondition.from(() -> o.getTotal() == 0, "o.getTotal() == 0"));
    processor.expectMethodCall(o, o, "addItem").withParameters(item, NotificationScope.ALL);
    processor.expectReturn(o, o).withOCL(OCLVariableDeclaration.fromExecutor(() -> {
      total2 = o.getTotal();
      processor.declare(total2, MatchingStrategy.FREE);
    })).withOCL(OCLCondition.from(() -> total2 == 6, "total2 == 6"));
    processor.expectReturn(o, o);
    processor.expectActivityEnd();

    //execute trigger
    try {
      o.addItem(item);
    } catch (Throwable ignore) {
      //catch all errors thrown by the trigger call
    }
    processor.assertExpectedInteractionsHaveOccurred();
  }
}
