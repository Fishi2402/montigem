/* (c) https://github.com/MontiCore/monticore */
package dtcockpit;


import dtcockpit.commands.User_create;
import dtcockpit.commands.User_get;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.Participant;

import java.util.Collection;
import java.util.Optional;

import static dtcockpit.DtcockpitRole.*;

public class UserAccessPolicy extends UserAccessPolicyTOP {

  @Override
  public boolean checkPermission(Command command) {

    // get user and role
    Participant p = CommandManager.getInstance().getParticipant(command.getParticipantId());
    Optional<User> userOpt = DtcockpitManager.getUserList().stream()
        .filter(u -> u.getUsername().equals(p.getUsername())) // user name is unique
        .findAny();

    if (!userOpt.isPresent()) {
      return false;
    }

    User user = userOpt.get();
    DtcockpitRole role = user.getRole();


    // check authorization
    if (command.isRequest()) {  // read
      if (!role.equals(USER)) {
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

    return true;
  }

  public boolean checkCreatePermission(Command command) {
    // get user and role
    Participant p = CommandManager.getInstance().getParticipant(command.getParticipantId());
    Optional<User> userOpt = DtcockpitManager.getUserList().stream()
        .filter(u -> u.getUsername().equals(p.getUsername())) // user name is unique
        .findAny();

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

  public boolean checkDeletePermission(Command command) {
    // get user and role
    Participant p = CommandManager.getInstance().getParticipant(command.getParticipantId());
    Optional<User> userOpt = DtcockpitManager.getUserList().stream()
        .filter(u -> u.getUsername().equals(p.getUsername())) // user name is unique
        .findAny();

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

