package commands.commands;

import commands.User;
import commands.gemlists.UserGemList;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.ListResult;
import umlp.backendrte.common.GemListBuilder;

import java.util.List;

public class GetCDTypeList extends GetCDTypeListTOP {
  public GetCDTypeList() {
    super();
  }

  @Override
  public ListResult<User, List<User>> doAction() {
    ListResult<Long, List<Long>> res = CommandManager.executeCommand(new User_get_filtered()).asListResult();
    GemListBuilder<User> list = new UserGemList(res.get());
    return ListResult.of(this.getId(), list.build().get(), "commands.User");
  }
}
