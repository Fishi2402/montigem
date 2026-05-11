package e2e;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import mydomain.test_infrastructure.AbstractMydomainE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import umlp.test_rte.playwright.ComponentKind;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ErrorMessagesE2ETest extends AbstractMydomainE2ETest {

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/gui/Start");
  }

  @Test
  void validatesAgeGemTextInputFromOclConstraint(Page page) {
    page.getByText("Add Person").click();
    waitForBackend();

    Locator ageInput = locator(page, ".gemTextInput1", ComponentKind.GemTextInput);
    assertThat(ageInput).hasValue("20");

    ageInput.fill("-1");
    waitForBackend();

    assertThat(ageInput).hasClass(Pattern.compile(".*\\bgem-input-error\\b.*"));

    ageInput.hover();
    assertThat(page.getByText("The age must be positive but is -1")).isVisible();
  }
}
