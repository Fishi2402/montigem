/* (c) https://github.com/MontiCore/monticore */
package sehub;

import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.Participant;

import java.util.Optional;

import static sehub.SehubRole.OWNER;

public class RoleAccessPolicy {

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
    if (role.equals(OWNER)) {
      return true;
    }
    return false;

  }

}
