package e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import jsweettest.test_infrastructure.AbstractJsweettestE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExampleE2ETest extends AbstractJsweettestE2ETest {

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/gui/test/Test0"); //go to page we want to test
    page.getByText("Reset Data").click();
    page.waitForLoadState(LoadState.NETWORKIDLE);
  }

  @Test
  void test0Static(Page page) {
    assertTrue(page.getByText("Show some static text").first().isVisible());
  }

  @Test
  void testNavigate0To1FromButton(Page page) {
    navAndWait(page, "/gui/test/Test0"); //go to page we want to test
    assertTrue(page.getByText("Page: Test0").first().isVisible(), "Failed to load Page 0");
    page.getByText("1 (static list)").click();
    waitRdy(page);
    assertTrue(waitInit(page, "Test1"), "Page Test 1 did not init");
    assertTrue(page.getByText("Page: Test1").first().isVisible(),
        "Failed to switch and load Page 1");
  }

  @Test
  void test1StaticList(Page page) {
    navAndWait(page, "/gui/test/Test1"); //go to page we want to test
    assertTrue(page.getByText("Got a C2").first().isVisible());
    int count = page.getByText("Got a C2").count();
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("AddC2")).click();

    assertThat(page.getByText("Got a C2"))
        .hasCount(count + 1);

  }

}
