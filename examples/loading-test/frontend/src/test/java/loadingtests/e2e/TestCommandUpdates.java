package loadingtests.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import loadingtests.*;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import jakarta.annotation.Nullable;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static umlp.test_rte.playwright.ComponentKind.GemButton;
import static umlp.test_rte.playwright.ComponentKind.GemText;

public class TestCommandUpdates extends AbstractLoadingTestsE2ETest {

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
  }


  @Test
  void testWithBuild(Page page) {
    // Test that a command result (that is actually built) is correctly updated

    navAndWait(page, "/test_commands/TestCommandUpdates"); //go to page we want to test
    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);
    waitForAngular(page);

    // Check ${a1_2}.favouriteFood.name
    assertThat(page.locator(GemText.selector())).hasText("loaded");

    page.locator(GemButton.selector() + ":text-is(\"CommandWithBuild\")").click();

    assertThat(page.locator(GemText.selector())).hasText("initial");
    // the backend waits 2s before an update is done

    // TODO: MontiGem: Command reply with Gem does not receive updates (https://git.rwth-aachen.de/monticore/monticore/-/issues/4695)
    Assertions.assertThrows(AssertionError.class, () -> { // assert, that the behavior does not change - please close the issue when fixing this bug
      assertThat(page.locator(GemText.selector())).hasText("updated");
    });

  }

  @Test
  void testWithoutBuild(Page page) throws InterruptedException {
    // Test that a command result (that is never built) does not update

    navAndWait(page, "/test_commands/TestCommandUpdates"); //go to page we want to test
    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);
    waitForAngular(page);

    // Check ${a1_2}.favouriteFood.name
    assertThat(page.locator(GemText.selector())).hasText("loaded");

    page.locator(GemButton.selector() + ":text-is(\"CommandWithBuild\")").click();

    assertThat(page.locator(GemText.selector())).hasText("initial");
    // the backend waits 2s, we wait 3s
    Thread.sleep(3000);
    // and it should not be updated :)
    assertThat(page.locator(GemText.selector())).hasText("initial");
  }


}
