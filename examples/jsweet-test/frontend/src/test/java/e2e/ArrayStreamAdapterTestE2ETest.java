package e2e;

import com.microsoft.playwright.ConsoleMessage;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import jsweettest.test_infrastructure.AbstractJsweettestE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArrayStreamAdapterTestE2ETest extends AbstractJsweettestE2ETest {

  private static final String RESULT_INPUT_SELECTOR = "#resultIdInput";
  private static final String DATA_INPUT_SELECTOR = "#inputTestFieldIdInput";

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/gui/test/ArrayStreamAdapterTest");
    page.waitForLoadState(LoadState.NETWORKIDLE);
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


  @Test
  void testBasicStreamOperations(Page page) {
    setInput(page, "10, 20, 30");
    checkActionExact(page, "map", "map(): [20,40,60]");

    setInput(page, "11, 22, 33, 44");
    checkActionExact(page, "filter", "filter(): [22,44]");

    setInput(page, "hi, hello, world, yo");
    checkActionExact(page, "mapPlusFilter", "filter()+map(): [HELLO,WORLD]");
  }

  @Test
  void testStructuralOperations(Page page) {
    setInput(page, "x,y;z");
    checkActionExact(page, "flatMap", "flatMap(): [x,y,z]");

    setInput(page, "5, 5, 6, 7, 7");
    checkActionExact(page, "distinct", "distinct(): [5,6,7]");
  }

  @Test
  void testSlicingAndSorting(Page page) {
    setInput(page, "10, 20, 30, 40, 50");
    checkActionExact(page, "limit", "limit(3): [10,20,30]");

    setInput(page, "10, 20, 30, 40, 50");
    checkActionExact(page, "skip", "skip(2): [30,40,50]");

    setInput(page, "9, 1, 5");
    checkActionExact(page, "sorted", "sorted(): [1,5,9]");
  }

  @Test
  void testPeek(Page page) {
    setInput(page, "8, 9");
    checkActionExact(page, "peek", "peek(): 8;9;");
  }

  @Test
  void testNewTerminalOperations(Page page) {
    setInput(page, "1, 2, 10, 20");
    checkActionExact(page, "count", "count(>5): 2");

    setInput(page, "1, 2, 3, 4");
    checkActionExact(page, "reduce", "reduce(sum): 10");

    setInput(page, "1, 2, 3");
    checkActionExact(page, "match", "any>2: true, all<5: true");

    setInput(page, "1, 2, 3, 99, 100, 101");
    checkActionExact(page, "findFirst", "findFirst(>5): 99");

    setInput(page, "10, 5, 20, 3");
    checkActionExact(page, "min", "min(): 3");

    setInput(page, "10, 5, 20, 3");
    checkActionExact(page, "max", "max(): 20");

    setInput(page, "Apple, Banana, Cherry");
    checkActionExact(page, "collectJoining", "collect(joining): Apple - Banana - Cherry");

    setInput(page, "1, 2, 10");
    checkActionExact(page, "btnFindAny", "findAny(): 10");

    setInput(page, "1, 2, 3");
    checkActionExact(page, "forEach", "forEach(): 1!2!3!");

    setInput(page, "1, 2, 3");
    checkActionExact(page, "noneMatch", "noneMatch(>10): true");
  }

  @Test
  public void testToArrayThrowsError(Page page) {
    List<String> consoleErrorLog = new ArrayList<>();
    page.onConsoleMessage(cm -> {
      if(cm.type().equals("error")) {
        consoleErrorLog.add(cm.text());
      }
    });
    page.waitForConsoleMessage(new Page.WaitForConsoleMessageOptions()
            .setPredicate((ConsoleMessage msg) -> msg.type().equals("error")),
        () -> page.locator(".btnToArray").click());

    assertTrue(consoleErrorLog.stream().anyMatch(e -> e.contains(
        "toArray() not supported use GemListUtil#toArraySync if you need JavaScript arrays."
    )));
  }

  @Test
  void testNewCollectors(Page page) {
    setInput(page, "1, 2, 2, 3, 3");
    checkActionExact(page, "collectToSet", "collect(toSet): [1, 2, 3]");

    setInput(page, "1, 2, 3, 4, 5");
    checkActionExact(page, "collectCounting", "collect(counting): 5");
  }
}
