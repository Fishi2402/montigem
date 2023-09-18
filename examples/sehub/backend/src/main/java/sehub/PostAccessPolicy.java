/* (c) https://github.com/MontiCore/monticore */
package sehub;

import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.Participant;

import java.util.Optional;

import static sehub.SehubRole.GUEST;

public class PostAccessPolicy extends PostAccessPolicyTOP {


  public boolean checkCreatePermission(Command command) {
    // get user and role
    Participant p = CommandManager.getInstance().getParticipant(command.getParticipantId());
    Optional<User> userOpt = SehubManager.getUserList().stream()
        .filter(u -> u.getUsername().equals(p.getUsername())) // user name is unique
        .findAny();

    if (!userOpt.isPresent()) {
      return false;
    }

    User user = userOpt.get();
    SehubRole role = user.getRole();

    if (role.equals(GUEST)) {
      return false;
    }

    return true;
  }


}


