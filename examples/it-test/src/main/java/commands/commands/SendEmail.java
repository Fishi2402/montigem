package commands.commands;

import umlp.backendrte.command.StatusResult;

import java.util.List;

public class SendEmail extends SendEmailTOP {

  public SendEmail(List<String> receiverList, String text) {
    super(receiverList, text);
  }

  public StatusResult doRun() {
    return StatusResult.ok(0);
  }

  @Override
  public boolean checkAccessPolicy() {
    return false;
  }
}
