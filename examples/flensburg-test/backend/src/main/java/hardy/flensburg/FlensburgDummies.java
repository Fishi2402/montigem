package hardy.flensburg;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FlensburgDummies {

  private static FlensburgDummies instance;

  private FlensburgDummies() {
  }

  public static FlensburgDummies getInstance() {
    if (instance == null) {
      instance = new FlensburgDummies();
    }
    return instance;
  }

  /**
   * Simple dummy for a simple check that everything works
   */
  public void inventSimple() {
    // initiate a manager mock
    FlensburgMock.init();

    // some random unique number: used as an id for the object in "database". Doesn't need to be set
    // if this specific object is not of interest
    FlensburgMock.secretlyAdaptNextGemId(1000);
    // build account object
    Budget b = FlensburgManager.budgetBuilder()
        .name("My Budget")
        .category(Optional.of("some category"))
        .balance(10000L)
        .cur(Currency.DOLLAR)
        .build()
        .get();

    Account a = FlensburgManager.accountBuilder()
        .name("My Account")
        .category(Optional.of("Personal"))
        .build()
        .get();
  }

  /**
   * Complex dummy for testing derived classes, attributes and associations
   */
  public void inventComplex() {
    // initiate a manager mock
    FlensburgMock.init();

    FlensburgMock.secretlyAdaptNextGemId(1111);

    FlensburgMock.secretlyAdaptNextGemId(1112);
    Budget b1 = FlensburgManager.budgetBuilder()
        .name("Scientific workers")
        .category(Optional.of("Staff"))
        .balance(30000L)
        .cur(Currency.DOLLAR)
        .build()
        .get();

    // create account builder
    Account a1 = FlensburgManager.accountBuilder()
        .name("DFG Berlin")
        .category(Optional.of("DFG"))
        .tag(Optional.of("research"))
        .build()
        .get();

    FlensburgMock.secretlyAdaptNextGemId(1113);
    Budget b2 = FlensburgManager.budgetBuilder()
        .name("Material resources")
        .category(Optional.of("Material"))
        .balance(10000L)
        .cur(Currency.EURO)
        .curOpt(Optional.of(Currency.DOLLAR))
        .build()
        .get();

    Set<Budget> ba = new HashSet<>(Arrays.asList(b1, b2));
    a1.addAllBudgets(ba);

    FlensburgMock.secretlyAdaptNextGemId(2000);
    Transaction t1 = FlensburgManager.transactionBuilder()
        .amount(-400L)
        .date(LocalDate.of(2023, 1, 12))
        .build()
        .get();

    FlensburgMock.secretlyAdaptNextGemId(3000);
    Transaction t2 = FlensburgManager.transactionBuilder()
        .amount(300L)
        .date(LocalDate.of(2023, 1, 23))
        .build()
        .get();

    FlensburgMock.secretlyAdaptNextGemId(4000);
    Transaction t3 = FlensburgManager.transactionBuilder()
        .amount(100L)
        .date(LocalDate.of(2023, 2, 9))
        .build()
        .get();

    Set<Transaction> tb = new HashSet<>(Arrays.asList(t1, t2, t3));
    b1.addAllTransactions(tb);

    Invoice i1 = FlensburgManager.invoiceBuilder();
    i1.build().get();

    Set<Transaction> ti = new HashSet<>(Arrays.asList(t2, t3));
    i1.addAllExpenses(ti);
  }

}
