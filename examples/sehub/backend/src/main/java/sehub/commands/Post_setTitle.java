/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import sehub.Post;
import sehub.PostAccessPolicy;
import sehub.SehubManager;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.GeneralErrorCollector;
import umlp.backendrte.common.NotificationScope;

import java.util.Optional;

public class Post_setTitle extends Post_setTitleTOP {

  public Post_setTitle(long gemId, String title) {
    super(gemId, title);
  }

  public StatusResult doRun() {
    PostAccessPolicy accessPolicy = new PostAccessPolicy();
    if (accessPolicy.checkWritePermission(this)) {
      Optional<Post> postOpt = SehubManager.postOpt(getGemId());
      if (postOpt.isPresent()) {
        postOpt.get().setTitle(title, NotificationScope.LOCAL);
        return StatusResult.ok(getId());
      } else {
        GeneralErrorCollector.reportSyncError();
        return StatusResult.error(getId());
      }
    } else {
      return StatusResult.error(getId());
    }

  }

}


