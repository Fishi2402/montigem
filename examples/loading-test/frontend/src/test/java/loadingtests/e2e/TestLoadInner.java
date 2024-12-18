package loadingtests.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import de.se_rwth.commons.logging.Log;
import loadingtests.LoadingTestsManager;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static loadingtests.e2e.E2EUtil.createDummyData;
import static loadingtests.e2e.E2EUtil.isLoadedAnimalFunction;
import static loadingtests.e2e.E2EUtil.isLoadedFoodFunction;
import static loadingtests.e2e.E2EUtil.isLoadedGameFunction;
import static loadingtests.e2e.E2EUtil.isLoadedPersonFunction;
import static loadingtests.e2e.E2EUtil.isLoadedPlayerFunction;
import static org.junit.jupiter.api.Assertions.assertEquals;

// Test if a given css style is correctly applied
public class TestLoadInner extends AbstractLoadingTestsE2ETest {

  private List<Long> gameGemIds;
  private List<Long> playerGemIds;

  @BeforeEach
  void init(Page page) {
    this.gameGemIds= new ArrayList<>();
    this.playerGemIds= new ArrayList<>();
    createDummyData(this.gameGemIds, this.playerGemIds);
    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
  }


  @Disabled
  @Test
  void testStyleOnPageLoad(Page page) {
    Long gameGemID = this.gameGemIds.get(0);
    Long playerGemId = this.playerGemIds.get(0);

    navAndWait(page, "/test_load_inner/PageC/"+gameGemID+"/" + playerGemId); //go to page we want to test
    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);
    waitForAngular(page);

    // PlayerList.gui: y = ["1fr", "auto"],
    assertEquals("1fr auto", page.locator(".playerContainer gem-grid.outergrid")
        .evaluate("node => node.style.gridTemplateRows"));
  }

  @Test
  void testAfterButtonClick(Page page) {
    Long gameGemID = this.gameGemIds.get(0);
    Long playerGemId = this.playerGemIds.get(0);

    navAndWait(page, "/test_load_inner/PageC/"+gameGemID+"/" + playerGemId); //go to page we want to test
    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);
    waitForAngular(page);

    // Click on the player selection button -> calls setPlayer
    page.locator(".others gem-button").click();
    page.waitForLoadState(LoadState.NETWORKIDLE);

    // PlayerList.gui: y = ["1fr", "auto"],
    assertEquals("1fr auto", page.locator(".playerContainer gem-grid.outergrid")
        .evaluate("node => node.style.gridTemplateRows"));

  }

  @Test
  @Disabled
  void testAfterRouteChange(Page page) {
    Long gameGemID = this.gameGemIds.get(0);
    Long playerGemId = this.playerGemIds.get(0);

    navAndWait(page, "/test_load_inner/PageC/"+gameGemID+"/" + playerGemId); //go to page we want to test
    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);
    waitForAngular(page);

    page.locator(".nav2").click();
    page.waitForLoadState(LoadState.NETWORKIDLE);

    // PlayerList.gui: y = ["1fr", "auto"],
    assertEquals("1fr auto", page.locator(".playerContainer gem-grid.outergrid")
        .evaluate("node => node.style.gridTemplateRows"));

  }

}
