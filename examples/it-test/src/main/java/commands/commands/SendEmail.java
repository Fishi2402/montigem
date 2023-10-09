package commands.commands;

import umlp.backendrte.command.StatusResult;

import java.util.List;
import java.util.Optional;

public class SendEmail extends SendEmailTOP {

  public SendEmail(long userGemId, String text, Optional<String> subject) {
    super(userGemId, text, subject);
  }

  public StatusResult doAction() {
    return StatusResult.ok(userGemId);
  }

  @Override
  public boolean checkAccessPolicy() {
    return true;
  }
}
