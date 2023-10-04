/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import sehub.SehubManager;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.NotificationScope;

public class Post_delete extends Post_deleteTOP {

  public Post_delete(long gemId) {
    super(gemId);
  }

  public StatusResult doAction() {
    if (SehubManager.deletePost(getGemId(), NotificationScope.LOCAL)) {
      return StatusResult.ok(getId());
    } else {
      return StatusResult.error(getId());
    }
  }

  @Override
  public boolean checkAccessPolicy() {
    sehub.PostAccessPolicy accessPolicy = new sehub.PostAccessPolicy();
    return accessPolicy.checkWritePermission(this);
  }
}


