/* (c) https://github.com/MontiCore/monticore */
package dtcockpit.commands;

import dtcockpit.DtcockpitManager;
import dtcockpit.UserAccessPolicy;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.NotificationScope;

public class User_delete extends User_deleteTOP {

  public User_delete(long gemId) {
    super(gemId);
  }

  public StatusResult doAction() {
    if (DtcockpitManager.deleteUser(getGemId(), NotificationScope.LOCAL)) {
      return StatusResult.ok(getId());
    } else {
      return StatusResult.error(getId());
    }
  }

  @Override
  public boolean checkAccessPolicy() {
    UserAccessPolicy accessPolicy = new UserAccessPolicy();
    return accessPolicy.checkDeletePermission(this);
  }
}


