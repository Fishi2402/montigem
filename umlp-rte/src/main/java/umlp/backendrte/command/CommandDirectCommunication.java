/* (c) https://github.com/MontiCore/monticore */
package umlp.backendrte.command;

import de.se_rwth.commons.logging.Log;
import umlp.backendrte.common.Result;
import umlp.backendrte.util.LogUtil;
import umlp.backendrte.util.TestContext;

import java.util.Optional;

/**
 * Simulation der Kommunikation zwischen Client und Server auf direkter Ebene in einem Thread
 * Aktuell werden zum Ausführen eines Commands der Backend-Kontext wiederhergestellt,
 * und der Command ausgeführt.
 * Das result wird serialisiert und dann deserialisiert bevor es an den Client weitergeleitet wird.
 * Die (de)-serialisierung sorgt dafür, dass unterschiedliche Instanzen der verschickten Objekte erstellt werden,
 * wodurch sich die Datensynchronisierung testen lässt.
 */
public class CommandDirectCommunication implements ClientCommandCommunication,
    ServerCommandCommunication {
  protected TestContext serverContext;
  // Wegen beidseitiger navigation muss dieser per setter gesetzt werden
  protected TestContext clientContext;

  public CommandDirectCommunication(TestContext backendContext) {
    this.serverContext = backendContext;
  }

  public void setClientContext(TestContext clientContext) {
    this.clientContext = clientContext;
  }

  @Override
  public <T> void sendCommandToParticipant(Command<T> command) {
    clientContext.activate();

    // ser and deser to simulate communication and clone objects
    String ser = CommandObjectMapper.serializeCommand(command);
    Command<Object> deser = CommandObjectMapper.deserializeCommand(ser).get();

    Log.trace(ser, LogUtil.getLoggerName());
    deser.doRun();
    serverContext.activate();
  }

  @Override
  public <T> CommandResult<T> sendCommandToServerAndReceiveResult(Command<T> command) {
    // Use (de)-serialization both ways to simulate Communication
    // Also effectively clones objects, allowing us to test the sync mechanisms

    String cmdStr = CommandObjectMapper.serializeCommand(command);
    serverContext.activate();
    Command<?> recreatedCommand = CommandObjectMapper.deserializeCommand(cmdStr).get();

    CommandResult<?> res = CommandManager.executeCommand(recreatedCommand);
    String resStr = CommandObjectMapper.serializeResponse(res);

    clientContext.activate();
    Optional<CommandResult<?>> cmdOpt = CommandObjectMapper.deserializeResponse(resStr);
    if(cmdOpt.isEmpty()){
      Log.warn("Can not deser CommandResult from\n"+ resStr);
    }
    CommandResult<?> recreatedRes = cmdOpt.get();
    return (CommandResult<T>) recreatedRes;
  }

  @Override
  public Result<Long> init() {
    return Result.ok(serverContext.getCommandManager()._register(new Participant(this)));
  }
}
