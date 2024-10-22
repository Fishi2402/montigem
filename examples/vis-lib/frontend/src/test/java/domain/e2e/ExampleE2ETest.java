package domain.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import domain.test_infrastructure.AbstractDomainE2ETest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import umlp.test_rte.playwright.FrontendServer4Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExampleE2ETest extends AbstractDomainE2ETest {

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/person/Overview"); //go to page we want to test
  }

  @Test
  void testOverview(Page page) {
    waitRdy(page);
    assertTrue(page.getByText("Alice").first().isVisible());
  }
}
