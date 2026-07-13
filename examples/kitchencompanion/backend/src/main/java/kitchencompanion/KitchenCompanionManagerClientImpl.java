package kitchencompanion;

import kitchencompanion.commands.User_get_filtered;
import kitchencompanion.gemlists.UserGemList;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.ListResult;
import umlp.backendrte.common.GemList;
import umlp.backendrte.common.GemListBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KitchenCompanionManagerClientImpl extends KitchenCompanionManagerClientImplTOP {
  //private GemList<User> users = new UserGemList();
  @umlp.jsweet.extension.annotation.Synchronized
  @Override
  protected GemList<User> _doGetUserList(){
    if (this.loadedUserList.isEmpty()) {
      ListResult<Long, List<Long>> res = CommandManager.executeCommand(new User_get_filtered()).asListResult();
      GemListBuilder<User> list = new UserGemList(res.get());
      this.loadedUserList = Optional.of(sortUsersList(list.build().get()));
    }
    return this.loadedUserList.get();
  }

  private static GemList<User> sortUsersList(GemList<User> users) {
    int n = users.size();
    for (int i = 0; i < n - 1; i++) {
      for (int j = 0; j < n - i - 1; j++) {
        User a = users.get(j);
        User b = users.get(j + 1);

        boolean shouldSwap = false;
        int prioA = getPriority(a);
        int prioB = getPriority(b);

        if (prioA != prioB) {
          shouldSwap = prioA < prioB;
        } else {
          int cmpLast = a.getLastName().compareToIgnoreCase(b.getLastName());
          if (cmpLast > 0) {
            shouldSwap = true;
          } else if (cmpLast == 0) {
            int cmpFirst = a.getName().compareToIgnoreCase(b.getName());
            shouldSwap = cmpFirst > 0;
          }
        }
        if (shouldSwap) {
          users.set(j, b);
          users.set(j + 1, a);
        }
      }
    }
    return users;
  }
  private static int getPriority(User u) {
    if (u.getLastName().compareToIgnoreCase("Rumpe") == 0) {
      return 2;
    }
    return 0;
  }

}
