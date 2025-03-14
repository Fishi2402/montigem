package loadingtests.e2e;

import com.microsoft.playwright.Page;
import loadingtests.LeftSide;
import loadingtests.LoadingTestsManager;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static umlp.test_rte.playwright.ComponentKind.GemButton;
import static umlp.test_rte.playwright.ComponentKind.GemText;

/**
 * Test bidirectional assocs when used in combination with builders
 */
public class TestBiBuilderAssoc extends AbstractLoadingTestsE2ETest {

  LeftSide leftSide;

  @BeforeEach
  void init(Page page) {
    // Bidirectional assoc
    this.leftSide = LoadingTestsManager.leftSideBuilder().label("L I 1").build().get();

    // Load something with an absent optional
    // Load something with an absent optional
    // Ensure, the empty LeftSide is not actually marked as loaded, breaking everything
    LoadingTestsManager.sidesBuilder().left(LoadingTestsManager.leftSideBuilder().label("inner").build().get()).build();

    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));

    Assertions.assertNotEquals(-1, this.leftSide.getGemId());
  }

  @Test
  @Disabled // TODO monticore/monticore#4550
  void testBuildOpt(Page page) {
    navAndWait(page, "/test_assoc/TestBiBuilder/" + leftSide.getGemId()); //go to page we want to test

    waitForAngular(page);

    assertInit(page);
    assertThat(page.locator(GemText.selector() + ":text-is(\"left: L I 1\")")).isVisible();
    page.locator(GemButton.selector() + ":text-is(\"New single from Left\")").click();
    assertThat(page.locator(GemText.selector() + ":text-is(\"-(s)-> : New single right side\")")).isVisible();
  }

  @Test
  void testBuildReload(Page page) {
    navAndWait(page, "/test_assoc/TestBiBuilder/" + leftSide.getGemId()); //go to page we want to test

    waitForAngular(page);

    assertInit(page);
    assertThat(page.locator(GemText.selector() + ":text-is(\"left: L I 1\")")).isVisible();
    page.locator(GemButton.selector() + ":text-is(\"New single from Left\")").click();
    page.reload();
    assertThat(page.locator(GemText.selector() + ":text-is(\"-(s)-> : New single right side\")")).isVisible();
  }

  @Test
  @Disabled // TODO monticore/monticore#4550
  void testBuildList(Page page) {
    navAndWait(page, "/test_assoc/TestBiBuilder/" + leftSide.getGemId()); //go to page we want to test

    waitForAngular(page);

    assertInit(page);
    assertThat(page.locator(GemText.selector() + ":text-is(\"left: L I 1\")")).isVisible();
    page.locator(GemButton.selector() + ":text-is(\"New from Left (List)\")").click();
    assertThat(page.locator(GemText.selector() + ":text-is(\"-(l)-> : New right side in list\")")).isVisible();
  }

  @Test
  void testBuildListReload(Page page) {
    navAndWait(page, "/test_assoc/TestBiBuilder/" + leftSide.getGemId()); //go to page we want to test

    waitForAngular(page);

    assertInit(page);
    assertThat(page.locator(GemText.selector() + ":text-is(\"left: L I 1\")")).isVisible();
    page.locator(GemButton.selector() + ":text-is(\"New from Left (List)\")").click();
    page.reload();
    assertThat(page.locator(GemText.selector() + ":text-is(\"-(l)-> : New right side in list\")")).isVisible();
  }

  protected void assertInit(Page page) {
    assertThat(page.locator(GemText.selector() + ":text-is(\"left: L I 1\")")).isVisible();
  }
}
