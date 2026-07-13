package gui.kitchen;

import app.AppRouter;
import kitchencompanion.Search;
import kitchencompanion.User;
import umlp.guiutil.Router;

import java.util.function.Consumer;

public class ExtraUserPanel extends ExtraUserPanelTOP {

  @Override
  public Boolean search(Search searchState, User user) {
    return Search.searchExtra(searchState, user);
  }

  @Override
  public Boolean searchHiwi(Search searchState, User user) {
    return Search.searchHiwi(searchState, user);
  }

  @Override
  public Consumer<Void> navigateToUser(User user, Boolean adminPage) {
    return $ -> {
      if(!adminPage) {
        AppRouter.navigateToGuiManagementOfferOverview(user.getGemId());
      }else{
        AppRouter.navigateToGuiAdminUserEdit(user.getGemId());
      }
    };
  }
  @Override
  public String getName(User user){
    return user.getFormatedName();
  }
}
