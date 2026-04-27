package loadingtests.e2e;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static loadingtests.e2e.E2EUtil.createDummyData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestNonNestedIterates extends AbstractLoadingTestsE2ETest {

  private List<Long> personGemIds;
  private List<Long> animalGemIds;
  private List<Long> foodGemIds;

  @BeforeEach
  void init(Page page) {
    this.personGemIds = new ArrayList<>();
    this.animalGemIds = new ArrayList<>();
    this.foodGemIds = new ArrayList<>();
    // This adds a few persons (from createDummyData)
    createDummyData(this.personGemIds, this.animalGemIds, this.foodGemIds);
  }

  @Test
  void testIteratesNotNested(Page page) throws Exception {
    navAndWait(page, "/test_iterate/NonNestedIterates");
    waitForAngular(page);
    page.waitForLoadState(LoadState.NETWORKIDLE);

    // Give it a moment to render
    Thread.sleep(300);

    Object valObj = page.evaluate("window.testing_initIter2Counter");
    int val = 0;
    if (valObj instanceof Integer) {
      val = (Integer) valObj;
    } else if (valObj instanceof Double) {
      val = ((Double) valObj).intValue();
    }

    assertEquals(1, val, "initIter2Iterable was called too many times (" + val + "). This indicates the nested iterates bug is present.");
  }
}
