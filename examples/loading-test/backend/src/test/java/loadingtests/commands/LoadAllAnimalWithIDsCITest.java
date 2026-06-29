package loadingtests.commands;

import loadingtests.Animal;
import loadingtests.LoadingTestsManager;
import loadingtests.LoadingTestsServerApplication;
import loadingtests.LoadingTestsTestContext;
import loadingtests.service.TestClientWebSocketCommandCommunication;
import loadingtests.test_infrastructure.LoadingTestsMockTestSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.TestClientCommandManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = LoadingTestsServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Execution(ExecutionMode.SAME_THREAD)
class LoadAllAnimalWithIDsCITest extends LoadingTestsMockTestSetup {

  @LocalServerPort
  private int port;
  private String uri = "ws://localhost:%s/umlp/api/command";
  private LoadingTestsTestContext serverContext;
  private LoadingTestsTestContext clientContext;
  private TestClientWebSocketCommandCommunication localhost;

  public LoadAllAnimalWithIDsCITest() {
    super(1);
  }

  @Override
  public void _init() throws Exception {
    super._init();
    this.serverContext = getBackend();
    this.clientContext = getClients(0);
    this.localhost = new TestClientWebSocketCommandCommunication(String.format(uri, port));
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

  @Test
  public void testBatchLoading10000() {
    withBatchSize(10000, 1000);
  }
  
  @Test
  public void testNormalLoading10000() {
    withBatchSize(10000, 0);
  }

  private void withBatchSize(int numberOfObjects, int batchSize) {
    serverContext.activate();
    List<Animal> createdAnimals = new ArrayList<>();
    for (int i = 0; i < numberOfObjects; i++) {
      createdAnimals.add(inventAnimal());
    }
    List<Long> createdAnimalIds = createdAnimals.stream().map(Animal::getGemId).collect(Collectors.toList());

    clientContext.activate();
    List<Animal> animals = LoadingTestsManager.getAnimalList();
    if (batchSize > 0) {
      for (int i = 0; i < createdAnimalIds.size(); i += batchSize) {
        int endIndex = Math.min(i + batchSize, createdAnimalIds.size());
        List<Long> idsBatch = createdAnimalIds.subList(i, endIndex);
        LoadingTestsManager.loadAllAnimalWithIDs(idsBatch);
      }
    }
    Set<Long> receivedAnimalIds = animals.stream().map(Animal::getGemId).collect(Collectors.toSet());

    assertEquals(numberOfObjects, animals.size());
    for (Animal createdAnimal : createdAnimals) {
      assertTrue(receivedAnimalIds.contains(createdAnimal.getGemId()));
    }
    for (Animal animal : animals) {
      assertTrue(animal.isBuildFinished());
    }
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
