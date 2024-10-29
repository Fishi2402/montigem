/* (c) https://github.com/MontiCore/monticore */
package example_input;

import de.se_rwth.commons.logging.Log;
import domain.DomainManager;
import domain.Message;
import domain.Role;
import domain.commands.Answer;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandResult;
import umlp.jsweet.extension.annotation.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.function.Consumer;

@Component
public class GemChat extends GemChatTOP {
  @Override
  public Consumer<Void> sendMessage(Message message) {
    return (Void) -> this.messageHandler(message);
  }

  private void messageHandler(Message message) {
    message.setSender(Role.USER);
    message.setReceiver(Role.SYSTEM);
    message.setTimestamp(LocalDateTime.now());
    message.build();
    this.setUserMsg(DomainManager.messageBuilder());

    CommandResult<Message> messageCommandResult = CommandManager.executeCommand(
        new Answer(message.getGemId()));
    Log.info("GemChat", messageCommandResult.asSimpleResult().get().getContent());
  }
}
