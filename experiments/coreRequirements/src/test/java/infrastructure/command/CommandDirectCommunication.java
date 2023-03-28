/* (c) https://github.com/MontiCore/monticore */
package infrastructure.command;

import de.se_rwth.commons.logging.Log;
import stanley.FlensburgTestContext;
import umlp.backendrte.command.ClientCommandCommunication;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandObjectMapper;
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.command.Participant;
import umlp.backendrte.command.ServerCommandCommunication;
import umlp.backendrte.common.Result;
import umlp.backendrte.util.LogUtil;

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
  protected FlensburgTestContext serverContext;
  // Wegen beidseitiger navigation muss dieser per setter gesetzt werden
  protected FlensburgTestContext clientContext;

  public CommandDirectCommunication(FlensburgTestContext backendContext) {
    this.serverContext = backendContext;
  }

  public void setClientContext(FlensburgTestContext clientContext) {
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
