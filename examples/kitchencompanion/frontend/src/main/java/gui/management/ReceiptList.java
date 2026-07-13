package gui.management;

import kitchencompanion.Receipt;
import kitchencompanion.User;
import umlp.guiutil.Router;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ReceiptList extends ReceiptListTOP {

  @Override
  public String getDate(Receipt val_$_$0) {
    String[] dateYearMonth = val_$_$0.getDate().toString().split("-");
    return dateYearMonth[1] + " - " + dateYearMonth[0];
  }
}
