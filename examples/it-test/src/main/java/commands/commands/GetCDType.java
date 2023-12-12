package commands.commands;

import commands.User;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.CommandResult;
import umlp.backendrte.command.SimpleResult;

public class GetCDType extends GetCDTypeTOP {

  public GetCDType(long id) {
    super(id);
  }

  @Override
  public SimpleResult<User> doAction() {
    CommandResult<User> res = CommandManager.executeCommand(new User_get(this.userId));
    return new SimpleResult<>(getId(), res.asSimpleResult().get(), "commands.User");
  }
}
