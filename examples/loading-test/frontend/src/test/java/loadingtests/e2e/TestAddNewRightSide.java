package loadingtests.e2e;

import com.microsoft.playwright.Page;
import loadingtests.LeftSide;
import loadingtests.LoadingTestsManager;
import loadingtests.RightSide;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static umlp.test_rte.playwright.ComponentKind.GemButton;
import static umlp.test_rte.playwright.ComponentKind.GemText;

/**
 * Test bidirectional assocs
 */
public class TestAddNewRightSide extends AbstractLoadingTestsE2ETest {

  LeftSide leftSide;

  @BeforeEach
  void init(Page page) {
    // Bidirectional assoc
    this.leftSide = LoadingTestsManager.leftSideBuilder().label("L 1").build().get();

    // Load something with an absent optional
    // Load something with an absent optional
    // Ensure, the empty LeftSide is not actually marked as loaded, breaking everything
    LoadingTestsManager.sidesBuilder().left(LoadingTestsManager.leftSideBuilder().label("inner").build().get()).build();

    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));

    Assertions.assertNotEquals(-1, this.leftSide.getGemId());
  }

  @Test
  @Disabled // TODO: https://git.rwth-aachen.de/monticore/monticore/-/issues/4696
  void testAddNewRightSideBuilder(Page page) throws Exception {
    navAndWait(page, "/test_assoc/TestAddNewRightSide/" + leftSide.getGemId()); //go to page we want to test

    waitForAngular(page);

    assertThat(page.locator(GemText.selector() + ":text-is(\"-> : R builder\")")).hasCount(0);

    page.locator(GemButton.selector() + ":text-is(\"New using builder\")").click();

    assertThat(page.locator(GemText.selector() + ":text-is(\"-> : R builder\")")).hasCount(1);

  }

  @Test
  @Disabled // TODO: https://git.rwth-aachen.de/monticore/monticore/-/issues/4696
  void testAddNewRightSideNoBuilder(Page page) throws Exception {
    navAndWait(page, "/test_assoc/TestAddNewRightSide/" + leftSide.getGemId()); //go to page we want to test

    waitForAngular(page);

    assertThat(page.locator(GemText.selector() + ":text-is(\"-> : R 1\")")).hasCount(0);

    page.locator(GemButton.selector() + ":text-is(\"New without builder\")").click();

    assertThat(page.locator(GemText.selector() + ":text-is(\"-> : R nobuilder\")")).hasCount(1);

  }
}
