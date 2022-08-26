package service.websocket;

import hardy.FlensburgMock;
import hardy.commands.Person_setAge;
import hardy.requests.GetFreeGemIdRequest;
import hardy.service.websocket.CommandWebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stanley.FlensburgManagerClientImplBuilder;
import stanley.FlensburgTestContext;
import umlp.command.CommandManager;
import umlp.command.CommandResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandCommunicationTest {
  @BeforeEach
  public void clear(){
    FlensburgTestContext.clear();
  }

  @Test
  public void testCommandIsSendFromClientToServer(){
    CommandWebSocketService.init();

    // TODO: connect to server using StandardWebSocketClient?

    // Start server with websocket service
    FlensburgMock.init();
    FlensburgTestContext serverContext = FlensburgTestContext.fromCurrentContext();
    long startGemId = 1001L;
    FlensburgMock.secretlyAdaptnextGemIds(startGemId);
    // TODO: init communication

    // Start client and connect to websocket
    FlensburgTestContext clientContext1 = new FlensburgManagerClientImplBuilder()
        .backendContext(serverContext)
        .build()
        .get();
    // TODO: connect to websocket

    // Send command from client to server
    clientContext1.activate();
    GetFreeGemIdRequest command = new GetFreeGemIdRequest();
    CommandResult<Long> res = CommandManager.executeCommand(command);

    // Check result
    assertNotNull(res);
    assertTrue(res.isSimpleResult());
    assertTrue(res.asSimpleResult().isSuccessful());
  }

  @Test
  public void testCommandIsSendFromServerToClient(){
    // Start server with websocket service

    // Start client an dconnect to websocket

    // send command from server to client
    Person_setAge command = new Person_setAge(1l, 3);

    // Do not check result (Server is single point of truth)
    // Check if command is received
  }

  @Test
  public void testCommandIsSendFromClientToOtherClients(){

  }
}
