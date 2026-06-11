package e2e;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
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
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
        .setName("Add Linked Access Root")
        .setExact(true)).click();
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

  @Test
  void validatesTargetSizeErrorOnLinkedAccessRoot(Page page) {
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
        .setName("Add Linked Access Root With Too Many Targets")
        .setExact(true)).click();
    waitForBackend();
    waitForAngular(page);

    navAndWait(page, "/ocl2java/errormessages/ErrorMsgs1Page");
    waitForAngular(page);

    locator(page, ".errorMsgs1Inv3", ComponentKind.GemButton).click();
    waitForAngular(page);

    assertThat(page.locator(".resultGemText")).hasText("0");
  }

  @Test
  void showsProjectBudgetConstraintErrorAtBothComparedInputs(Page page) {
    page.getByText("Add Project Budget").click();
    waitForBackend();

    Locator spentInput =
        locator(page, ".budgetEditComp .budgetSpentInput", ComponentKind.GemTextInput);
    Locator limitInput =
        locator(page, ".budgetEditComp .budgetLimitInput", ComponentKind.GemTextInput);

    assertThat(spentInput).hasValue("40");
    assertThat(limitInput).hasValue("100");

    spentInput.fill("120");
    waitForBackend();

    Pattern errorClass = Pattern.compile(".*\\bgem-input-error\\b.*");
    assertThat(spentInput).hasClass(errorClass);
    assertThat(limitInput).hasClass(errorClass);

    String shortError = "Project budget exceeded: spent 120, limit 100";

    spentInput.hover();
    assertVisibleText(page, shortError);

    limitInput.hover();
    assertVisibleText(page, shortError);
  }

  private void assertVisibleText(Page page, String text) {
    assertTrue(page.getByText(text).all().stream().anyMatch(Locator::isVisible));
  }
}
