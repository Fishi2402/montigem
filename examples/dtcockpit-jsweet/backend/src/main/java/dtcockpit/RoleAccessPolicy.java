/* (c) https://github.com/MontiCore/monticore */
package dtcockpit;

import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.Participant;

import java.util.Optional;

import static dtcockpit.DtcockpitRole.ADMIN;

public class RoleAccessPolicy {

  public boolean checkPermission(Command command) {

    // get user and role
    Participant p = CommandManager.getInstance().getParticipant(command.getParticipantId());
    // user name is unique
    Optional<User> userOpt = Optional.empty();
    for (User u : DtcockpitManager.getUserList()) {
      if (u.getUsername().equals(p.getUsername())) {
        userOpt = Optional.of(u);
        break;
      }
    }

    if (!userOpt.isPresent()) {
      return false;
    }

    User user = userOpt.get();
    DtcockpitRole role = user.getRole();


    // check authorization
    if (role.equals(ADMIN)) {
      return true;
    }
    return false;

  }

}
