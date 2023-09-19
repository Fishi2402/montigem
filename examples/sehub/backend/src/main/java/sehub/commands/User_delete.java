/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import sehub.SehubManager;
import sehub.UserAccessPolicy;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.NotificationScope;

public class User_delete extends User_deleteTOP {

  public User_delete(long gemId) {
    super(gemId);
  }

  public StatusResult doRun() {
    UserAccessPolicy accessPolicy = new UserAccessPolicy();
    if (accessPolicy.checkDeletePermission(this)) {
      if (SehubManager.deleteUser(getGemId(), NotificationScope.LOCAL)) {
        return StatusResult.ok(getId());
      }
      else {
        return StatusResult.error(getId());
      }
    }
    return StatusResult.error(getId());
  }

}


