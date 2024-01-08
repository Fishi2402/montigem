/* (c) https://github.com/MontiCore/monticore */
package guidslpaper.commands;

import guidslpaper.Role;
import guidslpaper.RoleAccessPolicy;


public class User_setRole extends User_setRoleTOP {

  public User_setRole(long gemId, Role role) {
    super(gemId, role);
  }

  @Override
  public boolean checkAccessPolicy() {
    RoleAccessPolicy accessPolicy = new RoleAccessPolicy();
    return accessPolicy.checkPermission(this);
  }
}


