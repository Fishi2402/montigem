/* (c) https://github.com/MontiCore/monticore */
package sehub;


import sehub.commands.User_get;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.Participant;

import java.util.Collection;
import java.util.Optional;

import static sehub.SehubRole.GUEST;

public class UserAccessPolicy extends UserAccessPolicyTOP {

  @Override
  public boolean checkPermission(umlp.backendrte.command.Command command) {

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


    // check authorization

    if (!role.equals(GUEST)) {
      return true;
    }

    if (command instanceof User_get) {
      long tgtUserGemId = ((User_get) command).getGemId();

      if (user.getGemId() != tgtUserGemId) {
        return false;
      } else {
        return true;
      }
    }

    return false;
  }

}

