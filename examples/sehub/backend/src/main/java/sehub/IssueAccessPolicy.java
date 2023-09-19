/* (c) https://github.com/MontiCore/monticore */
package sehub;

import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.Participant;

import java.util.Optional;

import static sehub.SehubRole.GUEST;
import static sehub.SehubRole.REPORTER;

public class IssueAccessPolicy extends IssueAccessPolicyTOP {

  public boolean checkCUDPermission(Command command) {

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
    if (role.equals(GUEST) || role.equals(REPORTER)) {
      return false;
    }
    return true;

  }
}

