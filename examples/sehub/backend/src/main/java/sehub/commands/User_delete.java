/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import sehub.UserAccessPolicy;
import umlp.backendrte.command.StatusResult;

public class User_delete extends User_deleteTOP {

  public User_delete(long gemId) {
    super(gemId);
  }

  public StatusResult doRun() {
    UserAccessPolicy accessPolicy = new UserAccessPolicy();
    if (accessPolicy.checkDeletePermission(this)) {
      return super.doRun();
    }
    return StatusResult.error(getId());
  }

}


