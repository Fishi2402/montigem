package loadingtests.e2e;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import loadingtests.LeftSide;
import loadingtests.LoadingTestsManager;
import loadingtests.RightSide;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static umlp.test_rte.playwright.ComponentKind.GemButton;

public class TestGemListInterfaceMethodSync extends AbstractLoadingTestsE2ETest {

  LeftSide leftSide;
  RightSide rightSide;
  RightSide rightSideAlternative;

  @BeforeEach
  void init(Page page) {
    // Bidirectional assoc
    this.leftSide = LoadingTestsManager.leftSideBuilder().label("L 1").build().get();
    this.rightSide = LoadingTestsManager.rightSideBuilder().label("R 1").build().get();
    this.rightSideAlternative = LoadingTestsManager.rightSideBuilder().label("R 2").build().get();

    page.onConsoleMessage(cm -> System.out.println("[BROWSER] " + cm.text()));
  }

  @Test
  public void testAdd(Page page) throws Exception{
    navAndWait(page, "/test_gemlist_listinterface_sync/TestGemListListInterface/"
        + leftSide.getGemId()+ "/"+rightSide.getGemId()); //go to page we want to test
    waitRdy(page);

    Locator l = page.locator(GemButton.selector() + ":text-is(\"Test List Interface Add\")");
    l.click();

    waitRdy(page);
    assertEquals(1, leftSide.getConnectedRightListList().size());
    assertEquals(rightSide.getGemId(), leftSide.getConnectedRightListList().get(0).getGemId());
  }

  @Test
  public void testRemove(Page page) throws Exception{
    leftSide.addConnectedRightList(rightSide);
    navAndWait(page, "/test_gemlist_listinterface_sync/TestGemListListInterface/"
        + leftSide.getGemId()+ "/"+rightSide.getGemId()); //go to page we want to test


    page.locator(GemButton.selector() + ":text-is(\"Test List Interface Remove\")").click();

    waitRdy(page);
    assertTrue(leftSide.getConnectedRightListList().isEmpty());
  }

  @Disabled
  @Test
  public void testSet(Page page) throws Exception{
    leftSide.addConnectedRightList(rightSide);
    assertEquals(rightSide.getGemId(), leftSide.getConnectedRightListList().get(0).getGemId());
    navAndWait(page, "/test_gemlist_listinterface_sync/TestGemListListInterface/"
        + leftSide.getGemId()+ "/"+rightSideAlternative.getGemId()); //go to page we want to test
    waitRdy(page);

    page.locator(GemButton.selector() + ":text-is(\"Test List Interface Set\")").click();

    waitRdy(page);
    assertEquals(1, leftSide.getConnectedRightListList().size());
    assertEquals(rightSideAlternative.getGemId(), leftSide.getConnectedRightListList().get(0).getGemId());
  }
}
