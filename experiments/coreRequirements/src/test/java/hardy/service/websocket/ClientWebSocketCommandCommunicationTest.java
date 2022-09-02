package hardy.service.websocket;

import hardy.*;
import hardy.commands.Person_get;
import infrastructure.command.ClientWebSocketCommandCommunication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import infrastructure.command.FlensburgManagerClientImplBuilder;
import stanley.FlensburgTestContext;
import umlp.command.CommandResult;
import umlp.rte.Result;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = UMLPApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClientWebSocketCommandCommunicationTest {

  @LocalServerPort
  private int port;

  private String uri = "ws://localhost:%s/umlp/api/command";

  @Test
  void testInit() {
    ClientWebSocketCommandCommunication connection = new ClientWebSocketCommandCommunication(uri, port);
    long participantId = 0l;
    Result<Long> init = connection.init();
    if (init.wasSuccessful()) {
      participantId = init.getValue();
    } else {
      fail("Could not establish websocket connection");
    }
    assertEquals(1l, participantId, "Websocket connection was not established first");
  }

  @Test
  void testSendCommandAndReceiveResult() {
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    (new FlensburgDummies()).inventDummiesGenerated(5,5,5);
    FlensburgMock.secretlyAdaptnextGemIds(startGemId);

    ClientWebSocketCommandCommunication connection = new ClientWebSocketCommandCommunication(uri, port);
    FlensburgTestContext clientContext = new FlensburgManagerClientImplBuilder()
        .backendContext(serverContext)
        .commandCommunication(connection).build().get();
    connection.setContext(serverContext, clientContext);

    clientContext.activate();
    Person_get person_get = new Person_get(1001);
    CommandResult<Person> personCommandResult = connection.sendCommandToServerAndReceiveResult(person_get);
    System.out.println(personCommandResult.asSimpleResult().get());
  }

  @Test
  void testPersonSyncOverWebsocket() {
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptnextGemIds(startGemId);
    (new FlensburgDummies()).inventDummiesGenerated(5,5,5);

    ClientWebSocketCommandCommunication connection1 = new ClientWebSocketCommandCommunication(uri, port);
    FlensburgTestContext clientContext1 = new FlensburgManagerClientImplBuilder()
        .backendContext(serverContext)
        .commandCommunication(connection1).build().get();
    connection1.setContext(serverContext, clientContext1);

    ClientWebSocketCommandCommunication connection2 = new ClientWebSocketCommandCommunication(uri, port);
    FlensburgTestContext clientContext2 = new FlensburgManagerClientImplBuilder()
        .backendContext(serverContext)
        .commandCommunication(connection2).build().get();
    connection2.setContext(serverContext, clientContext2);

    clientContext1.activate();
    Person person1 = FlensburgManager.getPersonSure(startGemId);
    Integer age1 = person1.getAge();
    // Warte bis alle Daten synchronisiert wurden
    CountDownLatch clientCountDownLatch1 = connection1.setCountDownLatchToIncomingMessages(1);

    clientContext2.activate();
    Person person2 = FlensburgManager.getPersonSure(1001L);
    person2.setAge(person2.getAge() + 10);
    waitFor(clientCountDownLatch1);

    assertEquals(age1 + 10, person1.getAge());
  }

  private void waitFor(CountDownLatch clientCountDownLatch1) {
    try {
      clientCountDownLatch1.await(5, TimeUnit.SECONDS);
    }
    catch (InterruptedException e) {
      fail();
    }
  }
}