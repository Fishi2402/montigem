package loginmodule.commands;

import loginmodule.LoginModuleManager;
import umlp.backendrte.command.ListResult;

import java.util.ArrayList;
import java.util.List;

public class LoginModuleUser_get_filtered extends LoginModuleUser_get_filteredTOP {

  @Override
  public ListResult<Long, List<Long>> doAction() {

    List<Long> validAccessIDs = new ArrayList<>();
    for (long id : LoginModuleManager.getLoginModuleUserList().getIDs()) {
      LoginModuleUser_get atomicCmd = new LoginModuleUser_get(id);
      atomicCmd.setParticipantId(this.getParticipantId());
      if (atomicCmd.doRun().isSuccessful()) {
        validAccessIDs.add(id);
      }
    }

    if (!validAccessIDs.isEmpty()) {
      return ListResult.of(getId(), validAccessIDs, "java.lang.Long");
    } else {
      return ListResult.error(getId(), "java.lang.Long");
    }

  }
}
