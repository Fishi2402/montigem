package loginmodule.commands;

import loginmodule.LoginModuleManager;
import loginmodule.LoginModuleUser;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.Participant;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.common.LoadingState;

public class GetCurrentUserRequest extends GetCurrentUserRequestTOP {
  @Override
  public SimpleResult<Long> doAction() {
    Participant p = CommandManager.getInstance().getParticipant(getParticipantId());

    for (LoginModuleUser user : LoginModuleManager.getLoginModuleUserList()) {
      if (user.getUsername().equals(p.getUsername())) {
        return new SimpleResult<>(getId(), user.getGemId(), "java.lang.Long");
      }
    }

    return new SimpleResult<>(getId(), LoadingState.FAILED_TO_LOAD, "java.lang.Long");
  }

}
