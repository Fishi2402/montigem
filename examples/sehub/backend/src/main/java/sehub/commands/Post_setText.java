/* Created by decorator umlp.generator.domain.decorator.commands.SetDecorator */
/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import sehub.Post;
import sehub.SehubManager;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.GeneralErrorCollector;
import umlp.backendrte.common.NotificationScope;

import java.util.Optional;

public class Post_setText extends Post_setTextTOP {

  public Post_setText(long gemId, String text) {
    super(gemId, text);
  }

  @Override
  public StatusResult doAction() {
    Optional<Post> postOpt = SehubManager.postOpt(getGemId());
    if (postOpt.isPresent()) {
      postOpt.get().setText(text, NotificationScope.LOCAL);
      return StatusResult.ok(getId());
    }
    else {
      GeneralErrorCollector.reportSyncError();
      return StatusResult.error(getId());
    }
  }
}


