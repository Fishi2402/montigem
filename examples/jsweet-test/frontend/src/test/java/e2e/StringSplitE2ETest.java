package e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import jsweettest.test_infrastructure.AbstractJsweettestE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class StringSplitE2ETest extends AbstractJsweettestE2ETest {

  private static final String RESULT_INPUT_SELECTOR = "#resultId";
  private static final String INPUT_SELECTOR = "#inputTestFieldId";
  private static final String REGEX_SELECTOR = "#regexTestFieldIdInput";
  private static final String LIMIT_SELECTOR = "#limitTestFieldIdInput";

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/gui/test/StringSplit");
    page.waitForLoadState(LoadState.NETWORKIDLE);
  }

  private void setParams(Page page, String input, String regex, int limit) {
    waitRdy(page);
    assertThat(page.locator(INPUT_SELECTOR)).isVisible();
    assertThat(page.locator(REGEX_SELECTOR)).isVisible();
    assertThat(page.locator(LIMIT_SELECTOR)).isVisible();
    page.fill(INPUT_SELECTOR, input);
    page.fill(REGEX_SELECTOR, regex);
    page.fill(LIMIT_SELECTOR, Integer.toString(limit));
  }

  private void checkActionExact(Page page, String buttonClass, String... expectedExactLines) {
    page.locator("." + buttonClass).click();

    String expectedExactValue = String.join("#", expectedExactLines);

    assertThat(page.locator(RESULT_INPUT_SELECTOR)).hasValue(expectedExactValue);
  }

  // --- TEST CASES ---

  @Test
  void testSplitSimple(Page page) {
    setParams(page, "a,,b,c,d,e,", ",", 4);

    // JS: "a,,b,c,d,e,".split(",")
    // [ "a", "", "b", "c", "d", "e", "" ]
    checkActionExact(page, "split", "a", "", "b", "c", "d", "e");

    // JS: "a,,b,c,d,e,".split(",", 4)
    // [ "a", "", "b", "c" ]
    checkActionExact(page, "limitSplit", "a", "", "b", "c,d,e,");
  }

  @Test
  void testSplitRegex(Page page) {
    setParams(page, "a||b|c|d|e|", "\\|", 4);

    // JS: "a||b|c|d|e|".split(/\|/)
    // [ "a", "", "b", "c", "d", "e", "" ]
    checkActionExact(page, "split", "a", "", "b", "c", "d", "e");

    // JS: "a||b|c|d|e|".split(/\|/, 4)
    // [ "a", "", "b", "c" ]
    checkActionExact(page, "limitSplit", "a", "", "b", "c|d|e|");
  }

  @Test
  void testSplitEmptyRegex(Page page) {
    // JS: "abcde".split("", 3)
    // [ "a", "b", "c" ]

    setParams(page, "abcde", "", 3);
    checkActionExact(page, "split", "a", "b", "c", "d", "e");
    checkActionExact(page, "limitSplit", "a", "b", "cde");

    setParams(page, "abcde", "(?i)", 3);
    checkActionExact(page, "split", "a", "b", "c", "d", "e");
    checkActionExact(page, "limitSplit", "a", "b", "cde");
  }

  @Test
  void testSplitEmbeddedIFlag(Page page) {
    setParams(page, "azbYccZdddyeezfYgz", "(?i)[yz]", 6);

    // JS: "azbYccZdddyeezfYgz".split(/[yz]/i)
    // [ "a", "b", "cc", "ddd", "ee", "f", "g", "" ]
    checkActionExact(page, "split", "a", "b", "cc", "ddd", "ee", "f", "g");

    // JS: "azbYccZdddyeezfYgz".split(/[yz]/i, 6)
    // [ "a", "b", "cc", "ddd", "ee", "f" ]
    checkActionExact(page, "limitSplit", "a", "b", "cc", "ddd", "ee", "fYgz");
  }

  @Test
  void testSplitEmbeddedIMFlags(Page page) {
    String multiline =  "foo\noOooO\nbar\nOobaz\noooooooO\nmonticore\no";

    // JS: multiline.split(/^o+$/mi)
    // [ "foo\n", "\nbar\nOobaz\n", "\nmonticore\n", "" ]

    // JS: multiline.split(/^o+$/mi, 2)
    // [ "foo\n", "\nbar\nOobaz\n" ]

    setParams(page, multiline, "(?m)(?i)^o+$", 2);
    checkActionExact(page, "split",  "foo\n", "\nbar\nOobaz\n", "\nmonticore\n");
    checkActionExact(page, "limitSplit",  "foo\n", "\nbar\nOobaz\noooooooO\nmonticore\no");

    setParams(page, multiline, "(?im)^o+$", 2);
    checkActionExact(page, "split",  "foo\n", "\nbar\nOobaz\n", "\nmonticore\n");
    checkActionExact(page, "limitSplit",  "foo\n", "\nbar\nOobaz\noooooooO\nmonticore\no");

    setParams(page, multiline, "(?i)^o+$", 2);
    checkActionExact(page, "split",  multiline);
    checkActionExact(page, "limitSplit", multiline);
  }
}
