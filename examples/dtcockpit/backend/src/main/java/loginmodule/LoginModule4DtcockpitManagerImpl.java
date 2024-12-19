package loginmodule;

import dtcockpit.DtcockpitManager;
import dtcockpit.LoginModuleUser4UserBuilder;
import dtcockpit.LoginModuleUser4UserDelegatedBuilder;

public class LoginModule4DtcockpitManagerImpl extends LoginModule4DtcockpitManagerImplTOP {
  public static void init() {
    LoginModule4DtcockpitManagerImplTOP.init();
  }

  @Override
  protected LoginModuleUserBuilder _doLoginModuleUserBuilderFromId(long gemId) {
    return new LoginModuleUser4UserDelegatedBuilder(DtcockpitManager.userBuilder());
  }

  @Override
  protected LoginModuleUserDeSerBuilder _doLoginModuleUserDeSerBuilder() {
    throw new IllegalStateException(); // TODO: Delegate DeSers too
  }

}
