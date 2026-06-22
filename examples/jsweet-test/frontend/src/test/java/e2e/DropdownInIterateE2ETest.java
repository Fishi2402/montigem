package e2e;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import jsweettest.test_infrastructure.AbstractJsweettestE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class DropdownInIterateE2ETest extends AbstractJsweettestE2ETest {

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/gui/test/DropdownInIterate");
    page.waitForLoadState(LoadState.NETWORKIDLE);
  }

  @Test
  void addedRowsInitializeDropdownOptions(Page page) {
    Locator dropdowns = page.locator("gem-dropdown-input");
    assertThat(dropdowns).hasCount(0);

    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add row")).click();
    waitRdy(page);
    assertThat(dropdowns).hasCount(1);
    assertThat(page.getByText("Placeholder")).hasCount(1);

    dropdowns.first().click();
    assertThat(page.locator(".gem-dropdown-options").getByText("o1")).isVisible();

    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add row")).click();
    waitRdy(page);
    assertThat(dropdowns).hasCount(2);
    assertThat(page.getByText("Placeholder")).hasCount(2);

    dropdowns.nth(1).click();
    assertThat(page.locator(".gem-dropdown-options").getByText("o1")).isVisible();
  }
}
