package loadingtests.e2e;

import com.microsoft.playwright.Page;
import de.se_rwth.commons.logging.LogStub;
import loadingtests.Animal;
import loadingtests.LoadingTestsManager;
import loadingtests.test_infrastructure.AbstractLoadingTestsE2ETest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import loadingtests.util.PerformanceTestUtil;
import loadingtests.util.PerformanceTestUtil.TestResult;
import standalone.com.sun.tools.sjavac.Log;

@Tag("SlowTest")
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestLoadAllAnimalWithIDs extends AbstractLoadingTestsE2ETest {

  private static final List<TestResult> testResults = new ArrayList<>();

  private static Log log;

  @BeforeEach
  void init(Page page) {
    page.setDefaultTimeout(600_000);
    page.setDefaultNavigationTimeout(600_000);
    page.onConsoleMessage(cm -> {
        if ("error".equals(cm.type())) {
            Log.warn("[BROWSER ERROR] " + cm.text());
        }
    });
  }
  
  @BeforeAll
  public static void initLog() {
    log = Log.get();
    LogStub.init();
  }

  @AfterAll
  public static void resetLog() {
    Log.setLogForCurrentThread(log);
  }

  @AfterAll
  public static void writeResultsToFile() {
    PerformanceTestUtil.writeResultsToCsv("build/reports/tests/test/frontend-performance-test-results.csv", testResults);
    PerformanceTestUtil.writeHtmlReport("build/reports/tests/test/frontend-performance-report.html", "Frontend Batch Loading Performance Analysis", testResults);
    PerformanceTestUtil.printPerformanceMatrix("Frontend Performance Matrix (Duration in ms)", testResults);
  }


//  @Order(1)
//  @ParameterizedTest
//  @ValueSource(ints = { 1, 10, 100, 1000, 10_000 })
//  public void warmupNormal(int numberOfObjects, Page page) {
//    withBatchSize("Warmup_Normal", numberOfObjects, 0, page);
//  }
//
//  @Order(2)
//  @ParameterizedTest
//  @ValueSource(ints = { 1, 10, 100, 1000, 10_000 })
//  public void warmupBatch5(int numberOfObjects, Page page) {
//    withBatchSize("Warmup_Batch_5", numberOfObjects, 5, page);
//  }

  @Order(10)
  @Tag("BatchLoading")
  @ParameterizedTest
  @ValueSource(ints = { 1, 10, 100, 1000, 10_000, 50_000, 100_000})
  public void testLoadAllNormal(int numberOfObjects, Page page) {
    withBatchSize("testLoadAllNormal", numberOfObjects, 0, page);
  }

  @Order(12)
  @Tag("BatchLoading")
  @ParameterizedTest
  @ValueSource(ints = { 1, 10, 100, 1000, 10_000, 50_000})
  public void testLoadAllWithIDsBatchSize1(int numberOfObjects, Page page) {
    withBatchSize("testLoadAllWithIDsBatchSize1", numberOfObjects, 1, page);
  }

  @Order(13)
  @Tag("BatchLoading")
  @ParameterizedTest
  @ValueSource(ints = { 1, 10, 100, 1000, 10_000, 50_000, 100_000, 300_000})
  public void testLoadAllWithIDsBatchSize1000(int numberOfObjects, Page page) {
    withBatchSize("testLoadAllWithIDsBatchSize1000", numberOfObjects, 1000, page);
  }

  @Order(14)
  @Tag("BatchLoading")
  @ParameterizedTest
  @ValueSource(ints = { 1, 10, 100, 1000, 10_000, 50_000, 100_000, 300_000})
  public void testLoadAllWithIDsBatchSize5000(int numberOfObjects, Page page) {
    withBatchSize("testLoadAllWithIDsBatchSize5000", numberOfObjects, 5000, page);
  }

  @Order(15)
  @Tag("BatchLoading")
  @ParameterizedTest
  @ValueSource(ints = { 1, 10, 100, 1000, 10_000, 50_000, 100_000, 300_000})
  public void testLoadAllWithIDsBatchSize10_000(int numberOfObjects, Page page) {
    withBatchSize("testLoadAllWithIDsBatchSize10000", numberOfObjects, 10_000, page);
  }

  @Order(16)
  @Tag("BatchLoading")
  @ParameterizedTest
  @ValueSource(ints = { 1, 10, 100, 1000, 10_000, 50_000, 100_000, 300_000})
  public void testLoadAllWithIDsBatchSize50_000(int numberOfObjects, Page page) {
    withBatchSize("testLoadAllWithIDsBatchSize50000", numberOfObjects, 50_000, page);
  }

  private void withBatchSize(String testName, int numberOfObjects, int batchSize, Page page) {
    LoadingTestsManager.reset();

    // Create test animals in the backend JVM
    List<Animal> createdAnimals = new ArrayList<>();
    for (int i = 0; i < numberOfObjects; i++) {
      createdAnimals.add(inventAnimal());
    }
    List<Integer> createdAnimalIds = createdAnimals.stream().map(a -> (int)a.getGemId())
        .collect(Collectors.toList());

    // Navigate to a page first to establish a valid origin for localStorage
    navAndWait(page, "/test_load_all/DisplayAnimals");

    // Set properties in local storage so that LoadAnimalsBatchTest can read them
    page.evaluate("() => { " +
        "localStorage.setItem('testBatchSize', '" + batchSize + "'); " +
        "localStorage.setItem('testNumberOfObjects', '" + numberOfObjects + "'); " +
        "}");

    // Clear JS cache
    page.reload();
    waitForAngular(page);

    long startTime = System.nanoTime();
    
    // This navigation will trigger the Angular component initialization,
    // which in turn executes the batch loading logic implemented in LoadAnimalsBatchTest.java
    navAndWait(page, "/test_load_all/LoadAnimalsBatchTest");
    
    // Wait until all requested animals are loaded on the frontend
    // Evaluating the loop entirely in the browser to avoid Playwright protocol overhead.
    page.waitForFunction(
        "() => {" +
        "  const ids = " + createdAnimalIds.toString() + ";" +
        "  for(let i=0; i<ids.length; i++) {" +
        "    if(window.LoadingTestsManager.isLoadedAnimal(ids[i]) !== true) return false;" +
        "  }" +
        "  return true;" +
        "}");

    long endTime = System.nanoTime();
    long durationInNanos = endTime - startTime;
    double durationInMillis = durationInNanos / 1_000_000.0;

    if (batchSize > 0) {
      Log.log(Log.Level.WARN, String.format("Frontend loading %d animals with batch size %d took %.2f ms%n", numberOfObjects,
          batchSize, durationInMillis));
    }
    else {
      Log.log(Log.Level.WARN, String.format("Frontend loading %d animals took %.2f ms%n", numberOfObjects, durationInMillis));
    }

    testResults.add(new TestResult(testName, numberOfObjects, batchSize, durationInMillis));
  }

  private Animal inventAnimal() {
    Random random = new Random();
    StringBuilder nameBuilder = new StringBuilder();
    int nameLength = random.nextInt(3, 11);
    for (int i = 0; i < nameLength; i++) {
      nameBuilder.append((char) (random.nextInt(26) + 'a'));
    }
    String name = nameBuilder.toString();
    Integer numberOfLegs = random.nextInt(1, 41);
    return LoadingTestsManager.animalBuilder()
        .name(name)
        .legs(numberOfLegs)
        .build().get();
  }
}
