/* (c) https://github.com/MontiCore/monticore */
package finanzverwaltung.commands;

import finanzverwaltung.FinanzverwaltungManager;
import finanzverwaltung.UserAccessPolicy;
import umlp.backendrte.command.StatusResult;
import umlp.backendrte.common.NotificationScope;

public class User_delete extends User_deleteTOP {

  public User_delete(long gemId) {
    super(gemId);
  }

  public StatusResult doAction() {
    if (FinanzverwaltungManager.deleteUser(getGemId(), NotificationScope.LOCAL)) {
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


