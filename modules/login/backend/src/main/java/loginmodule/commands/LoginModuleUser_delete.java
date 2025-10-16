package loginmodule.commands;

import loginmodule.LoginModuleUser;
import umlp.backendrte.command.StatusResult;
import umlp.jsweet.extension.annotation.BackendOnly;
import umlp.jsweet.extension.annotation.BackendOnlyModes;

public class LoginModuleUser_delete extends LoginModuleUser_deleteTOP {
  public LoginModuleUser_delete(long gemId) {
    super(gemId);
  }

  @BackendOnly(BackendOnlyModes.THROW_ERROR)
  @Override
  public StatusResult doAction() {
    return super.doAction();
  }
}
