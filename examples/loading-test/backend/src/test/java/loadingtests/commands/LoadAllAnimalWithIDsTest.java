package loadingtests.commands;

import loadingtests.Animal;
import loadingtests.LoadingTestsManager;
import loadingtests.LoadingTestsServerApplication;
import loadingtests.LoadingTestsTestContext;
import loadingtests.service.TestClientWebSocketCommandCommunication;
import loadingtests.test_infrastructure.LoadingTestsMockTestSetup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.TestClientCommandManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import loadingtests.util.PerformanceTestUtil;
import loadingtests.util.PerformanceTestUtil.TestResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Performance test suite to evaluate the efficiency of the "Batch Loading" feature.
 * It tests loading large amounts of objects (up to 300,000) over the WebSocket connection,
 * comparing the "Normal" single-object fetching approach against loading objects in batches.
 * Results are written to a CSV file and visualized in an HTML report.
 */
@Tag("SlowTest")
@SpringBootTest(classes = LoadingTestsServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoadAllAnimalWithIDsTest extends LoadingTestsMockTestSetup {

  @LocalServerPort
  private int port;

  private String uri = "ws://localhost:%s/umlp/api/command";

  private LoadingTestsTestContext serverContext;

  private LoadingTestsTestContext clientContext;

  private TestClientWebSocketCommandCommunication localhost;

  private static final List<TestResult> testResults = new ArrayList<>();

  public LoadAllAnimalWithIDsTest() {
    super(1);
  }

  @Override
  public void _init() throws Exception {
    super._init();

    this.serverContext = getBackend();
    this.clientContext = getClients(0);
    this.localhost = new TestClientWebSocketCommandCommunication(
        String.format(uri, port));
    this.localhost.setContext(serverContext, clientContext);
    TestClientCommandManager testClientCommandManager = new TestClientCommandManager(this.localhost);
    CommandManager.initMe(testClientCommandManager);
    clientContext.setCommandManager(testClientCommandManager);
  }

  @BeforeEach
  public void initBeforeEach() throws Exception {
    this._init();
    serverContext.activate();
    LoadingTestsManager.reset();
    clientContext.activate();
    LoadingTestsManager.reset();
  }

  @AfterEach
  public void tearDownAfterEach() throws Exception {
    if (this.localhost != null) {
      this.localhost.close();
    }
  }

  @AfterAll
  public static void writeResultsToFile() {
    PerformanceTestUtil.writeResultsToCsv("build/reports/tests/test/performance-test-results.csv", testResults);
    PerformanceTestUtil.writeHtmlReport("build/reports/tests/test/performance-report.html", "Batch Loading Performance Analysis", testResults);
    PerformanceTestUtil.printPerformanceMatrix("Performance Matrix (Duration in ms)", testResults);
  }


//  @ParameterizedTest
//  @ValueSource(ints = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 10000, 100000, 300000 })
//  public void testLoadAllWithIDsAll(int numberOfObjects) {
//    withBatchSize("testLoadAllWithIDsAll", numberOfObjects, Integer.MAX_VALUE);
//  }


  @Order(1)
  @Tag("BatchLoading")
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 10000, 100000, 300000 })
  public void warmupNormal(int numberOfObjects) {
    withBatchSize("Warmup_Normal", numberOfObjects, 0);
  }

  @Order(2)
  @Tag("BatchLoading")
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 10000, 100000, 300000 })
  public void warmupBatch5(int numberOfObjects) {
    withBatchSize("Warmup_Batch_5", numberOfObjects, 5);
  }

  @Order(10)
  @Tag("BatchLoading")
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 10000, 100000, 300000 })
  public void testLoadAllNormal1(int numberOfObjects) {
    withBatchSize("testLoadAllNormal_run_1", numberOfObjects, 0);
  }

  @Order(11)
  @Tag("BatchLoading")
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 10000, 100000, 300000 })
  public void testLoadAllNormal2(int numberOfObjects) {
    withBatchSize("testLoadAllNormal_run_2", numberOfObjects, 0);
  }

  @Order(12)
  @Tag("BatchLoading")
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 10000, 100000, 300000 })
  public void testLoadAllWithIDsBatchSize1(int numberOfObjects) {
    withBatchSize("testLoadAllWithIDsBatchSize1", numberOfObjects, 1);
  }

  @Order(13)
  @Tag("BatchLoading")
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 10000, 100000, 300000 })
  public void testLoadAllWithIDsBatchSize1000(int numberOfObjects) {
    withBatchSize("testLoadAllWithIDsBatchSize1000", numberOfObjects, 1000);
  }

  @Order(14)
  @Tag("BatchLoading")
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 10000, 100000, 300000 })
  public void testLoadAllWithIDsBatchSize5000(int numberOfObjects) {
    withBatchSize("testLoadAllWithIDsBatchSize5000", numberOfObjects, 5000);
  }

  @Order(15)
  @Tag("BatchLoading")
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 10000, 100000, 300000 })
  public void testLoadAllWithIDsBatchSize10000(int numberOfObjects) {
    withBatchSize("testLoadAllWithIDsBatchSize10000", numberOfObjects, 10000);
  }

  @Order(16)
  @Tag("BatchLoading")
  @ParameterizedTest
  @ValueSource(ints = { 100000, 300000 })
  public void testLoadAllWithIDsBatchSize50000(int numberOfObjects) {
    withBatchSize("testLoadAllWithIDsBatchSize50000", numberOfObjects, 50000);
  }

  @Order(17)
  @Tag("BatchLoading")
  @ParameterizedTest
  @ValueSource(ints = { 300000 })
  public void testLoadAllWithIDsBatchSize100000(int numberOfObjects) {
    withBatchSize("testLoadAllWithIDsBatchSize100000", numberOfObjects, 100000);
  }

  /**
   * Helper method to generate the specified amount of objects on the server,
   * then request them from the server.
   * If batchSize > 0, the objects are requested in chunks.
   * If batchSize <= 0, all objects are requested at once using the default method.
   */
  private void withBatchSize(String testName, int numberOfObjects, int batchSize) {
    serverContext.activate();

    // Create test animals
    List<Animal> createdAnimals = new ArrayList<>();
    for (int i = 0; i < numberOfObjects; i++) {
      createdAnimals.add(inventAnimal());
    }
    List<Long> createdAnimalIds = createdAnimals.stream().map(Animal::getGemId)
        .collect(Collectors.toList());

    // Load all animals from the server
    clientContext.activate();
    long startTime = System.nanoTime();

    List<Animal> animals = LoadingTestsManager.getAnimalList();
    if (batchSize > 0) {
      for (int i = 0; i < createdAnimalIds.size(); i += batchSize) {
        int endIndex = Math.min(i + batchSize, createdAnimalIds.size());
        List<Long> idsBatch = createdAnimalIds.subList(i, endIndex);
        LoadingTestsManager.loadAllAnimalWithIDs(idsBatch);
      }
    } else {
      // no preloading
    }
    Set<Long> receivedAnimalIds = animals.stream().map(Animal::getGemId).collect(Collectors.toSet());

    long endTime = System.nanoTime();
    long durationInNanos = endTime - startTime;
    double durationInMillis = durationInNanos / 1_000_000.0;

    assertEquals(numberOfObjects, animals.size());

    for (Animal createdAnimal : createdAnimals) {
      assertTrue(receivedAnimalIds.contains(createdAnimal.getGemId()));
    }

    for (Animal animal : animals) {
      assertTrue(animal.isBuildFinished());
    }

    if (batchSize > 0) {
      System.out.printf("Loading %d animals with batch size %d took %.2f ms%n", numberOfObjects,
          batchSize, durationInMillis);
    }
    else {
      System.out.printf("Loading %d animals took %.2f ms%n", numberOfObjects, durationInMillis);
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
