package gui.admin;
import app.AppRouter;
import kitchencompanion.KitchenCompanionManager;
import kitchencompanion.User;
import kitchencompanion.commands.SendEmail;
import umlp.backendrte.command.CommandManager;
import umlp.guiutil.Router;

import java.util.List;
import java.util.function.Consumer;

public class Admin extends AdminTOP {

  @Override
  public void init() {
    super.init();
    if(!LoginServiceFront.getLoggedIn()){
      AppRouter.navigateToGuiAdminLoginPage();
    }
  }
  @Override
  public Consumer<Void> endMonth() {
    return $ ->{
      for(User user : KitchenCompanionManager.getUserList()){
        user.endMonth();
      }
    };
  }

  /**
   * @param users This Method sends an email to any user
   */
  @Override
  public Consumer<Void> sendMail() {
    return $ -> {
      CommandManager.executeCommand(new SendEmail());
      AppRouter.navigateToGuiAdminAdmin();
    };
  }
}
