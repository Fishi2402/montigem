package e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import jsweettest.A;
import jsweettest.B;
import jsweettest.D;
import jsweettest.JsweettestManager;
import jsweettest.test_infrastructure.AbstractJsweettestE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NestedListUpdateTest extends AbstractJsweettestE2ETest {

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/gui/test/TestNestedList"); //go to page we want to test
    page.waitForLoadState(LoadState.NETWORKIDLE);
  }

  @Test
  void testNestedUpdateIsPropagated(Page page) throws InterruptedException {
    String identifier = "New";
    assertEquals(0, page.getByText(identifier).count());

    B b1 = JsweettestManager.bBuilder().value("b1").build().get();
    JsweettestManager.aBuilder().value("a1").bAdd(b1).build().get();
    D d1 = JsweettestManager.dBuilder().value(identifier).build().get();
    b1.addD(d1);

    page.waitForLoadState(LoadState.NETWORKIDLE);
    Thread.sleep(1000L);

    assertEquals(1, page.getByText(identifier).count());
    b1.removeD(d1);

    page.waitForLoadState(LoadState.NETWORKIDLE);
    Thread.sleep(1000L);
    assertEquals(0, page.getByText(identifier).count());
  }

  @Test
  void testTopLevelUpdateIsPropagated(Page page) throws InterruptedException {
    String identifier = "New";
    assertEquals(0, page.getByText(identifier).count());

    A a1 = JsweettestManager.aBuilder().value(identifier).build().get();

    page.waitForLoadState(LoadState.NETWORKIDLE);
    Thread.sleep(1000L);
    assertEquals(1, page.getByText(identifier).count());

    JsweettestManager.deleteA(a1.getGemId());

    page.waitForLoadState(LoadState.NETWORKIDLE);
    Thread.sleep(1000L);
    assertEquals(0, page.getByText(identifier).count());
  }
}
