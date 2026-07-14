package loadingtests.e2e;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static loadingtests.e2e.E2EUtil.createDummyData;
import static org.junit.jupiter.api.Assertions.assertEquals;

// Test if a given css style is correctly applied
public class TestLoadInner extends AbstractLoadingTestsE2ETest {

  private List<Long> gameGemIds;
  private List<Long> playerGemIds;

  @BeforeEach
  void init(Page page) {
    this.gameGemIds = new ArrayList<>();
    this.playerGemIds = new ArrayList<>();
    createDummyData(this.gameGemIds, this.playerGemIds);
    attachLoggersToPage(page, "[BROWSER]");
  }


  @Test
  void testStyleOnPageLoad(Page page) {
    Long gameGemID = this.gameGemIds.get(0);
    Long playerGemId = this.playerGemIds.get(0);

    navAndWait(page, "/test_load_inner/PageC/" + gameGemID + "/" + playerGemId); //go to page we want to test
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

    navAndWait(page, "/test_load_inner/PageC/" + gameGemID + "/" + playerGemId); //go to page we want to test
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
  void testAfterRouteChange(Page page) {
    Long gameGemID = this.gameGemIds.get(0);
    Long playerGemId = this.playerGemIds.get(0);

    navAndWait(page, "/test_load_inner/PageC/" + gameGemID + "/" + playerGemId); //go to page we want to test
    // We need this to ensure that all Client Request were answered from the Backend
    page.waitForLoadState(LoadState.NETWORKIDLE);
    waitForAngular(page);

    page.locator(".nav2").click();
    page.waitForLoadState(LoadState.NETWORKIDLE);

    // PlayerList.gui: y = ["1fr", "auto"],
    assertEquals("1fr auto", page.locator(".playerContainer gem-grid.outergrid")
        .evaluate("node => node.style.gridTemplateRows"));
  }

  protected void attachLoggersToPage(Page page, final String loggerName) {
    page.onConsoleMessage(cm -> System.out.println(loggerName+" " + cm.text()));
    page.onClose(p -> System.out.println(loggerName+" closed."));
    page.onCrash(p -> System.out.println(loggerName+" crashed."));
  }


  @Test
  public void testPlayerStats(Page page) {
    navAndWait(page, "/test_load_inner/PlayerStatsIndex");

    // Open all 3 pages at the same time
    try (var playerStats = page.context().newPage()) {
      attachLoggersToPage(playerStats, "[BROWSER playerStats]");
      navAndWait(playerStats, "/test_load_inner/PlayerStatsIndex");
      playerStats.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("PlayerStats").setExact(true)).click();
      try (var playerStatsWithComponent = page.context().newPage()) {
        attachLoggersToPage(playerStatsWithComponent, "[BROWSER playerStatsWithComponent]");
        navAndWait(playerStatsWithComponent, "/test_load_inner/PlayerStatsIndex");
        playerStatsWithComponent.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("PlayerStatsWithComponent").setExact(true)).click();
        try (var playerStatsGlobal = page.context().newPage()) {
          attachLoggersToPage(playerStatsGlobal, "[BROWSER playerStatsGlobal]");
          navAndWait(playerStatsGlobal, "/test_load_inner/PlayerStatsIndex");
          playerStatsGlobal.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("PlayerStatsGlobal").setExact(true)).click();


          BiConsumer<Function<Page, Locator>, Consumer<LocatorAssertions>> assertThat = (locator, locatorAssertionsConsumer) -> {
            waitRdy(page);
            // Wait for 400ms to let the backend execute any Commands and notify all clients
            waitForBackend();
            waitForBackend();

            waitRdy(playerStats);
            locatorAssertionsConsumer.accept(assertThat(locator.apply(playerStats)));
            waitRdy(playerStatsWithComponent);
            locatorAssertionsConsumer.accept(assertThat(locator.apply(playerStatsWithComponent)));
            waitRdy(playerStatsGlobal);
            locatorAssertionsConsumer.accept(assertThat(locator.apply(playerStatsGlobal)));
          };

          // Starts with a reset
          page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Reset")).click();

          // Is the initial player visible?
          assertThat.accept(p->p.getByText("Player: Initial"), LocatorAssertions::isVisible);

          page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("add stat local")).click();
          assertThat.accept(p->p.getByText("> local stat of Initial"), LocatorAssertions::isVisible);

          page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("add stat remote")).click();
          assertThat.accept(p->p.getByText("> remote stat of Initial"), LocatorAssertions::isVisible);

          page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("add player local")).click();
          assertThat.accept(p->p.getByText("Player: From Local"), LocatorAssertions::isVisible);

          page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("add player remote")).click();
          assertThat.accept(p->p.getByText("Player: From Remote"), LocatorAssertions::isVisible);


          page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Reset")).click();

          assertThat.accept(p->p.getByText("Player: From Remote"), l -> l.hasCount(0));
          assertThat.accept(p->p.getByText("Player: From Local"), l -> l.hasCount(0));
          assertThat.accept(p->p.getByText("> local stat of Initial"), l -> l.hasCount(0));
          assertThat.accept(p->p.getByText("> remote stat of Initial"), l -> l.hasCount(0));
        }
      }
    }
  }

}
