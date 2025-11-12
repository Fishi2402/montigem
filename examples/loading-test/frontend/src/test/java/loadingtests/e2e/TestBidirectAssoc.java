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
public class TestBidirectAssoc extends AbstractLoadingTestsE2ETest {

  LeftSide leftSide;
  RightSide rightSide;

  @BeforeEach
  void init(Page page) {
    // Bidirectional assoc
    this.leftSide = LoadingTestsManager.leftSideBuilder().label("L 1").build().get();
    this.rightSide = LoadingTestsManager.rightSideBuilder().label("R 1").build().get();

    // Load something with an absent optional
    // Load something with an absent optional
    // Ensure, the empty LeftSide is not actually marked as loaded, breaking everything
    LoadingTestsManager.sidesBuilder().left(LoadingTestsManager.leftSideBuilder().label("inner").build().get()).build();

    leftSide.setConnectedRight(rightSide);

    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));

    Assertions.assertNotEquals(-1, this.rightSide.getGemId());
    Assertions.assertNotEquals(-1, this.leftSide.getGemId());
    Assertions.assertNotEquals(this.leftSide.getGemId(), this.rightSide.getGemId());
  }

  @Test
  void testBidirectionalAssocFromLeft(Page page) throws Exception {
    navAndWait(page, "/test_assoc/TestBidirecti/" + leftSide.getGemId() + "/" + rightSide.getGemId()); //go to page we want to test

    waitForAngular(page);

    assertInit(page);

    clickDisconnectLeft(page);
    assertNotConnected(page);

    clickConnectFromLeft(page);
    assertConnected(page);

    clickDisconnectLeft(page);
    assertNotConnected(page);

    clickConnectFromLeft(page);
    assertConnected(page);

    // Disconnect twice

    clickDisconnectLeft(page);
    assertNotConnected(page);

    clickDisconnectLeft(page);
    assertNotConnected(page);
  }

  @Test
  void testBidirectionalAssocFromRight(Page page) throws Exception {
    navAndWait(page, "/test_assoc/TestBidirecti/" + leftSide.getGemId() + "/" + rightSide.getGemId()); //go to page we want to test

    waitForAngular(page);

    assertInit(page);

    clickDisconnectRight(page);
    assertNotConnected(page);

    clickConnectFromRight(page);
    assertConnected(page);

    clickDisconnectRight(page);
    assertNotConnected(page);

    clickConnectFromRight(page);
    assertConnected(page);

    // Disconnect twice

    clickDisconnectRight(page);
    assertNotConnected(page);

    clickDisconnectRight(page);
    assertNotConnected(page);
  }

  @Test
  void testBidirectionalAssocConnectTwiceFromRight(Page page) throws Exception {
    navAndWait(page, "/test_assoc/TestBidirecti/" + leftSide.getGemId() + "/" + rightSide.getGemId()); //go to page we want to test

    waitForAngular(page);

    // Explicitly connect twic

    assertInit(page);
    clickDisconnectRight(page);
    assertNotConnected(page);

    clickConnectFromRight(page);
    assertConnected(page);

    clickConnectFromRight(page);
    assertConnected(page);
  }



  @Test
  void testSecondDC(Page page) throws Exception {
    navAndWait(page, "/test_assoc/TestBidirecti/" + leftSide.getGemId() + "/" + rightSide.getGemId()); //go to page we want to test

    var otherLeft = LoadingTestsManager.leftSideBuilder().label("L 1").build().get();
    var otherRight = LoadingTestsManager.rightSideBuilder().label("R 1").build().get();

    otherLeft.setConnectedRightAbsent();
    otherRight.setConnectedLeftAbsent();

    System.err.println("XXX " + otherLeft.getGemId() + "/" + otherRight.getGemId());
    System.err.println(" YYYY " + otherLeft.getConnectedRightGem().getGemId() + "/" + otherRight.getConnectedLeftGem().getGemId());


    waitForAngular(page);

    assertInit(page);
    clickDisconnectLeft(page);
    assertNotConnected(page);
  }

  protected void clickDisconnectLeft(Page page) {
    page.locator(GemButton.selector() + ":text-is(\"Disconnect from left\")").click();

  }

  protected void clickConnectFromLeft(Page page) {
    page.locator(GemButton.selector() + ":text-is(\"Connect from left\")").click();
  }

  protected void clickDisconnectRight(Page page) {
    page.locator(GemButton.selector() + ":text-is(\"Disconnect from right\")").click();
  }

  protected void clickConnectFromRight(Page page) {
    page.locator(GemButton.selector() + ":text-is(\"Connect from right\")").click();
  }

  protected void assertConnected(Page page) throws Exception {
    assertThat(page.locator(GemText.selector() + ":text-is(\"-> : R 1\")")).hasCount(1);
    assertThat(page.locator(GemText.selector() + ":text-is(\"<- : L 1\")")).hasCount(1);
    Thread.sleep(500);
    Assertions.assertEquals(this.rightSide.getGemId(), this.leftSide.getConnectedRightGem().getGemId(), "this.leftSide.getConnectedRight().getGemId");
    Assertions.assertTrue(this.leftSide.isPresentConnectedRight(), "leftSide#isPresentConnectedRight");

    Assertions.assertEquals(this.leftSide.getGemId(), this.rightSide.getConnectedLeftGem().getGemId(), "rightSide.getConnectedLeftGem().getGemId");
    Assertions.assertTrue(this.rightSide.isPresentConnectedLeft(), "rightSide#isPresentConnectedLeft " + this.rightSide.getConnectedLeftGem().getGemId() + " vs " + this.leftSide.getGemId());
  }

  protected void assertNotConnected(Page page) throws Exception {
    assertThat(page.locator(GemText.selector() + ":text-is(\"-> : R 1\")")).hasCount(0);
    assertThat(page.locator(GemText.selector() + ":text-is(\"<- : L 1\")")).hasCount(0);
    Thread.sleep(500);
    Assertions.assertNotEquals(this.rightSide.getGemId(), this.leftSide.getConnectedRightGem().getGemId(), "this.leftSide.getConnectedRight().getGemId()");
    Assertions.assertFalse(this.leftSide.isPresentConnectedRight(), "leftSide#isPresentConnectedRight");

    Assertions.assertNotEquals(this.leftSide.getGemId(), this.rightSide.getConnectedLeftGem().getGemId(), "rightSide.getConnectedLeftGem().getGemId: " + this.leftSide.getGemId() + " - " + this.rightSide.getConnectedLeftGem().getGemId());
    Assertions.assertFalse(this.rightSide.isPresentConnectedLeft(), "rightSide#isPresentConnectedLeft" + this.rightSide.getConnectedLeftGem().getGemId());
  }

  protected void assertInit(Page page) {
    assertThat(page.locator(GemText.selector() + ":text-is(\"left: L 1\")")).isVisible();
    assertThat(page.locator(GemText.selector() + ":text-is(\"-> : R 1\")")).isVisible();
    assertThat(page.locator(GemText.selector() + ":text-is(\"right: R 1\")")).isVisible();
    assertThat(page.locator(GemText.selector() + ":text-is(\"<- : L 1\")")).isVisible();
  }

  @Test
  @Disabled // TODO:https://git.rwth-aachen.de/monticore/monticore/-/issues/4697
  public void testTwice() {
    var l1 = LoadingTestsManager.leftSideBuilder().label("L 1").build().get();
    var l2 = LoadingTestsManager.leftSideBuilder().label("L 2").build().get();
    var r1 = LoadingTestsManager.rightSideBuilder().label("R 1").build().get();
    var r2 = LoadingTestsManager.rightSideBuilder().label("R 2").build().get();
    l1.setConnectedRight(r1);
    l1.setConnectedRightAbsent();
    r1.setConnectedLeft(l1);
    l2.setConnectedRight(r2);
    l1.setConnectedRightAbsent(); // org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException
    l2.setConnectedRight(r2);

    l1.setConnectedRightAbsent();
    r2.setConnectedLeftAbsent();
    l1.setConnectedRight(r2);
    l2.setConnectedRight(r1);
    l1.setConnectedRightAbsent();
    r2.setConnectedLeftAbsent();
  }
}
