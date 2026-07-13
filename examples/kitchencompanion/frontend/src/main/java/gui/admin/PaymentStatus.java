package gui.admin;

import app.AppRouter;
import gui.SearchProvider;
import kitchencompanion.KitchenCompanionManager;
import kitchencompanion.Receipt;
import kitchencompanion.Search;
import kitchencompanion.User;
import umlp.guiutil.Router;

import java.time.LocalDate;
import java.util.List;

public class PaymentStatus extends PaymentStatusTOP {

  @Override
  public Boolean hasReceipt(User user) {
    if (!user.getReceiptsList().isEmpty()) {
      LocalDate date = LocalDate.now().minusMonths(1);
      return user.getReceipts(0).getDate().getMonth() == date.getMonth() && user.getReceipts(0).getDate().getYear() == date.getYear();
    }
    return false;
  }

  @Override
  public Receipt getReceipt(User user) {
    return user.getReceipts(0);
  }
  @Override
  public Boolean hasAny1Receipt() {
    for (User u : getUsers()) {
      if (hasReceipt(u)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void init() {
    super.init();
    if (!LoginServiceFront.getLoggedIn()) {
      AppRouter.navigateToGuiAdminLoginPage();
    }
  }

  @Override
  protected void initUsers() {
    super.initUsers();
    setUsers(User.getUsersList());
  }

  @Override
  public void destroy() {
    super.destroy();
    KitchenCompanionManager.deleteSearch(SearchProvider.getSearch().getGemId());
    SearchProvider.search = null;
    System.out.println("Search state has been destroyed");
  }


}
