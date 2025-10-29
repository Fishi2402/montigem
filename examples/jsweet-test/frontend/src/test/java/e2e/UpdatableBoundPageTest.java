/* (c) https://github.com/MontiCore/monticore */
package e2e;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import jsweettest.test_infrastructure.AbstractJsweettestE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static umlp.test_rte.playwright.ComponentKind.GemText;
import static umlp.test_rte.playwright.ComponentKind.GemTextInput;

public class UpdatableBoundPageTest extends AbstractJsweettestE2ETest {

  @BeforeEach
  void init(Page page) {
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
    navAndWait(page, "/gui/test/TestUpdatableBoundPage"); //go to page we want to test
    page.waitForLoadState(LoadState.NETWORKIDLE);
  }

  @Test
  void testUpdateBoundField(Page page) {
    ElementHandle text = page.locator(GemText.selector()).getByText("Malte").elementHandle();
    assertNotNull(text);

    List<Locator> textinputs = page.locator(GemTextInput.selector()).all();
    Optional<Locator> malteInput = Optional.empty();
    for (Locator textinput : textinputs) {
      if (textinput.inputValue().equals("Malte")) {
        malteInput = Optional.of(textinput);
        break;
      }
    }
    assertTrue(malteInput.isPresent());

    String inputValue = "my val";
    malteInput.get().fill(inputValue);
    assertEquals(inputValue, text.textContent().trim());
  }
}
