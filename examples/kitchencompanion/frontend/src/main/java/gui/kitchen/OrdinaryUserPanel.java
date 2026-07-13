package gui.kitchen;

import app.AppRouter;
import kitchencompanion.KitchenCompanionRole;
import kitchencompanion.Search;
import kitchencompanion.User;
import umlp.guiutil.Router;

import java.util.function.Consumer;

public class OrdinaryUserPanel extends OrdinaryUserPanelTOP {

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
  public Boolean search(Search searchState, User val_$_$1) {
    return Search.searchOrdinary(searchState, val_$_$1);
  }
  @Override
  public String getName(User user){
    return user.getFormatedName();
  }
}
