/* (c) https://github.com/MontiCore/monticore */
package commands.commands;

import umlp.backendrte.command.StatusResult;

import java.util.List;
import java.util.Optional;

public class SendEmail extends SendEmailTOP {

  public SendEmail(long userGemId, String text, Optional<String> subject, List<String> forwards) {
    super(userGemId, text, subject, forwards);
  }

  public StatusResult doAction() {
    return StatusResult.ok(userGemId);
  }

}
