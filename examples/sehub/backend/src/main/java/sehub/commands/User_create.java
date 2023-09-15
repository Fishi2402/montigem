/* (c) https://github.com/MontiCore/monticore */
package sehub.commands;

import sehub.User;
import sehub.UserAccessPolicy;
import umlp.backendrte.command.StatusResult;

public class User_create extends User_createTOP {

  public User_create(User user) {
    super(user);
  }

  public StatusResult doRun() {
    UserAccessPolicy accessPolicy = new UserAccessPolicy();
    if(accessPolicy.checkPermission(this)){
      return super.doRun();
    }
    return StatusResult.error(getId());
  }


}


