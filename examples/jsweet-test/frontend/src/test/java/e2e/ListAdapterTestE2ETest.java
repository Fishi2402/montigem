package e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import jsweettest.test_infrastructure.AbstractJsweettestE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ListAdapterTestE2ETest extends AbstractJsweettestE2ETest {

  private static final String RESULT_INPUT_SELECTOR = "#listResultIdInput";
  private static final String DATA_INPUT_SELECTOR = "#listInputTestFieldIdInput";

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/gui/test/ListAdapterTest");
    page.waitForLoadState(LoadState.NETWORKIDLE);
    setInput(page, "alpha, beta, gamma, delta");
  }

  private void setInput(Page page, String value) {
    waitRdy(page);
    assertThat(page.locator(DATA_INPUT_SELECTOR)).isVisible();
    page.fill(DATA_INPUT_SELECTOR, value);
  }

  private void checkActionExact(Page page, String buttonClass, String expectedExactValue) {
    page.locator("." + buttonClass).click();
    waitRdy(page);
    assertThat(page.locator(RESULT_INPUT_SELECTOR)).hasValue(expectedExactValue);
  }

  @Test
  void testBasicListOperations(Page page) {
    checkActionExact(page, "add", "add(): size=2, alpha, beta");
    checkActionExact(page, "get", "get(2): gamma");
  }

  @Test
  void testListContainsAndIndex(Page page) {
    checkActionExact(page, "contains", "contains(alpha)=true, contains(zeta)=false");
    checkActionExact(page, "indexOf", "indexOf(gamma): 2");
    checkActionExact(page, "lastIndexOf", "lastIndexOf(alpha): 4");
  }

  @Test
  void testListSetAndRemove(Page page) {
    checkActionExact(page, "set", "set(1,delta): old=beta, list=alpha, delta, gamma, delta");
    checkActionExact(page, "removeIdx", "remove(1): removed=beta, list=alpha, gamma, delta");
    checkActionExact(page, "removeObj", "remove(gamma): removed=true, list=alpha, beta, delta");
  }

  @Test
  void testListInsertAndClear(Page page) {
    checkActionExact(page, "addAtIndex", "add(1,inserted): alpha, inserted, beta, gamma, delta");
    checkActionExact(page, "isEmptyClear", "isEmptyClear: before=true, afterAdd=false, afterClear=true");
  }

  @Test
  void testListSort(Page page) {
    checkActionExact(page, "sort", "sort(desc): gamma, delta, beta, alpha");
  }
}
