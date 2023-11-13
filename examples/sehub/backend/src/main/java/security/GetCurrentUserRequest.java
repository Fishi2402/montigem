/* (c) https://github.com/MontiCore/monticore */
package security;

import sehub.SehubManager;
import sehub.User;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.Participant;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.common.LoadingState;
import umlp.backendrte.request.Request;

public class GetCurrentUserRequest extends Request<Long> {
  protected GetCurrentUserRequest() {
    super("security.GetCurrentUserRequest");
  }

  @Override
  public SimpleResult<Long> doAction() {
    Participant p = CommandManager.getInstance().getParticipant(getParticipantId());

    for (User user : SehubManager.getUserList()) {
      if(user.getUsername().equals(p.getUsername())){
        return new SimpleResult<>(getId(), user.getGemId(), "java.lang.Long");
      }
    }

    return new SimpleResult<>(getId(), LoadingState.FAILED_TO_LOAD, "java.lang.Long");
  }

  @Override
  public boolean checkAccessPolicy() {
    return true;
  }
}
