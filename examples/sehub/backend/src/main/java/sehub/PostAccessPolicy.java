/* (c) https://github.com/MontiCore/monticore */
package sehub;

import sehub.commands.Post_delete;
import sehub.commands.Post_setText;
import sehub.commands.Post_setTitle;
import sehub.commands.associations.Discussion_remove;
import sehub.commands.associations.Resp_remove;
import umlp.backendrte.command.Command;
import umlp.backendrte.command.CommandManager;
import umlp.backendrte.command.Participant;

import java.util.Optional;

import static sehub.SehubRole.GUEST;
import static sehub.SehubRole.OWNER;

public class PostAccessPolicy extends PostAccessPolicyTOP {

  public boolean checkWritePermission(Command command) {
    // for editing of deletion

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

    if (role.equals(OWNER)) {
      return true;
    }

    // workaround: to be improved in future versions
    long postID = -1;
    if (command.getCommandType().equals("sehub.commands.Post_setText")) {
      postID = ((Post_setText) command).getGemId();
    } else if (command.getCommandType().equals("sehub.commands.Post_setTitle")) {
      postID = ((Post_setTitle) command).getGemId();
    } else if (command.getCommandType().equals("sehub.commands.Post_delete")) {
      postID = ((Post_delete) command).getGemId();
    } else  if (command.getCommandType().equals("sehub.commands.associations.Resp_remove")) {
      postID = ((Resp_remove) command).getResponsesGemId();
    } else  if (command.getCommandType().equals("sehub.commands.associations.Discussion_remove")) {
      postID = ((Discussion_remove) command).getPostsGemId();
    }

    if (postID != -1) {
      Optional<Post> post = SehubManager.postOpt(postID);
      if (post.isPresent()) {
        if (post.get().getAuthor().getUsername().equals(user.getUsername())) {
          return true;
        }
      }
    }
    return false;

  }

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


