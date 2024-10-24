package domain.commands;

import domain.DomainManager;
import domain.Message;
import domain.Role;
import umlp.backendrte.command.SimpleResult;

import java.time.LocalDateTime;
import java.util.Optional;

public class Answer extends AnswerTOP{
  public Answer(long mGemId) {
    super(mGemId);
  }

  @Override
  public SimpleResult<Message> doAction() {
    DomainManager.getMessageSure(this.getMGemId());
    Optional<Message> msg = DomainManager.messageBuilder().content("Mockup fom Backend Service :)").sender(Role.ASSISTANT).receiver(Role.USER).timestamp(LocalDateTime.now()).build();


    return new SimpleResult<>(this.mGemId, msg.get(), "domain.Message");
  }
}
