/* (c) https://github.com/MontiCore/monticore */
package dtcockpit.commands;

import dtcockpit.RoleAccessPolicy;
import dtcockpit.DtcockpitRole;


public class User_setRole extends User_setRoleTOP {

  public User_setRole(long gemId, DtcockpitRole role) {
    super(gemId, role);
  }

  @Override
  public boolean checkAccessPolicy() {
    RoleAccessPolicy accessPolicy = new RoleAccessPolicy();
    return accessPolicy.checkPermission(this);
  }
}


