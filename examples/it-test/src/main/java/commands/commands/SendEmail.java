package commands.commands;

import umlp.backendrte.command.StatusResult;

import java.util.List;
import java.util.Optional;

public class SendEmail extends SendEmailTOP {

  public SendEmail(long gemId, long userGemId, String text, Optional<String> subject) {
    super(gemId, userGemId, text, subject);
  }

  public StatusResult doAction() {
    return StatusResult.ok(0);
  }

  @Override
  public boolean checkAccessPolicy() {
    return false;
  }
}
