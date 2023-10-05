/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import sehub.RoleAccessPolicy;
import sehub.SehubRole;


public class User_setRole extends User_setRoleTOP {

  public User_setRole(long gemId, SehubRole role) {
    super(gemId, role);
  }

  @Override
  public boolean checkAccessPolicy() {
    RoleAccessPolicy accessPolicy = new RoleAccessPolicy();
    return accessPolicy.checkPermission(this);
  }
}


