package e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import jsweettest.test_infrastructure.AbstractJsweettestE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LocalDateTimeE2ETest extends AbstractJsweettestE2ETest {

  private static final String RESULT_INPUT_SELECTOR = "#resultIdInput";
  private static final String DATE_INPUT_SELECTOR = "#inputTestFieldIdInput";

  private static final String TEST_DATE_ISO = "2026-01-01T15:30:45";

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/gui/test/LocalDateTime");
    page.waitForLoadState(LoadState.NETWORKIDLE);
  }

  private void setInputDate(Page page) {
    waitRdy(page);
    assertThat(page.locator(DATE_INPUT_SELECTOR)).isVisible();
    page.fill(DATE_INPUT_SELECTOR, TEST_DATE_ISO);
  }

  private void checkActionExact(Page page, String buttonClass, String expectedExactValue) {
    page.locator("." + buttonClass).click();

    assertThat(page.locator(RESULT_INPUT_SELECTOR)).hasValue(expectedExactValue);
  }

  // --- TEST CASES ---

  @Test
  void testStandardGetters(Page page) {
    setInputDate(page);

    checkActionExact(page, "getHour", "Hour (0-23): 15");
    checkActionExact(page, "getMinute", "Minute (0-59): 30");
    checkActionExact(page, "getSecond", "Second (0-59): 45");
    checkActionExact(page, "getYear", "Year: 2026");
    checkActionExact(page, "getDayOfMonth", "Day of Month (1-31): 1");
  }

  @Test
  void testEnumsAndDayOfYear(Page page) {
    setInputDate(page);

    checkActionExact(page, "getDayOfMonthValue", "Month Value (1-12): 1");
    checkActionExact(page, "getDayOfMonthEnum", "Month Enum: JANUARY");
    checkActionExact(page, "getDayOfMonthWeek", "Day of Week Enum: THURSDAY");
    checkActionExact(page, "getDayOfYear", "Day of Year (1-365/366): 1");
  }

  @Test
  void testFactoryAndManipulation(Page page) {
    setInputDate(page);

    checkActionExact(page, "methodPlus", "Now + 1 Week + 2 Hours: 2026-01-08T17:30:45");

    checkActionExact(page, "methodMinus", "Now - 1 Year - 30 Minutes: 2025-01-01T15:00:45");
  }

  @Test
  void testComparisons(Page page) {
    setInputDate(page);
    checkActionExact(page, "methodIsAfter", "isAfter yesterday? true");
    checkActionExact(page, "methodIsBefore", "isBefore tomorrow? true");
    checkActionExact(page, "methodIsEqual", "isEqual? true");
  }
}
