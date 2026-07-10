package e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import jsweettest.test_infrastructure.AbstractJsweettestE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TestAnonymousInnerClassE2ETest extends AbstractJsweettestE2ETest {

  private static final String RESULT_INPUT_SELECTOR = "#innerClassResultIdInput";

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/gui/test/TestAnonymousInnerClass");
    page.waitForLoadState(LoadState.NETWORKIDLE);
  }

  private void checkActionExact(Page page, String buttonClass, String expectedExactValue) {
    page.locator("." + buttonClass).click();
    waitRdy(page);
    assertThat(page.locator(RESULT_INPUT_SELECTOR)).hasValue(expectedExactValue);
  }

  @Test
  void testAnonymousClassOverridingMethodInvokesConstructorCapturedConsumer(Page page) {
    // covers InnerAnonymousClass: new HelperClass($ -> {}) { @Override public void myNotification() {...} }
    checkActionExact(page, "anonymousOverride", "anonymous: consumer-invoked");
  }

  @Test
  void testAnonymousClassImplementingFunctionalInterfaceCapturesLocal(Page page) {
    checkActionExact(page, "anonymousCapture", "anonymousConsumer: captured-local-42");
  }

  @Test
  void testNonStaticInnerClassAccessesOuterInstanceField(Page page) {
    // covers InnerAnonymousClass.NastyInnerClass reading the enclosing instance's field
    checkActionExact(page, "innerOuterField", "NastyInnerClass: x=10");
  }

  @Test
  void testNonStaticInnerClassOverloadedConstructors(Page page) {
    // covers InnerAnonymousClass.NastyInnerClassWithConstructor(int) / (String)
    checkActionExact(page, "innerCtorInt", "ctor: int:7");
    checkActionExact(page, "innerCtorString", "ctor: string:seven");
  }

  @Test
  void testClearResetsResultField(Page page) {
    checkActionExact(page, "innerOuterField", "NastyInnerClass: x=10");
    checkActionExact(page, "btnClear", "");
  }
}
