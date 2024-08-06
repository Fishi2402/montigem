package e2e;

import com.microsoft.playwright.Page;
import jsweettest.test_infrastructure.AbstractJsweettestE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("SlowTest")
public class ExampleE2ETest extends AbstractJsweettestE2ETest {

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/gui/Start"); //go to page we want to test
  }

  @Test
  void testOverview(Page page) {
    assertTrue(page.getByText("Erzeuge B").first().isVisible());
  }
}
