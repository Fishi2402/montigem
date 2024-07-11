package e2e;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import domain.test_infrastructure.AbstractDomainE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static umlp.backendrte.test_infrastructure.playwright.ComponentKind.GemButton;
import static umlp.backendrte.test_infrastructure.playwright.ComponentKind.GemTextInput;

public class MultiPageE2ETest extends AbstractDomainE2ETest {

  Page page1, page2;

  @BeforeEach
  void setup(Browser browser) {
    //create the number of pages we want to test with
    page1 = browser.newPage();
    page2 = browser.newPage();
    init(page1);
    init(page2);
  }

  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER1] " + cm.text()));
    navAndWait(page, "/person/Overview"); //go to page we want to test
  }

  @Test
  void testSync() {
    var createPerson = page1.locator(".createPerson");
    locator(createPerson, ".name", GemTextInput).fill("Abby");
    locator(createPerson, ".bday", GemTextInput).fill("2024-05-07");
    locator(createPerson, ".street", GemTextInput).fill("Cherrystr.");
    locator(createPerson, ".house", GemTextInput).fill("2");

    locator(createPerson, ".createButton", GemButton).click();

    waitForBackend(); //give commands time to reach backend

    page2.reload();
    waitRdy(page2);

    assertThat(page2.getByText("Abby").first()).isVisible();
  }
}
