/* (c) https://github.com/MontiCore/monticore */
package e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import jsweettest.test_infrastructure.AbstractJsweettestE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static umlp.test_rte.playwright.ComponentKind.GemText;

public class TestCustomUpdateTest extends AbstractJsweettestE2ETest {

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/gui/test/TestCustomUpdate"); //go to page we want to test
    page.waitForLoadState(LoadState.NETWORKIDLE);
  }

  @Test
  void testChangeFieldWithoutUpdate(Page page) {
    assertEquals(1, page.locator(GemText.selector()).all().size());
    assertEquals("First, Second", page.locator(GemText.selector()).textContent().trim());

    page.getByText("changeFieldWithoutUpdate").click();

    assertEquals(1, page.locator(GemText.selector()).all().size());
    assertEquals("First, Second", page.locator(GemText.selector()).textContent().trim());
  }

  @Test
  void testChangeFieldWithUpdate(Page page) {
    assertEquals(1, page.locator(GemText.selector()).all().size());
    assertEquals("First, Second", page.locator(GemText.selector()).textContent().trim());

    page.getByText("changeFieldWithoutUpdate").click();
    page.getByText("changeFieldWithUpdate").click();

    assertEquals(1, page.locator(GemText.selector()).all().size());
    assertEquals("First, Second, Third, Fourth", page.locator(GemText.selector()).textContent().trim());
  }
}
