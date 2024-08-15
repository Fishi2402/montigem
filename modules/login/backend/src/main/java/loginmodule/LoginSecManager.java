package loginmodule;

import umlp.backendrte.common.Gem;
import umlp.backendrte.common.NotificationScope;

public class LoginSecManager extends LoginSecManagerTOP {
  @Override
  public LoginModuleUser getSignedInUser() {
    throw new IllegalStateException("ONLY AVAILABLE IN FRONTEND!");
  }

  @Override
  public Gem<LoginModuleUser> getSignedInUserGem() {
    return new Gem<>();
  }

  @Override
  public void setSignedInUser(LoginModuleUser _signedInUser, NotificationScope scope) {
    throw new IllegalStateException("ONLY AVAILABLE IN FRONTEND!");
  }
}
