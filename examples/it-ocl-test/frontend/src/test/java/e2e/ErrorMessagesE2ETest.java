package e2e;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import mydomain.test_infrastructure.AbstractMydomainE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import umlp.test_rte.playwright.ComponentKind;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ErrorMessagesE2ETest extends AbstractMydomainE2ETest {

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/gui/Start");
  }

  @Test
  void validatesAgeGemTextInputFromErrorMessageSubjectOclConstraint(Page page) {
    page.getByText("Add Error Message Subject").click();
    waitForBackend();

    Locator ageInput = locator(page, ".ageInput", ComponentKind.GemTextInput);
    assertThat(ageInput).hasValue("20");

    ageInput.fill("-1");
    waitForBackend();

    assertThat(ageInput).hasClass(Pattern.compile(".*\\bgem-input-error\\b.*"));

    ageInput.hover();
    assertTrue(page.getByText("Age not correct: -1").all().stream().anyMatch(Locator::isVisible));
  }

  @Test
  void validatesNestedGemTextInputWithLinkedObjectErrors(Page page) {
    page.getByText("Add Linked Access Root").click();
    waitForBackend();

    Locator checkedValueInput =
        locator(page, ".linkEditComp .checkedValueInput", ComponentKind.GemTextInput);
    assertThat(checkedValueInput).hasValue("101");

    checkedValueInput.fill("-1");
    waitForBackend();

    assertThat(checkedValueInput).hasClass(Pattern.compile(".*\\bgem-input-error\\b.*"));

    checkedValueInput.hover();
    assertVisibleText(page, "Linked checked value not correct: -1");
    assertVisibleText(page, "Linked target association invalid: linked access root");
  }

  private void assertVisibleText(Page page, String text) {
    assertTrue(page.getByText(text).all().stream().anyMatch(Locator::isVisible));
  }
}
