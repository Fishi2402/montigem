package e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import jsweettest.JsweettestManager;
import jsweettest.test_infrastructure.AbstractJsweettestE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class DomainStreamAdapterTestE2ETest extends AbstractJsweettestE2ETest {

  private static final String RESULT_INPUT_SELECTOR = "#domainResultIdInput";
  private static final String DATA_INPUT_SELECTOR = "#domainInputTestFieldIdInput";

  private String marker;

  @BeforeEach
  void init(Page page) {
    marker = "domain-stream-" + System.nanoTime();
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
    navAndWait(page, "/gui/test/DomainStreamAdapterTest");
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
    assertThat(page.locator(RESULT_INPUT_SELECTOR)).hasValue(expectedExactValue);
  }

  private String name(String suffix) {
    return marker + ":" + suffix;
  }

  private String names(String... suffixes) {
    StringBuilder sb = new StringBuilder("[");
    for (String suffix : suffixes) {
      if (sb.length() > 1) {
        sb.append(", ");
      }
      sb.append(name(suffix));
    }
    return sb.append("]").toString();
  }

  @Test
  void testBasicDomainStreamOperations(Page page) {
    checkActionExact(page, "map", "map(): [22, 44, 66, 88]");
    checkActionExact(page, "filter", "filter(): " + names("beta", "delta"));
    checkActionExact(page, "mapPlusFilter",
        "filter()+map(): " + names("alpha", "beta", "gamma", "delta").toUpperCase());
  }

  @Test
  void testDomainStructuralOperations(Page page) {
    checkActionExact(page, "flatMap", "flatMap(): " + names("alpha", "beta", "gamma", "delta"));
    checkActionExact(page, "distinct", "distinct(): " + names("alpha", "beta", "gamma"));
  }

  @Test
  void testDomainSlicingAndSorting(Page page) {
    checkActionExact(page, "limit", "limit(3): " + names("alpha", "beta", "gamma"));
    checkActionExact(page, "skip", "skip(2): " + names("gamma", "delta"));
    checkActionExact(page, "sorted", "sorted(): " + names("beta", "gamma", "alpha", "delta"));
  }

  @Test
  void testDomainPeek(Page page) {
    checkActionExact(page, "peek",
        "peek(): " + name("alpha") + ";" + name("beta") + ";" + name("gamma") + ";" + name("delta") + ";");
  }

  @Test
  void testDomainTerminalOperations(Page page) {
    checkActionExact(page, "count", "count(>15): 3");
    checkActionExact(page, "reduce", "reduce(min): " + name("alpha"));
    checkActionExact(page, "match", "any>20: true, all<50: true");
    checkActionExact(page, "findFirst", "findFirst(>15): " + name("beta"));
    checkActionExact(page, "min", "min(): " + name("alpha"));
    checkActionExact(page, "max", "max(): " + name("delta"));
    checkActionExact(page, "collectJoining",
        "collect(joining): " + name("alpha") + " - " + name("beta") + " - " + name("gamma") + " - " + name("delta"));
    checkActionExact(page, "btnToArray", "toArray(): " + names("alpha", "beta", "gamma", "delta"));
    checkActionExact(page, "btnFindAny", "findAny(): " + name("delta"));
    checkActionExact(page, "forEach",
        "forEach(): " + name("alpha") + "!" + name("beta") + "!" + name("gamma") + "!" + name("delta") + "!");
    checkActionExact(page, "noneMatch", "noneMatch(>100): true");
  }

  @Test
  void testDomainCollectors(Page page) {
    checkActionExact(page, "collectToSet", "collect(toSet): " + names("alpha", "beta", "gamma"));
    checkActionExact(page, "collectCounting", "collect(counting): 4");
  }
}
