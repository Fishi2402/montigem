/* (c) https://github.com/MontiCore/monticore */
package guidslpaper;

import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.Participant;

import java.util.Optional;

public class RoleAccessPolicy {

  public boolean checkPermission(Command command) {

    // get user and role
    Participant p = CommandManager.getInstance().getParticipant(command.getParticipantId());
    Optional<User> userOpt = GuidslpaperManager.getUserList().stream()
        .filter(u -> u.getUsername().equals(p.getUsername())) // user name is unique
        .findAny();

    if (!userOpt.isPresent()) {
      return false;
    }

    User user = userOpt.get();
    Role role = user.getRole();


    // check authorization
    return role.equals(Role.ADMIN);

  }

}
