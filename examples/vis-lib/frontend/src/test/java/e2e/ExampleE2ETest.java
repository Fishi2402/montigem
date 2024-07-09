package e2e;

import com.microsoft.playwright.Page;
import domain.test_infrastructure.AbstractDomainE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExampleE2ETest extends AbstractDomainE2ETest {

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/person/Overview"); //go to page we want to test
  }

  @Test
  void testOverview(Page page) {
    assertTrue(page.getByText("Alice").first().isVisible());
  }
}
