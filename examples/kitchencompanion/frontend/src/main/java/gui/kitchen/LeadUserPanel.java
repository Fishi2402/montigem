package gui.kitchen;

import app.AppRouter;
import kitchencompanion.KitchenCompanionRole;
import kitchencompanion.Search;
import kitchencompanion.User;
import umlp.guiutil.Router;

import java.util.function.Consumer;

public class LeadUserPanel extends LeadUserPanelTOP {
  @Override
  public Boolean search(Search searchState, User user) {
    return Search.searchLead(searchState, user);
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
