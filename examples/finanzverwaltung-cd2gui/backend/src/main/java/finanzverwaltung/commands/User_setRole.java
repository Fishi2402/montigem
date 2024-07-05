/* (c) https://github.com/MontiCore/monticore */
package finanzverwaltung.commands;

import finanzverwaltung.Role;
import finanzverwaltung.RoleAccessPolicy;


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


