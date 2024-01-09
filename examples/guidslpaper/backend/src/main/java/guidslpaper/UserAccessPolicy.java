/* (c) https://github.com/MontiCore/monticore */
package guidslpaper;


import guidslpaper.commands.User_get;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.Participant;

import java.util.Optional;

import static guidslpaper.Role.ADMIN;
import static guidslpaper.Role.USER;

public class UserAccessPolicy extends UserAccessPolicyTOP {

  @Override
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
    if (command.isRequest()) {  // read
      if (!role.equals(USER)) {
        return true;
      }

      if (command instanceof User_get) {
        long tgtUserGemId = ((User_get) command).getGemId();

        if (user.getGemId() != tgtUserGemId) {
          return false;
        }
        else {
          return true;
        }
      }

      return false;
    }

    return true;
  }

  public boolean checkCreatePermission(Command command) {
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
    return role.equals(ADMIN);
  }

  public boolean checkDeletePermission(Command command) {
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
    return role.equals(ADMIN);
  }

}

