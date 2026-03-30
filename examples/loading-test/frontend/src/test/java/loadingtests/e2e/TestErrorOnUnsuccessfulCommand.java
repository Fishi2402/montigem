package loadingtests.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static umlp.test_rte.playwright.ComponentKind.GemButton;

public class TestErrorOnUnsuccessfulCommand extends AbstractLoadingTestsE2ETest {

  @Test
  void testUpdateWithBuilder(Page page) {
    List<String> consoleErrors = new ArrayList<>();

    page.onConsoleMessage(msg -> {
      if ("error".equals(msg.type())) {
        consoleErrors.add(msg.text());
      }
    });

    navAndWait(page, "/test_internal_unsuccessful_command/UnsuccessfulCommand"); //go to page we want to test
    waitRdy(page);

    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);

    // Click button and thereby provoke unsuccessful command
    page.locator(GemButton.selector()).first().click();

    // This is the only wait option that works, since a null error is caused in JavaScript which seems to break Angular or other waiting mechanisms
    page.waitForTimeout(100);

    // Assert that an error was thrown
    boolean found = consoleErrors.stream()
        .anyMatch(msg -> msg.matches(".*0x12001: Could not load Animal with gemId .*"));

    assertTrue(found, "Expected console error for unsuccessful command. Errors were:\n"
        + String.join("\n", consoleErrors));
  }
}
