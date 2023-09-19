/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import sehub.RoleAccessPolicy;
import sehub.SehubRole;
import umlp.backendrte.command.StatusResult;


public class User_setRole extends User_setRoleTOP {

  public User_setRole(long gemId, SehubRole role) {
    super(gemId, role);
  }

  @Override
  public StatusResult doRun() {
    RoleAccessPolicy accessPolicy = new RoleAccessPolicy();
    if(accessPolicy.checkPermission(this)){
      return super.doRun();
    }
    return StatusResult.error(getId());
  }

}


