/* (c) https://github.com/MontiCore/monticore */
package dtcockpit.commands;

import dtcockpit.DtcockpitManager;
import dtcockpit.User;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.Participant;
import umlp.backendrte.command.SimpleResult;
import umlp.backendrte.common.LoadingState;

public class GetCurrentUserRequest extends GetCurrentUserRequestTOP {

  @Override
  public SimpleResult<Long> doAction() {
    Participant p = CommandManager.getInstance().getParticipant(getParticipantId());

    for (User user : DtcockpitManager.getUserList()) {
      if(user.getUsername().equals(p.getUsername())){
        return new SimpleResult<>(getId(), user.getGemId(), "java.lang.Long");
      }
    }

    return new SimpleResult<>(getId(), LoadingState.FAILED_TO_LOAD, "java.lang.Long");
  }

}
