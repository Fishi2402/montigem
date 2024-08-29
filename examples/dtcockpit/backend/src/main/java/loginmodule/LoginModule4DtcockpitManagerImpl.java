package loginmodule;

import dtcockpit.DtcockpitManager;
import dtcockpit.LoginModuleUser4UserBuilder;

public class LoginModule4DtcockpitManagerImpl extends LoginModule4DtcockpitManagerImplTOP {
  public static void init() {
    LoginModule4DtcockpitManagerImplTOP.init();
  }

  @Override
  protected LoginModuleUserBuilder _doLoginModuleUserBuilderFromId(long gemId) {
    return new LoginModuleUser4UserBuilder(DtcockpitManager.userBuilder());
  }

  @Override
  protected LoginModuleUserDeSerBuilder _doLoginModuleUserDeSerBuilder() {
    throw new IllegalStateException(); // TODO: Delegate DeSers too
  }

}
