package gui.admin;

import kitchencompanion.Receipt;
import umlp.jsweet.extension.annotation.Component;

import java.util.function.Consumer;
@Component
public class UserPaymentStatus extends UserPaymentStatusTOP {

  @Override
  public String getInvoiceAmount(Receipt receipt) {
    return receipt.getAmountToPay() + " €";
  }

  @Override
  public void setPaidChecked(Boolean paid) {
    Receipt r = getReceipt();
    r.setAmountPaid(0.);
    getReceipt().setConfirmed(false);
    super.setPaidChecked(paid);
  }

  @Override
  public Consumer<Void> confirm() {
    return $ -> {
      if (!getReceipt().isConfirmed() && getReceipt().isPaid()) {
        if (getReceipt().getAmountPaid() >= getReceipt().getAmountToPay()) {
          double addedBalance = getReceipt().getAmountPaid() - getReceipt().getAmountToPay();
          getUser().setBalance(getUser().getBalance() + addedBalance);
        } else if (getReceipt().getAmountPaid() == 0) {
          getReceipt().setAmountPaid(getReceipt().getAmountToPay());
        }
        getReceipt().setConfirmed(true);
      }
    };
  }

  @Override
  public Consumer<Void> reset() {
    return $ -> {
      if (getReceipt().isConfirmed() && getReceipt().isPaid()) {
        if (getReceipt().getAmountPaid() >= getReceipt().getAmountToPay() &&
            !((getReceipt().getAmountPaid() == getReceipt().getPastBalance())
                && (getReceipt().getAmountPaid() == getReceipt().getSum() + getReceipt().getDebt()))) {
          double addedBalance = getReceipt().getAmountPaid() - getReceipt().getAmountToPay();
          getUser().setBalance(getUser().getBalance() - addedBalance);
          getReceipt().setPaid(false);
          setPaidChecked(false);
          getReceipt().setConfirmed(false);
        }
      }
    };
  }
}
