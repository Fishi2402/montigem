package e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import jsweettest.JsweettestManager;
import jsweettest.test_infrastructure.AbstractJsweettestE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class DomainGemListAdapterTestE2ETest extends AbstractJsweettestE2ETest {

  private static final String RESULT_INPUT_SELECTOR = "#domainGemListResultIdInput";
  private static final String DATA_INPUT_SELECTOR = "#domainGemListInputTestFieldIdInput";

  private String marker;

  @BeforeEach
  void init(Page page) {
    marker = "domain-gemlist-" + System.nanoTime();
    JsweettestManager.c2Builder()
        .string(marker + ":alpha")
        .int1(11)
        .int2(30)
        .build().get();
    JsweettestManager.c2Builder()
        .string(marker + ":beta")
        .int1(22)
        .int2(10)
        .build().get();
    JsweettestManager.c2Builder()
        .string(marker + ":gamma")
        .int1(33)
        .int2(20)
        .build().get();
    JsweettestManager.c2Builder()
        .string(marker + ":delta")
        .int1(44)
        .int2(40)
        .build().get();

    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/gui/test/DomainGemListAdapterTest");
    page.waitForLoadState(LoadState.NETWORKIDLE);
    setInput(page, marker);
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

  private String name(String suffix) {
    return marker + ":" + suffix;
  }

  private String names(String... suffixes) {
    StringBuilder sb = new StringBuilder("");
    for (String suffix : suffixes) {
      if (sb.length() > 1) {
        sb.append(", ");
      }
      sb.append(name(suffix));
    }
    return sb.append("").toString();
  }

  @Test
  void testBasicGemListOperations(Page page) {
    checkActionExact(page, "add", "add(): size=2, " + names("alpha", "beta"));
    checkActionExact(page, "get", "get(2): " + name("gamma"));
  }

  @Test
  void testGemListContainsAndIndex(Page page) {
    checkActionExact(page, "contains", "contains(alpha)=true, contains(gamma)=false");
    checkActionExact(page, "indexOf", "indexOf(gamma): 2");
    // TODO lastIndexOf works in general but cannot be tested since GemList cannot contain the same 0bject twice
    checkActionExact(page, "lastIndexOf", "lastIndexOf(alpha): 0");
  }

  @Test
  void testGemListSetAndRemove(Page page) {
    checkActionExact(page, "set",
        "set(1,delta): old=" + name("beta") + ", list=" + names("alpha", "delta", "gamma"));
    checkActionExact(page, "removeIdx",
        "remove(1): removed=" + name("beta") + ", list=" + names("alpha", "gamma", "delta"));
    checkActionExact(page, "removeObj",
        "remove(gamma): removed=true, list=" + names("alpha", "beta", "delta"));
  }

  @Test
  void testGemListInsertAndClear(Page page) {
    checkActionExact(page, "addAtIndex", "add(1,beta): " + names("alpha", "beta", "gamma"));
    checkActionExact(page, "isEmptyClear", "isEmptyClear: before=true, afterAdd=false, afterClear=true");
  }
}
